package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Description 课程基本信息相关controller
 * @author Mr.M
 * @Date 2022/10/7 16:22
 * @version 1.0
 */
@Api(value = "课程管理接口",tags = "课程管理接口")
@RestController
public class CourseBaseInfoController {
    @Resource
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")//指定权限标识符
    public PageResult<CourseBase> list(PageParams params, @RequestBody QueryCourseParamsDto queryCourseParamsDto){
        //调用service获取数据
	    return courseBaseInfoService.queryCourseBaseList(params, queryCourseParamsDto);
    }
	
	@ApiOperation("新增课程接口")
	@PostMapping("/course")
	public CourseBaseInfoDto creatCourseBase(@RequestBody @Validated AddCourseDto addCourseDto){
		
		//获取当前用户所属培训机构的id
		Long companyId = 22L;
		//调用service
		return courseBaseInfoService.createCourseBase(companyId, addCourseDto);
	}
	
	@ApiOperation("根据课程id查询课程基本信息")
	@GetMapping("/course/{courseId}")
	public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){
		//获取当前用户信息
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		System.out.println(principal);
		
		return courseBaseInfoService.getCourseBaseInfo(courseId);
	}
	
	@ApiOperation("修改课程基本信息")
	@PutMapping("/course")
	public CourseBaseInfoDto modifyCourseBase(@RequestBody EditCourseDto dto){
		
		Long companyId = 1232141425L;
		return courseBaseInfoService.updateCourseBaseInfo(companyId, dto);
	}

}
