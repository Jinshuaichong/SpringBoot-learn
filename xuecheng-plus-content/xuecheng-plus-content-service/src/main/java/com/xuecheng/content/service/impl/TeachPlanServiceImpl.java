package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachPlanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
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
 * @date 2023/2/3 17:03
 */
@Service
@Slf4j
public class TeachPlanServiceImpl implements TeachPlanService {
	@Resource
	TeachplanMapper teachplanMapper;
	
	@Resource
	TeachplanMediaMapper teachplanMediaMapper;
	@Override
	public List<TeachplanDto> findTeachPlanTree(Long courseId) {
		
		return teachplanMapper.selectTreeNodes(courseId);
	}
	
	//实现课程的新增和修改
	@Override
	public void saveTeachPlan(SaveTeachplanDto dto) {
		Long id = dto.getId();
		
		Teachplan teachplan = teachplanMapper.selectById(id);
		
		if(teachplan==null) {
			teachplan = new Teachplan();
			BeanUtils.copyProperties(dto,teachplan);
			//找到统计课程计划的数量
			int teachPlanCount = getTeachPlanCount(dto.getCourseId(), dto.getParentid());
			//新的课程计划的值
			teachplan.setOrderby(teachPlanCount+1);
			
			teachplanMapper.insert(teachplan);
		}else {
			BeanUtils.copyProperties(dto,teachplan);
			teachplanMapper.updateById(teachplan);
		}
		
	}
	
	@Transactional
	@Override
	public TeachplanMedia associationMedia(BindTeachPlanMediaDto bindTeachPlanMediaDto) {
		Long teachPlanId = bindTeachPlanMediaDto.getTeachplanId();
		//约束校验 只有二级目录才能绑定视频 教学计划不存在也无法绑定
		Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
		if(teachplan==null){
			XueChengPlusException.cast("教学计划不存在");
		}
		Integer grade = teachplan.getGrade();
		if(grade!=2){
			XueChengPlusException.cast("只有二级目录才可以绑定视频");
		}
		
		//删除原来的绑定关系
		LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<TeachplanMedia>();
		queryWrapper.eq(TeachplanMedia::getTeachplanId, teachPlanId);
		teachplanMediaMapper.delete(queryWrapper);
		TeachplanMedia  teachplanMedia=new TeachplanMedia();
		teachplanMedia.setTeachplanId(teachPlanId);
		teachplanMedia.setMediaFilename(bindTeachPlanMediaDto.getFileName());
		teachplanMedia.setMediaId(bindTeachPlanMediaDto.getMediaId());
		teachplanMedia.setCreateDate(LocalDateTime.now());
		teachplanMedia.setCourseId(teachplan.getCourseId());
		teachplanMediaMapper.insert(teachplanMedia);
		return teachplanMedia;
		//添加新的绑定关系
		
	}
	
	
	//计算新的课程的orderby 找到同级的课程计划数量
	public int getTeachPlanCount(Long courseId,Long parentId){
		LambdaQueryWrapper<Teachplan> teachplanLambdaQueryWrapper = new LambdaQueryWrapper<>();
		teachplanLambdaQueryWrapper.eq(Teachplan::getCourseId,courseId);
		teachplanLambdaQueryWrapper.eq(Teachplan::getParentid,parentId);
		Integer integer = teachplanMapper.selectCount(teachplanLambdaQueryWrapper);
		return integer.intValue();
	}
}
