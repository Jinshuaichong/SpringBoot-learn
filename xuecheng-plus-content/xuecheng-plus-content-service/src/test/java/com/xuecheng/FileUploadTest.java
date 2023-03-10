package com.xuecheng;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

/**
 * @Description TODO
 * @Date 2023/3/1,14:41
 * @Author Metty
 * @Version 1.0
 */
@SpringBootTest
public class FileUploadTest {
	
	@Resource
	MediaServiceClient mediaServiceClient;
	
	
	@Test
	public void test(){
		File file = new File("D:\\develop\\18.html");
		MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
		String course = mediaServiceClient.upload(multipartFile, "course", "test.html");
		System.out.println(course);
		
	}
}
