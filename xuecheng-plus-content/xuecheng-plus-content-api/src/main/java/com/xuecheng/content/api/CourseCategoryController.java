package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 16086
 * @version 1.0
 * @description TODO
 * @date 2023/1/28 19:35
 */

@Slf4j
@Api(value = "课程分类相关接口",tags = "课程分类相关接口")
@RestController
public class CourseCategoryController {
	
	@Resource
	CourseCategoryService courseCategoryService;
	@ApiOperation("课程分类查询接口")
	@GetMapping("/course-category/tree-nodes")
	public List<CourseCategoryTreeDto> queryTreeNodes(){
		List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes("1");
		return courseCategoryTreeDtos;
	}
}
