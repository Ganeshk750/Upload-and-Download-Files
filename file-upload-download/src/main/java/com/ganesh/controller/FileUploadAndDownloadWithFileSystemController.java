package com.ganesh.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ganesh.controller.dto.FileUploadResponse;
import com.ganesh.service.FileStorageService;

@RestController
public class FileUploadAndDownloadWithFileSystemController {
	
	private FileStorageService fileStorageService;

	public FileUploadAndDownloadWithFileSystemController(FileStorageService fileStorageService) {
		this.fileStorageService = fileStorageService;
	}
	
	@PostMapping("single/upload")
	public FileUploadResponse singleFileUpload(@RequestParam("file")MultipartFile file){
		
		String fileName = fileStorageService.storeFile(file);
		
		String url = ServletUriComponentsBuilder.fromCurrentContextPath() // Making URL localhost:8080/download
				    .path("/download/")
				    .path(fileName)
				    .toUriString();
		String contentType = file.getContentType();
		
		FileUploadResponse response = new FileUploadResponse(fileName, contentType, url);
		
		return response;
	}
	
	
	@GetMapping("/download/{fileName}")
	public ResponseEntity<Resource> downLoadSingleFile(@PathVariable String fileName, HttpServletRequest request){
		Resource resource = fileStorageService.downloadFile(fileName);
		
		//MediaType contentType = MediaType.IMAGE_JPEG; // Used For only jpg type image
		
		String mimeType;
		
		try {
			 mimeType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException e) {
			mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}
		
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(mimeType))
				//.header(HttpHeaders.CONTENT_DISPOSITION, "attachment: fileName="+ resource.getFilename())
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName"+ resource.getFilename())
				.body(resource);
	}
	

}
