package com.xuecheng.media;

import io.minio.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

/**
 * @author 16086
 * @version 1.0
 * @description MinIO 上传删除 查询文件测试
 * @date 2023/2/14 16:48
 */

public class MinIOTest {
	static MinioClient minioClient=
			MinioClient.builder()
					.endpoint("http://192.168.101.65:9000")
					.credentials("minioadmin","minioadmin")
					.build();
	
	@Test
	public void upload(){
		
		try {
			UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
					.bucket("testbucket")
					.object("sqlFile")
					.filename("D:\\miniotest\\tables_xxl_job.sql")
					.build();
			//上传
			minioClient.uploadObject(uploadObjectArgs);
			System.out.println("upload sucess");
		}catch (Exception e){
			System.out.println("upload fail");
		}
	}
	
	//指定桶内目录
	@Test
	public void upload2(){
		
		try {
			UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
					.bucket("testbucket")
					.object(LocalDateTime.now().getYear()+"/"+LocalDateTime.now().getMonthValue()+"/"+LocalDateTime.now().getDayOfMonth()+"/"+"1.sql")
					.filename("D:\\miniotest\\tables_xxl_job.sql")
					.build();
			//上传
			minioClient.uploadObject(uploadObjectArgs);
			System.out.println("upload sucess");
		}catch (Exception e){
			System.out.println("upload fail");
		}
	}
	
	//删除文件
	@Test
	public void delete(){
		
		try {
			RemoveObjectArgs removeObjectArgs=RemoveObjectArgs.builder().bucket("testbucket").object("2023/2/14/1.sql").build();
			minioClient.removeObject(removeObjectArgs);
			
			System.out.println("delete sucess");
		}catch (Exception e){
			System.out.println("delete fail");
		}
	}
	
	//查询文件
	@Test
	public void search(){
		
		try {
			GetObjectArgs getObjectArgs=GetObjectArgs.builder().bucket("testbucket").object("2023/2/14/1.sql").build();
			GetObjectResponse object = minioClient.getObject(getObjectArgs);
			
			if(object!=null){
				System.out.println("search sucess");
			}
		}catch (Exception e){
			System.out.println("search fail");
		}
	}
	
}
