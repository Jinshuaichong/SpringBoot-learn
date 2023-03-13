package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

/**
 * @author 16086
 * @version 1.0
 * @description 课程管理service
 * @date 2023/1/27 14:16
 */

public interface CourseBaseInfoService {

    /**
     * @MethodName queryCourseBaseList
     * @Description 分页查询结果
     * @param companyId 机构id
     * @param params 分页参数
     * @param queryCourseParamsDto 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.content.model.po.CourseBase>
     * @Author Metty
     * @Date 2023/3/13 20:53
    */
    public PageResult<CourseBase> queryCourseBaseList(Long companyId,PageParams params, QueryCourseParamsDto queryCourseParamsDto);
	
	/**
	 * 新增课程
	 * @param companyId 机构id
	 * @param addCourseDto 新增的课程参数
	 * @return  课程的基本信息 营销信息
	 */
	public CourseBaseInfoDto createCourseBase(Long companyId,AddCourseDto addCourseDto);
	
	/**
	 * 获取课程信息接口
	 * @param courseID 课程id
     */
	public CourseBaseInfoDto getCourseBaseInfo(Long courseID);
	
	/**
	 * 修改课程基本信息
	 * @param companyId 机构id 只能修改本机构的课程
	 * @param dto 修改的参数
	 * @return 课程的基本信息 营销信息
	 */
	public CourseBaseInfoDto updateCourseBaseInfo(Long companyId, EditCourseDto dto);
	
}
