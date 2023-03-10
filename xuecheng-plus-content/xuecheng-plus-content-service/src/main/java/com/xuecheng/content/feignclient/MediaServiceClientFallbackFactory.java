package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description TODO
 * @Date 2023/3/1,15:00
 * @Author Metty
 * @Version 1.0
 */
@Component
@Slf4j
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
	
	//使用fallbackFactory可以获取到异常信息
	@Override
	public MediaServiceClient create(Throwable throwable) {
		return new MediaServiceClient(){
			@Override
			public String upload(MultipartFile filedata, String folder, String objectName) {
				//降级方法
				log.debug("调用媒资管理服务上传文件时发生熔断,异常信息:{}",throwable.getMessage());
				return null;
			}
		};
	}
}
