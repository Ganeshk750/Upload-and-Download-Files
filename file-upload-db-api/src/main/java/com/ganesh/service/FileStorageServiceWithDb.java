package com.ganesh.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ganesh.model.FileDocument;
import com.ganesh.repository.FileDocumentRepository;

@Service
public class FileStorageServiceWithDb {
	
	private FileDocumentRepository fileDocumentRepo;
	
    private Path fileStoragePath;
	
	private String fileStorageLocation;
	
	public FileStorageServiceWithDb(@Value("${file.storage.location:temp}")String fileStorageLocation, FileDocumentRepository fileDocumentRepo ) {
		this.fileDocumentRepo = fileDocumentRepo;
		this.fileStorageLocation = fileStorageLocation;
		this.fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();
		try {
			Files.createDirectories(fileStoragePath);
		} catch (IOException e) {
			//e.printStackTrace();
			throw new RuntimeException("Issue in creating file directory");
		}
	}


	
	public String storeFile(MultipartFile file) throws IOException {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        FileDocument fileDocument = new FileDocument();
        fileDocument.setFileName(fileName);
        fileDocument.setDocFile(file.getBytes());
        fileDocumentRepo.save(fileDocument);
		Path filePath = Paths.get(fileStoragePath + "//" + fileName);
		try {
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			//e.printStackTrace();
			throw new RuntimeException("Issue in storing the file");
		}
		return fileName;
	}



	public FileDocument downloadFile(String fileName) {
		//FileDocument docName = fileDocumentRepo.findByFileName(fileName);
		Path path = Paths.get(fileStorageLocation).toAbsolutePath().resolve(fileName);
		Resource resource;
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			throw new RuntimeException("Issue in reading the file", e);
		}
		 if(resource.exists() && resource.isReadable()) {
			 FileDocument docName = fileDocumentRepo.findByFileName(fileName);
			 return docName;
		 }else {
			 throw new RuntimeException("the file doesn't exist or not readable");
		 }
		 
	}



	
	

}
