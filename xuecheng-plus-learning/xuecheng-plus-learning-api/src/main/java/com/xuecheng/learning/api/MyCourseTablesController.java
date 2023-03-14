package com.xuecheng.learning.api;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.service.MyCourseTablesService;
import com.xuecheng.learning.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description 我的课程表接口
 * @author Mr.M
 * @date 2022/10/25 9:40
 * @version 1.0
 */

 @Api(value = "我的课程表接口", tags = "我的课程表接口")
 @Slf4j
 @RestController
 public class MyCourseTablesController {

    @Autowired
    MyCourseTablesService myCourseTablesService;


    @ApiOperation("添加选课")
    @PostMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId) {
        //当前登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user==null){
            XueChengPlusException.cast("请登录");
        }
        //用户id
        String userId = user.getId();
        //调用service添加选课
        XcChooseCourseDto xcChooseCourseDto = myCourseTablesService.addChooseCourse(userId, courseId);

        return xcChooseCourseDto;
    }


 }
