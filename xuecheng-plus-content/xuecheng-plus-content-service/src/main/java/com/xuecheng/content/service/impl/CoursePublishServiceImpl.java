package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.feignclient.SearchServiceClient;
import com.xuecheng.content.feignclient.model.CourseIndex;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachPlanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 课程发布服务
 * @Date 2023/2/25,14:52
 * @Author Metty
 * @Version 1.0
 */
@Service
@Slf4j
public class CoursePublishServiceImpl implements CoursePublishService {
	
	@Resource
	CourseBaseInfoService courseBaseInfoService;
	
	@Resource
	TeachPlanService teachPlanService;
	
	@Resource
	CourseBaseMapper courseBaseMapper;
	
	@Resource
	CourseMarketMapper courseMarketMapper;
	
	@Resource
	CoursePublishPreMapper coursePublishPreMapper;
	
	@Resource
	CoursePublishMapper coursePublishMapper;
	
	@Resource
	MqMessageService mqMessageService;
	
	@Resource
	MediaServiceClient mediaServiceClient;
	
	@Resource
	SearchServiceClient searchServiceClient;
	
	
	
	
	
	
	@Override
	public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
		
		//基本信息 营销信息
		CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
		
		//教学计划
		List<TeachplanDto> teachPlanTree = teachPlanService.findTeachPlanTree(courseId);
		
		CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
		coursePreviewDto.setCourseBase(courseBaseInfo);
		coursePreviewDto.setTeachplans(teachPlanTree);
		return coursePreviewDto;
	}
	
	@Override
	public void commitAudit(Long companyId, Long courseId) {
		//约束校验
		CourseBase courseBase = courseBaseMapper.selectById(courseId);
		//课程审核状态
		String auditStatus = courseBase.getAuditStatus();
		//当前审核状态为已提交不允许再次提交
		if("202003".equals(auditStatus)){
			XueChengPlusException.cast("当前为等待审核状态，审核完成可以再次提交。");
		}
		//本机构只允许提交本机构的课程
		if(!courseBase.getCompanyId().equals(companyId)){
			XueChengPlusException.cast("不允许提交其它机构的课程。");
		}
		
		//课程图片是否填写
		if(StringUtils.isEmpty(courseBase.getPic())){
			XueChengPlusException.cast("提交失败，请上传课程图片");
		}
		
		//查询课程计划信息
		List<TeachplanDto> teachPlanTree = teachPlanService.findTeachPlanTree(courseId);
		if (teachPlanTree.size() == 0){
			XueChengPlusException.cast("提交失败,还没有添加课程计划");
		}
		
		//封装数据,基本信息/营销信息/课程计划信息/师资信息
		CoursePublishPre coursePublishPre = new CoursePublishPre();
		//查询基本信息
		CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
		BeanUtils.copyProperties(courseBaseInfo,coursePublishPre);
		//将课程计划转为json
		String teachplanTreeJson = JSON.toJSONString(teachPlanTree);
		coursePublishPre.setTeachplan(teachplanTreeJson);
		//课程营销信息
		CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
		String courseMarketJson = JSON.toJSONString(courseMarket);
		coursePublishPre.setMarket(courseMarketJson);
		//课程预发布表初始审核状态
		coursePublishPre.setStatus("202003");
		CoursePublishPre findIncoursePublishPre = coursePublishPreMapper.selectById(courseId);
		if(findIncoursePublishPre==null){
			coursePublishPreMapper.insert(coursePublishPre);
		}else{
			coursePublishPreMapper.updateById(coursePublishPre);
		}
		
		//更新课程基本表的审核状态
		courseBase.setAuditStatus("202003");
		courseBaseMapper.updateById(courseBase);
		
		
	}
	
	@Transactional
	@Override
	public void publish(Long companyId, Long courseId) {
		//约束校验
		//查询课程预发布表
		CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
		if(coursePublishPre == null){
			XueChengPlusException.cast("请先提交课程审核，审核通过才可以发布");
		}
		//本机构只允许提交本机构的课程
		if(!coursePublishPre.getCompanyId().equals(companyId)){
			XueChengPlusException.cast("不允许提交其它机构的课程。");
		}
		
		
		//课程审核状态
		String auditStatus = coursePublishPre.getStatus();
		//审核通过方可发布
		if(!"202004".equals(auditStatus)){
			XueChengPlusException.cast("操作失败，课程审核通过方可发布。");
		}
		
		//保存课程发布信息
		saveCoursePublish(courseId);
		
		//保存消息表
		saveCoursePublishMessage(courseId);
		
		//删除课程预发布表对应记录
		coursePublishPreMapper.deleteById(courseId);
		
	}
	
	@Override
	public File generateCourseHtml(Long courseId) {
		//配置freemarker
		try {
			Configuration configuration = new Configuration(Configuration.getVersion());
			
			//加载模板
			//选指定模板路径,classpath下templates下
			//得到classpath路径
			String classpath = this.getClass().getResource("/").getPath();
			configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
			//设置字符编码
			configuration.setDefaultEncoding("utf-8");
			
			//指定模板文件名称
			Template template = configuration.getTemplate("course_template.ftl");
			
			//准备数据
			CoursePreviewDto coursePreviewInfo = getCoursePreviewInfo(courseId);
			
			Map<String, Object> map = new HashMap<>();
			map.put("model", coursePreviewInfo);
			
			//静态化
			//参数1：模板，参数2：数据模型
			String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
			System.out.println(content);
			//将静态化内容输出到文件中
			InputStream inputStream = IOUtils.toInputStream(content);
			//创建临时文件作为静态文件载体
			File htmlFile = File.createTempFile("course", ".html");
			log.debug("课程静态化,生成静态文件:{}",htmlFile.getAbsolutePath());
			
			//输出流
			FileOutputStream outputStream = new FileOutputStream(htmlFile);
			IOUtils.copy(inputStream, outputStream);
			return htmlFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void uploadCourseHtml(Long courseId, File file) {
		MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
		String result = mediaServiceClient.upload(multipartFile, "course", courseId+".html");
		if(result==null){
			XueChengPlusException.cast("远程调用媒资服务上传文件失败");
		}
	}
	
	@Override
	public Boolean saveCourseIndex(Long courseId) {
		//查询课程发布表的数据
		CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
		//远程调用搜索服务 创建索引
		CourseIndex courseIndex = new CourseIndex();
		BeanUtils.copyProperties(coursePublish,courseIndex);
		Boolean result = searchServiceClient.add(courseIndex);
		
		if(!result){
			XueChengPlusException.cast("创建课程索引失败");
			return false;
		}

		return true;
	}

    @Override
    public CoursePublish getCoursePublish(Long courseId) {
        return coursePublishMapper.selectById(courseId);
    }


    /***
	 * @MethodName saveCoursePublish
	 * @Description 保存课程发布信息
	 * @param courseId 课程id
	 * @Author Metty
	 * @Date 2023/2/27 20:31
	*/
	private void saveCoursePublish(Long courseId){
		CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
		CoursePublish coursePublish = new CoursePublish();
		//拷贝到课程发布对象
		BeanUtils.copyProperties(coursePublishPre,coursePublish);
		coursePublish.setStatus("203002");
		
		CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
		if(coursePublishUpdate==null){
			coursePublishMapper.insert(coursePublish);
		}else{
			coursePublishMapper.updateById(coursePublish);
		}
		//更新课程基本表的发布状态
		CourseBase courseBase = courseBaseMapper.selectById(courseId);
		courseBase.setStatus("203002");
		courseBaseMapper.updateById(courseBase);
		
	}
	
	/**
	 * @MethodName saveCoursePublishMessage
	 * @Description 保存消息记录表
	 * @param courseId 课程id
	 * @Author Metty
	 * @Date 2023/2/27 20:32
	*/
	private void saveCoursePublishMessage(Long courseId){
		MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
		if(mqMessage==null){
			XueChengPlusException.cast("添加消息记录失败");
		}
		
	}
	
	
}
