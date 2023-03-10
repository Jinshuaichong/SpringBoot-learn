package com.xuecheng.media.api;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @description 媒资文件管理接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
 @Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
 @RestController
public class MediaFilesController {
	 @Resource
	 MediaFileService mediaFileService;
	 
    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto){
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiles(companyId,pageParams,queryMediaParamsDto);
	}
	
	@RequestMapping(value = "/upload/coursefile",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile filedata,
	                                  @RequestParam(value = "folder",required = false) String folder,
	                                  @RequestParam(value = "objectName",required = false)String objectName){
	
		Long companyId=1232141425L;
		UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
		String contentType=filedata.getContentType();
		uploadFileParamsDto.setContentType(contentType);
		uploadFileParamsDto.setFileSize(filedata.getSize());
		if(contentType.indexOf("image")>=0){
			//传过来的是个图片
			uploadFileParamsDto.setFileType("001001");
		}else{
			uploadFileParamsDto.setFileType("001003");
		}
		uploadFileParamsDto.setFilename(filedata.getOriginalFilename());
		UploadFileResultDto uploadFileResultDto=null;
		try{
			uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, filedata.getBytes(), folder, objectName);
		}catch (Exception e){
			XueChengPlusException.cast("上传文件出错");
		}
		return uploadFileResultDto;
	}
	
	@ApiOperation("预览文件接口")
	@GetMapping("/preview/{mediaId}")
	public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId){
		//调用service查询文件url返回
		MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
		
		return RestResponse.success(mediaFiles.getUrl());
	}

}
