package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

/**
 * @author 16086
 * @version 1.0
 * @description TODO
 * @date 2023/2/3 16:12
 */

@Data
public class TeachplanDto extends Teachplan {
	//子目录
	List<TeachplanDto> teachPlanTreeNodes;
	//关联的媒资信息
	TeachplanMedia teachplanMedia;
}
