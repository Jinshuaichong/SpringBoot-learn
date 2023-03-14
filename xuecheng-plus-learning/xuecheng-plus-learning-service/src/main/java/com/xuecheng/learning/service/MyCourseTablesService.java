package com.xuecheng.learning.service;

import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;

/**
 * @author Mr.M
 * @version 1.0
 * @Description 我的课程表service
 * @Date 2022/10/25 9:41
 */
public interface MyCourseTablesService {



    /**
     * @MethodName addChooseCourse
     * @Description 添加选课
     * @param userId 用户id
     * @param courseId 课程id
     * @return com.xuecheng.learning.model.dto.XcChooseCourseDto
     * @Author Metty
     * @Date 2023/3/14 14:55
    */
    public XcChooseCourseDto addChooseCourse(String userId,Long courseId);

    /**
     * @MethodName getLearningStatus
     * @Description 查询用户对某一课程的学习状态
     * @param userId 用户id
     * @param courseId 课程id
     * @return com.xuecheng.learning.model.dto.XcCourseTablesDto
     * @Author Metty
     * @Date 2023/3/14 16:38
    */
    public XcCourseTablesDto getLearningStatus(String userId,Long courseId);


}
