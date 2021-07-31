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

@Service
public class FileStorageService {
	
	private Path fileStoragePath;
	private String fileStorageLocation;
    	
	public FileStorageService(@Value("${file.storage.location:temp}")String fileStorageLocation) {
		this.fileStorageLocation = fileStorageLocation;
		fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();
		
		try {
			Files.createDirectories(fileStoragePath);
		} catch (IOException e) {
			throw new RuntimeException("Issue in creating directory",e);
		}
	}


	public String storeFile(MultipartFile file) {
	   String fileName = StringUtils.cleanPath(file.getOriginalFilename());
	   Path filePath = Paths.get(fileStoragePath + "\\" + fileName );
	   try {
		Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
	} catch (IOException e) {
		throw new RuntimeException("Issue in storing the file", e);
	}
		return fileName;
	}


	public Resource downloadFile(String fileName) {
		
		Path path = Paths.get(fileStorageLocation).toAbsolutePath().resolve(fileName);
		Resource resource;
		try {
			resource = new UrlResource(path.toUri());
			return resource;
		} catch (MalformedURLException e) {
			throw new RuntimeException("Issue in reading file", e);
		}
//		 if(resource.exists() && resource.isReadable()) {
//			 return resource;
//		 }else {
//			 throw new RuntimeException("the file does't exist or not readable");
//		 }
		
	}

}
