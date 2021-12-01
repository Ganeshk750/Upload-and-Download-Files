package com.ganesh.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ganesh.dto.FileUploadResponse;
import com.ganesh.model.FileDocument;
import com.ganesh.repository.FileDocumentRepository;
import com.ganesh.service.FileStorageServiceWithDb;

@RestController
public class UploadDownloadWithDbController {
	
	//private FileDocumentRepository fileDocumentRepo;
	
	private FileStorageServiceWithDb fileStorageServiceWithDb;
	
	public UploadDownloadWithDbController(FileDocumentRepository fileDocumentRepo, FileStorageServiceWithDb fileStorageServiceWithDb) {
		//this.fileDocumentRepo = fileDocumentRepo;
		this.fileStorageServiceWithDb = fileStorageServiceWithDb;
	}

	@PostMapping("single/uploadDb")
	FileUploadResponse siglneFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
		
		//String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        //FileDocument fileDocument = new FileDocument();
        //fileDocument.setFileName(fileName);
        //fileDocument.setDocFile(file.getBytes());
        //fileDocumentRepo.save(fileDocument);
		
		String fileName = fileStorageServiceWithDb.storeFile(file);
        
		String url = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFromDb/").path(fileName)
				.toUriString();

		String conentType = file.getContentType();
		
		FileUploadResponse response = new FileUploadResponse(fileName, conentType, url);
		
		return response;

	}
	
	@GetMapping("/downloadFromDb/{fileName}")
	ResponseEntity<byte[]> downloadSingleFile(@PathVariable String fileName, HttpServletRequest request) {
		
		//FileDocument resource = fileDocumentRepo.findByFileName(fileName);
		
		FileDocument resource = fileStorageServiceWithDb.downloadFile(fileName);
		
		String mimeType = request.getServletContext().getMimeType(resource.getFileName());
	
		
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType))
				//.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=" + resource.getFilename()) // in browser its downloaded
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + resource.getFileName()) // browser rendering
				.body(resource.getDocFile());
	}
	
	

}
