package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author 16086
 * @version 1.0
 * @description 课程分类操作相关
 * @date 2023/1/29 16:24
 */

public interface CourseCategoryService {
	/**
	 *
	 * @param id 根节点id
	 * @return 根节点下边所有的子节点
	 */
 
     List<CourseCategoryTreeDto> queryTreeNodes(String id);
     
}
