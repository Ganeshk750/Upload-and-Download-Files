package com.ganesh.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ganesh.dto.FileUploadResponse;
import com.ganesh.service.FileStorageService;

@RestController
public class UploadDownloadWithFileSystemController {

	private FileStorageService fileStorageServie;

	public UploadDownloadWithFileSystemController(FileStorageService fileStorageServie) {
		this.fileStorageServie = fileStorageServie;
	}

	@PostMapping("single/upload")
	FileUploadResponse siglneFileUpload(@RequestParam("file") MultipartFile file) {
		String fileName = fileStorageServie.storeFile(file);

		String url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/").path(fileName)
				.toUriString();

		String conentType = file.getContentType();
		
		FileUploadResponse response = new FileUploadResponse(fileName, conentType, url);
		
		return response;

	}
	
	@GetMapping("/download/{fileName}")
	ResponseEntity<Resource> downloadSingleFile(@PathVariable String fileName, HttpServletRequest request) {
		Resource resource = fileStorageServie.downloadFile(fileName);
		//MediaType contentType = MediaType.IMAGE_JPEG; // here is fixed content type
		String mimeType ;
		try {
			mimeType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException e) {
			//e.printStackTrace();
			mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}
		
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType))
				//.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=" + resource.getFilename()) // in browser its downloaded
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + resource.getFilename()) // browser rendering
				.body(resource);
	}
	
	
	@PostMapping("multiple/upload")
	List<FileUploadResponse> multipleFileUpload(@RequestParam("files") MultipartFile[] files) {
		
		if(files.length > 5) {
			throw new RuntimeException("to many files");
		}

		List<FileUploadResponse> uploadResponsesList = new ArrayList<>();

		Arrays.asList(files).stream().forEach(file -> {

			String fileName = fileStorageServie.storeFile(file);

			String url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/").path(fileName)
					.toUriString();

			String conentType = file.getContentType();

			FileUploadResponse response = new FileUploadResponse(fileName, conentType, url);

			uploadResponsesList.add(response);
		});

		return uploadResponsesList;

	}
	
	@GetMapping("zipDownload")
	void zipDownload(@RequestParam("fileName") String[] files, HttpServletResponse response) throws IOException {

		// 1. create ZipOutputStream its having Zip entry of each object
		
		try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
			Arrays.asList(files).stream().forEach(file -> {
				Resource resource = fileStorageServie.downloadFile(file);
				ZipEntry zipEntry = new ZipEntry(resource.getFilename());
				try {
					zipEntry.setSize(resource.contentLength());
					zos.putNextEntry(zipEntry);

					StreamUtils.copy(resource.getInputStream(), zos);
					zos.closeEntry();

				} catch (IOException e) {
					// e.printStackTrace();
					System.out.println("Some Exception while ziping");
				}
			});

			zos.finish();
			
		}
		//response.setStatus(200);
		//response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=zipfile");

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
