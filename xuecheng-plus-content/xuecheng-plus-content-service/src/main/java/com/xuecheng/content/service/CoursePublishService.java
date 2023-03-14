package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CoursePublish;

import java.io.File;

/**
 * @Description TODO
 * @Date 2023/2/25,14:50
 * @Author Metty
 * @Version 1.0
 */

public interface CoursePublishService {
	
	/***
	 * @MethodName getCoursePreviewInfo
	 * @Description 获取课程预览信息
	 * @param courseId 课程id
	 * @return com.xuecheng.content.model.dto.CoursePreviewDto
	 * @Author Metty
	 * @Date 2023/2/25 14:51
	*/
	CoursePreviewDto getCoursePreviewInfo(Long courseId);
	
	/***
	 * @MethodName commitAudit
	 * @Description 课程审核
	 * @param companyId 机构id
	 * @param courseId 课程id
	 * @Author Metty
	 * @Date 2023/2/27 15:49
	*/
	void commitAudit(Long companyId,Long courseId);
	
	/***
	 * @MethodName publish
	 * @Description 课程发布
	 * @param companyId 机构id
	 * @param courseId 课程id
	 * @Author Metty
	 * @Date 2023/2/27 20:28
	*/
	void publish(Long companyId,Long courseId);
	
	/**
	 * @MethodName generateCourseHtml
	 * @Description 课程静态化
	 * @param courseId 课程id
	 * @return java.io.File
	 * @Author Metty
	 * @Date 2023/3/1 15:44
	*/
	public File generateCourseHtml(Long courseId);
	
	/**
	 * @MethodName uploadCourseHtml
	 * @Description 上传课程静态化页面
	 * @param courseId 课程id
	 * @param file 静态文件
	 * @Author Metty
	 * @Date 2023/3/1 15:45
	*/
	public void  uploadCourseHtml(Long courseId,File file);
	
	/**
	 * @MethodName saveCourseIndex
	 * @Description 创建课程索引
	 * @param courseId 课程id
	 * @return java.lang.Boolean
	 * @Author Metty
	 * @Date 2023/3/4 16:42
	*/
	public Boolean saveCourseIndex(Long courseId);

    /**
     * @MethodName getCoursePublish
     * @Description 根据课程id查询课程发布信息
     * @param courseId 课程id
     * @return com.xuecheng.content.model.po.CoursePublish
     * @Author Metty
     * @Date 2023/3/14 13:47
    */
    public CoursePublish getCoursePublish(Long courseId);
	
}
