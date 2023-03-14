package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * @Description TODO
 * @Date 2023/2/24,20:13
 * @Author Metty
 * @Version 1.0
 */
//freemarker返回的是页面 不是json 所以用controller注解
@Controller
@Api(value = "课程预览发布接口",tags = "课程预览发布接口")
public class CoursePublishController {
	
	@Resource
	CoursePublishService coursePublishService;
	
	@ApiOperation(value = "课程预览",tags = "课程预览")
	@GetMapping("/coursepreview/{courseId}")
	public ModelAndView preview(@PathVariable("courseId") Long courseId){
		//查询数据
		CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
		
		ModelAndView modelAndView = new ModelAndView();
		//准备模型数据
		modelAndView.addObject("model",coursePreviewInfo);
		//设置视图名称,就是模板文件的名称
		modelAndView.setViewName("course_template");
		return modelAndView;
	}
	
	//提交审核
	@ResponseBody
	@PostMapping("/courseaudit/commit/{courseId}")
	public void commitAudit(@PathVariable("courseId") Long courseId) {
		Long companyId = 1232141425L;
		coursePublishService.commitAudit(companyId, courseId);
	}
	
	@ApiOperation("课程发布")
	@ResponseBody
	@PostMapping("/coursepublish/{courseId}")
	public void coursepublish(@PathVariable("courseId") Long courseId){
		Long companyId=1232141425L;
		coursePublishService.publish(companyId,courseId);
	}


    @ApiOperation("查询课程发布信息")
    @ResponseBody
    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursepublish(@PathVariable("courseId") Long courseId){
        //查询课程发布信息
        return coursePublishService.getCoursePublish(courseId);


    }
	
	
}
