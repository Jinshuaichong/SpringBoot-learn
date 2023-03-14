package com.xuecheng.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.xuecheng.learning.mapper.XcCourseTablesMapper;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.model.po.XcCourseTables;
import com.xuecheng.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/10/25 9:43
 */
@Slf4j
@Service
public class MyCourseTablesServiceImpl implements MyCourseTablesService {

    @Autowired
    ContentServiceClient contentServiceClient;


    //选课记录表的mapper
    @Autowired
    XcChooseCourseMapper chooseCourseMapper;


    //我的课程表mapper
    @Autowired
    XcCourseTablesMapper courseTablesMapper;

    //添加选课
    @Override
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId) {
        //选课调用内容管理查询课程的收费规则
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if(coursepublish==null){
            XueChengPlusException.cast("课程不存在");
        }
        //收费规则
        String charge = coursepublish.getCharge();
        if("201000".equals(charge)){
            //如果课程免费 就向选课记录表写数据
            XcChooseCourse xcChooseCourse = addFreeCourse(userId, coursepublish);
            //我的课程表写数据
            XcCourseTables xcCourseTables = addCourseTables(xcChooseCourse);

        }else {
            //如果收费 会向选课记录表写数据
            XcChooseCourse xcChooseCourse = addChargeCourse(userId, coursepublish);
        }




        //判断学生的学习资格

        return null;
    }


    /**
     * @MethodName addFreeCourse
     * @Description 添加免费课程到选课记录表 我的课程表
     * @param userId 用户id
     * @param coursePublish 课程发布信息
     * @return com.xuecheng.learning.model.po.XcChooseCourse
     * @Author Metty
     * @Date 2023/3/14 15:30
    */
    public XcChooseCourse addFreeCourse(String userId,CoursePublish coursePublish){
        Long courseId=coursePublish.getId();
        //如果存在免费的选课记录且选课状态为成功,直接返回
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<XcChooseCourse>().eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, courseId)
                .eq(XcChooseCourse::getOrderType, "700001")//免费课程
                .eq(XcChooseCourse::getStatus, "701001");//选课成功
        List<XcChooseCourse> xcChooseCourses = chooseCourseMapper.selectList(queryWrapper);
        if(xcChooseCourses.size()>0){
            return xcChooseCourses.get(0);
        }

        //向选课记录表写数据
        XcChooseCourse chooseCourse = new XcChooseCourse();
        chooseCourse.setCourseId(courseId);
        chooseCourse.setCourseName(coursePublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursePublish.getCompanyId());
        chooseCourse.setOrderType("700001");
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursePublish.getPrice());
        chooseCourse.setValidDays(365);
        chooseCourse.setStatus("701001");
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));

        int insert = chooseCourseMapper.insert(chooseCourse);
        if(insert<=0){
            XueChengPlusException.cast("添加选课记录失败");
        }


        return chooseCourse;
    }

    /**
     * @MethodName addCourseTables
     * @Description 添加到我的课程表
     * @param xcChooseCourse 选择的课程
     * @return com.xuecheng.learning.model.po.XcCourseTables
     * @Author Metty
     * @Date 2023/3/14 15:31
    */
    public XcCourseTables addCourseTables(XcChooseCourse xcChooseCourse){
        //选课成功了才可以向我的课程表添加
        String status = xcChooseCourse.getStatus();
        if("701001".equals(status)){
            XueChengPlusException.cast("选课未成功,无法添加到课程表");
        }
        XcCourseTables xcCourseTables = getXcCourseTables(xcChooseCourse.getUserId(), xcChooseCourse.getCourseId());
        if(xcCourseTables!=null){
            return xcCourseTables;
        }
        xcCourseTables=new XcCourseTables();
        BeanUtils.copyProperties(xcChooseCourse,xcCourseTables);
        //记录选课表的主键
        xcCourseTables.setChooseCourseId(xcChooseCourse.getId());
        xcCourseTables.setCourseType(xcCourseTables.getCourseType());
        xcCourseTables.setUpdateDate(LocalDateTime.now());
        int insert = courseTablesMapper.insert(xcCourseTables);
        if(insert<=0){
            XueChengPlusException.cast("添加我的课程表失败");
        }
        return xcCourseTables;
    }

    /**
     * @MethodName addChargeCourse
     * @Description 添加收费课程
     * @param userId 用户id
     * @param coursePublish 课程发布信息
     * @return com.xuecheng.learning.model.po.XcChooseCourse
     * @Author Metty
     * @Date 2023/3/14 15:32
    */
    public XcChooseCourse addChargeCourse(String userId,CoursePublish coursePublish){
        Long courseId=coursePublish.getId();
        //如果存在收费的选课记录且选课状态为待支付,直接返回
        LambdaQueryWrapper<XcChooseCourse> queryWrapper = new LambdaQueryWrapper<XcChooseCourse>().eq(XcChooseCourse::getUserId, userId)
                .eq(XcChooseCourse::getCourseId, courseId)
                .eq(XcChooseCourse::getOrderType, "700002")//免费课程
                .eq(XcChooseCourse::getStatus, "701002");//待支付
        List<XcChooseCourse> xcChooseCourses = chooseCourseMapper.selectList(queryWrapper);
        if(xcChooseCourses.size()>0){
            return xcChooseCourses.get(0);
        }

        //向选课记录表写数据
        XcChooseCourse chooseCourse = new XcChooseCourse();
        chooseCourse.setCourseId(courseId);
        chooseCourse.setCourseName(coursePublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursePublish.getCompanyId());
        chooseCourse.setOrderType("700002");
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursePublish.getPrice());
        chooseCourse.setValidDays(365);
        //待支付
        chooseCourse.setStatus("701002");
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));

        int insert = chooseCourseMapper.insert(chooseCourse);
        if(insert<=0){
            XueChengPlusException.cast("添加选课记录失败");
        }


        return chooseCourse;
    }

    /**
     * @MethodName getXcCourseTables
     * @Description 根据课程和用户查询我的课程表中某一门课
     * @param userId 用户id
     * @param courseId 课程id
     * @return com.xuecheng.learning.model.po.XcCourseTables
     * @Author Metty
     * @Date 2023/3/14 16:00
    */
    public XcCourseTables getXcCourseTables(String userId,Long courseId){

        return courseTablesMapper.selectOne(new LambdaQueryWrapper<XcCourseTables>()
                .eq(XcCourseTables::getUserId, userId)
                .eq(XcCourseTables::getCourseId, courseId));

    }

}
