package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.BindTeachPlanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 16086
 * @version 1.0
 * @description TODO
 * @date 2023/2/3 16:16
 */

@Api(value = "课程计划管理", description = "课程计划管理", tags = {"课程计划管理"})
@RestController
@Slf4j
public class TeachplanController {
	
	@Resource
	TeachPlanService teachPlanService;
	
	
	@GetMapping("/teachplan/{courseId}/tree-nodes")
	public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId){
		return teachPlanService.findTeachPlanTree(courseId);
	}
	
	@PostMapping("/teachplan")
	public void saveTeachPlan(@RequestBody SaveTeachplanDto dto){
		teachPlanService.saveTeachPlan(dto);
	}
	
	@PostMapping("/teachplan/association/media")
	public void associationMedia(@RequestBody BindTeachPlanMediaDto bindTeachPlanMediaDto){
		teachPlanService.associationMedia(bindTeachPlanMediaDto);
	}
}
