package com.xuecheng.content.service.jobHandler;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author Mr.M
 * @version 1.0
 * @Description 课程发布任务
 * @Date 2022/10/17 17:11
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {
	
	@Resource
	CoursePublishService coursePublishService;
	
	//课程发布任务执行入口，由xxl-job调度
	@XxlJob("CoursePublishJobHandler")
	public void coursePublishJobHandler() throws Exception {
		// 分片参数
		int shardIndex = XxlJobHelper.getShardIndex();
		int shardTotal = XxlJobHelper.getShardTotal();
		log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
		//参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
		process(shardIndex,shardTotal,"course_publish",5,60);
	}
	
	
	//课程发布执行逻辑
	@Override
	public boolean execute(MqMessage mqMessage) {
		
		log.debug("开始执行课程发布任务,课程id:{}",mqMessage.getBusinessKey1());
		
		//课程id
		long courseId = Long.parseLong(mqMessage.getBusinessKey1());
		
		//将课程信息进行静态化...
		generateCourseHtml(mqMessage,courseId);
		//将静态页面上传到minIO
		
		//将课程信息储存到索引库
		saveCourseIndex(mqMessage,courseId);
		//存储到redis
		
		return true;
	}
	
	/**
	 * @MethodName generateCourseHtml
	 * @Description 将课程信息页面进行静态化
	 * @param mqMessage 消息
	 * @param courseId 课程id
	 * @Author Metty
	 * @Date 2023/3/4 16:35
	*/
	private void generateCourseHtml(MqMessage mqMessage,long courseId){
		log.debug("开始进行课程页面静态化:{}",courseId);
		//消息id
		Long id = mqMessage.getId();
		MqMessageService mqMessageService = this.getMqMessageService();
		//判断任务是否完成
		int stageOne = mqMessageService.getStageOne(id);
		if(stageOne>0){
			log.debug("当前阶段是静态化课程信息已经完成不在处理,任务信息:{}",mqMessage);
			return ;
		}
		
		//生成静态文件
		File file = coursePublishService.generateCourseHtml(courseId);
		if(file==null){
			XueChengPlusException.cast("课程静态化发生异常");
		}
		//将静态文件上传到minio
		coursePublishService.uploadCourseHtml(courseId,file);
		//保存第一阶段状态
		mqMessageService.completedStageOne(courseId);
	}
	
	/**
	 * @MethodName saveCourseIndex
	 * @Description 创建课程索引
	 * @param mqMessage 消息
	 * @param courseId 课程id
	 * @Author Metty
	 * @Date 2023/3/4 16:36
	*/
	private void saveCourseIndex(MqMessage mqMessage,Long courseId){
		log.debug("开始建立课程索引:{}",courseId);
		//消息id
		Long id = mqMessage.getId();
		//判断任务是否完成
		int stageTwo = this.getMqMessageService().getStageTwo(id);
		if(stageTwo>0){
			log.debug("当前课程已添加索引,无需再次添加,任务信息:{}",mqMessage);
			return ;
		}
		coursePublishService.saveCourseIndex(courseId);
		
		//保存第一阶段状态
		this.getMqMessageService().completedStageTwo(courseId);
	}
}
