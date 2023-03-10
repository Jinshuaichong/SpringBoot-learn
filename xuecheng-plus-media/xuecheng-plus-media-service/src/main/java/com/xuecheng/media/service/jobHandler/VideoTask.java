package com.xuecheng.media.service.jobHandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @Date 2023/2/20,14:58
 * @Author Metty
 * @Version 1.0
 */
@Component
@Slf4j
public class VideoTask {
	
	@Resource
	MediaFileProcessService mediaFileProcessService;
	
	@Resource
	MediaFileService mediaFileService;
	
	@Value("${videoprocess.ffmpegpath}")
	String ffmpegpath;
	
	
	
	@SuppressWarnings("AlibabaThreadPoolCreation")
	@XxlJob("videoJobHandler")
	public void shardingJobHandler() throws Exception {
		
		// 分片序号 0开始
		int shardIndex = XxlJobHelper.getShardIndex();
		// 分片总数
		int shardTotal = XxlJobHelper.getShardTotal();
		//查询待处理任务 一次处理的任务数和cpu核心数一样
		List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal,2);
		if(mediaProcessList==null||mediaProcessList.size()<=0){
			log.debug("查询到的待处理的视频任务为0");
		}
		//要处理的任务数
		int size = mediaProcessList.size();
		//启动多线程去处理 创建个size数量的线程池
		ExecutorService threadPool = Executors.newFixedThreadPool(size);
		//计数器
		CountDownLatch countDownLatch=new CountDownLatch(size);
		//便利mediaProcessList，将任务放入线程池
		mediaProcessList.forEach(mediaProcess -> {
			threadPool.execute(()->{
				//视频状态
				String status=mediaProcess.getStatus();
				if("2".equals(status)){
					log.debug("视频已经处理,无需再次处理,视频信息:{}",mediaProcess);
					countDownLatch.countDown();
					return;
				}
				//桶
				String bucket=mediaProcess.getBucket();
				//存储路径
				String filePath=mediaProcess.getFilePath();
				//原始md5
				String fileId=mediaProcess.getFileId();
				//原始文件名
				String fileName=mediaProcess.getFilename();
				//创建临时文件
				File originalFile=null;
				File mp4File=null;
				try {
					originalFile=File.createTempFile("original",null);
					mp4File=File.createTempFile("mp4",".mp4");
					
				}catch (IOException e){
					log.error("处理视频前创建临时文件出错");
					countDownLatch.countDown();
					return;
				}
				//将原始文件下载到本地
				try {
					mediaFileService.downloadFileFromMinIO(originalFile,bucket,filePath);
				} catch (Exception e) {
					log.error("下载原始文件过程中出错:{},文件信息:{}",e.getMessage(),mediaProcess);
					countDownLatch.countDown();
					return;
				}
				//调用工具类将avi转为mp4
				//转换后mp4文件的名称
				String mp4_name = fileId+".mp4";
				//转换后mp4文件的路径
				String mp4_path = mp4File.getAbsolutePath();
				//创建工具类对象
				Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegpath,originalFile.getAbsolutePath(),mp4_name,mp4_path);
				//开始视频转换，成功将返回success
				String result = videoUtil.generateMp4();
				String statusNew="3";
				String url=null;
				if("success".equals(result)){
					//转换成功 上传到minIO
					String objectName = getFilePathByMd5(fileId, ".mp4");
					try {
						mediaFileService.addMediaFilesToMinIO(mp4_path,bucket,objectName);
					} catch (Exception e) {
						log.error("上传文件出错:{}",e.getMessage());
						countDownLatch.countDown();
						return;
					}
					statusNew="2";
					url="/"+bucket+"/"+objectName;
				}
				
				
				try {
					//记录任务处理结果到db
					mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(),statusNew,fileId,url,result);
				} catch (Exception e) {
					log.error("保存任务处理结果出错:{}",e.getMessage());
					countDownLatch.countDown();
					return;
				}
				//计数器-1
				countDownLatch.countDown();
			});
		});
		
		//阻塞到任务完成 当计数器归零 阻塞解除
		countDownLatch.await(30, TimeUnit.MINUTES);
	
	}
	private String getFilePathByMd5(String fileMd5,String fileExt){
		return fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
	}
}
