package com.ganesh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ganesh.model.FileDocument;

@Repository 
public interface FileDocumentRepository extends JpaRepository<FileDocument, Long> {

	FileDocument findByFileName(String fileName);

}
