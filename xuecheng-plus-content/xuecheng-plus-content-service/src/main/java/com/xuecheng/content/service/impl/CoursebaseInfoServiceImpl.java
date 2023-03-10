package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 16086
 * @version 1.0
 * @description TODO
 * @date 2023/1/27 14:21
 */

@Service
@Slf4j
public class CoursebaseInfoServiceImpl implements CourseBaseInfoService {

    @Resource
    CourseBaseMapper courseBaseMapper;
	
	@Resource
	CourseMarketMapper courseMarketMapper;
	
	@Resource
	CourseCategoryMapper courseCategoryMapper;
	
	@Resource
	CourseMarketServiceImpl courseMarketService;
	
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams params, QueryCourseParamsDto queryCourseParamsDto) {

        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();

        //拼接查询条件
        //根据课程名称模糊查询 name like
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName,queryCourseParamsDto.getCourseName());

        //根据课程审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());

        //根据课程发布状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());

        //分页参数
        Page<CourseBase> page = new Page<>(params.getPageNo(), params.getPageSize());
        //分页查询 E page 分页参数 @param("ew") Wrapper<T> queryWrapper 查询条件
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        //数据
        List<CourseBase> items = pageResult.getRecords();
        //总记录数
        long total = pageResult.getTotal();
        //准备返回数据
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items, total, params.getPageNo(), params.getPageSize());
		
        return courseBasePageResult;
    }
	
	@Transactional
	@Override
	public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
		//参数合法性校验  此部分已放到controller层使用jsr303校验
		//对数据进行封装 调用mapper进行数据持久化
		CourseBase courseBase = new CourseBase();
		//传入的课程参数写入courseBase对象 将dto中和courseBase属性名一样的属性值拷贝到courseBase
		BeanUtils.copyProperties(dto,courseBase);
		//设置机构的id
		courseBase.setCompanyId(companyId);
		//创建时间
		courseBase.setCreateDate(LocalDateTime.now());
		//审核状态默认为未提交 发布状态默认为未发布
		courseBase.setAuditStatus("202002");
		courseBase.setStatus("203001");
		//课程基本表插入一条记录
		int insert = courseBaseMapper.insert(courseBase);
		//获取课程id
		Long courseID = courseBase.getId();
		CourseMarket courseMarket = new CourseMarket();
		BeanUtils.copyProperties(dto,courseMarket);
		courseMarket.setId(courseID);
		//如果课程为收费 那么价格必须输入
		int insert1 = this.saveCourseMarket(courseMarket);
		//课程营销表插入一条记录
		//int insert1 = courseMarketMapper.insert(courseMarket);
		if(insert<=0||insert1<=0){
			log.error("创建课程过程出错:{}",dto);
			throw new RuntimeException("创建课程过程中出错");
		}
		//组装要返回的结果
		
		return getCourseBaseInfo(courseID);
	}
	
	@Override
	public CourseBaseInfoDto getCourseBaseInfo(Long courseID){
		//基本信息
		CourseBase courseBase = courseBaseMapper.selectById(courseID);
		//营销信息
		CourseMarket courseMarket = courseMarketMapper.selectById(courseID);
		
		CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
		BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
		if(courseMarket!=null){
			BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
		}
		//根据课程分类的id查询分类名称
		String mt = courseBase.getMt();
		String st = courseBase.getSt();
		CourseCategory mtCategory = courseCategoryMapper.selectById(mt);
		CourseCategory stCategory = courseCategoryMapper.selectById(st);
		if(mtCategory!=null){
			String mtName = mtCategory.getName();
			courseBaseInfoDto.setMtName(mtName);
		}
		if(stCategory!=null){
			String stName = stCategory.getName();
			courseBaseInfoDto.setStName(stName);
		}
		
		return courseBaseInfoDto;
	}
	
	@Transactional
	@Override
	public CourseBaseInfoDto updateCourseBaseInfo(Long companyId, EditCourseDto dto) {
		//校验
		Long id = dto.getId();
		CourseBase courseBase = courseBaseMapper.selectById(id);
		if(courseBase==null){
			XueChengPlusException.cast("课程不存在");
		}
		//判断课程是否属于当前机构
		if(!courseBase.getCompanyId().equals(companyId)){
			XueChengPlusException.cast("此课程不属于当前机构");
		}
		//封装基本信息的数据
		BeanUtils.copyProperties(dto,courseBase);
		courseBase.setChangeDate(LocalDateTime.now());
		//更新课程基本信息
		courseBaseMapper.updateById(courseBase);
		//封装营销信息的数据
		CourseMarket courseMarket = new CourseMarket();
		BeanUtils.copyProperties(dto,courseMarket);
		//如果课程为收费 那么价格必须输入
		saveCourseMarket(courseMarket);
		
		//请求数据库
		//对营销表有则更新 没有则添加
		
		//查询课程信息
		return this.getCourseBaseInfo(id);
	}
	
	//抽取对营销信息的保存
	private int saveCourseMarket(CourseMarket courseMarket){
		//如果课程为收费 那么价格必须输入
		String charge = courseMarket.getCharge();
		if(StringUtils.isBlank(charge)){
			XueChengPlusException.cast("未选择收费规则");
		}
		if(charge.equals("201001")){
			if(courseMarket.getPrice()==null||courseMarket.getPrice().floatValue()<=0){
				XueChengPlusException.cast("收费课程价格不能为空或者小于0");
				//throw new RuntimeException("收费课程价格不能为空");
			}
		}
		
		//对营销表有则更新 没有则添加
		boolean res = courseMarketService.saveOrUpdate(courseMarket);
		return res?1:0;
	}
}
