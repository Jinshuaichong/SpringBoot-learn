package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachPlanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * @author 16086
 * @version 1.0
 * @description TODO
 * @date 2023/2/3 17:02
 */


public interface TeachPlanService {
	
	/**
	 * 查询课程计划树
	 * @param courseId 课程id
	 * @return 课程计划树
	 */
	public List<TeachplanDto> findTeachPlanTree(Long courseId);
	
	/**
	 * 保存课程计划  包括新增和修改
	 * @param dto
	 */
	public void saveTeachPlan(SaveTeachplanDto dto);
	
	/***
	 * @MethodName associationMedia
	 * @Description 教学计划绑定媒资
	 * @param bindTeachPlanMediaDto
	 * @return com.xuecheng.content.model.po.TeachplanMedia
	 * @Author Metty
	 * @Date 2023/2/21 16:36
	*/
	public TeachplanMedia associationMedia(BindTeachPlanMediaDto bindTeachPlanMediaDto);
}
