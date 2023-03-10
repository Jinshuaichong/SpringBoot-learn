package com.xuecheng.content.feignclient;

import com.xuecheng.content.feignclient.model.CourseIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description TODO
 * @Date 2023/3/1,15:00
 * @Author Metty
 * @Version 1.0
 */
@Component
@Slf4j
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
	
	//使用fallbackFactory可以获取到异常信息
	@Override
	public SearchServiceClient create(Throwable throwable) {
		return new SearchServiceClient() {
			@Override
			public Boolean add(@RequestBody CourseIndex courseIndex) {
				throwable.printStackTrace();
				log.debug("调用搜索服务发生熔断,执行降级方法,异常信息:{}",throwable.getMessage());
				return false;
			}
		};
	}
}
