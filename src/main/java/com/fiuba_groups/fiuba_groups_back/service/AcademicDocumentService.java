package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.AcademicDocument;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.repository.AcademicDocumentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AcademicDocumentService {

    @Autowired
    private AcademicDocumentRepository documentRepository;

    @Value("${file.upload-dir:uploads/academic-documents}")
    private String uploadDir;

    @Transactional
    public AcademicDocument uploadDocument(User user, MultipartFile file, String title, String type) throws IOException {
        if (user.getStudent() == null) {
            throw new BadRequestException("User does not have a student profile");
        }

        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Save file to disk
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create document record
        AcademicDocument document = new AcademicDocument();
        document.setTitle(title);
        document.setType(type);
        document.setFileName(originalFilename);
        document.setFileUrl("/uploads/academic-documents/" + uniqueFilename);
        document.setUploadDate(LocalDateTime.now());
        document.setStudent(user.getStudent());

        return documentRepository.save(document);
    }

    public List<AcademicDocument> getDocumentsForStudent(Long studentId) {
        return documentRepository.findByStudentId(studentId);
    }

    @Transactional
    public void deleteDocument(User user, Long documentId) throws IOException {
        AcademicDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document with id " + documentId + " not found"));

        if (user.getStudent() == null) {
            throw new BadRequestException("User does not have a student profile");
        }

        if (!document.getStudent().getId().equals(user.getStudent().getId())) {
            throw new BadRequestException("You can only delete your own documents");
        }

        // Delete file from disk
        String filename = document.getFileUrl().substring(document.getFileUrl().lastIndexOf("/") + 1);
        Path filePath = Paths.get(uploadDir).resolve(filename);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // Delete database record
        documentRepository.delete(document);
    }
}
