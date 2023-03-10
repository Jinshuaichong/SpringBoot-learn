package com.xuecheng.content.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description TODO
 * @Date 2023/2/25,14:48
 * @Author Metty
 * @Version 1.0
 */

@Data
public class CoursePreviewDto {
	CourseBaseInfoDto courseBase;
	
	List<TeachplanDto> teachplans;
	
}
