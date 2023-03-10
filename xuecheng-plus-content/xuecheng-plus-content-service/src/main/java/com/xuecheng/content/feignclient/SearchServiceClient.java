package com.xuecheng.content.feignclient;

import com.xuecheng.content.feignclient.model.CourseIndex;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description 搜索服务远程调用接口
 * @Date 2023/3/1,14:35
 * @Author Metty
 * @Version 1.0
 */

@FeignClient(value = "search",fallbackFactory = SearchServiceClientFallbackFactory.class)
@RequestMapping("/search")
public interface SearchServiceClient {
	
	@ApiOperation("添加课程索引")
	@PostMapping("/index/course")
	Boolean add(@RequestBody CourseIndex courseIndex);
	
}
