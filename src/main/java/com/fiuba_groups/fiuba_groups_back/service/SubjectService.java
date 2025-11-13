package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.Subject;
import com.fiuba_groups.fiuba_groups_back.repository.SubjectRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.SubjectCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {
    @Autowired SubjectRepository subjectRepository;

    public Subject addSubject(SubjectCreateRequest request) {
        try {
            Subject newSubject = new Subject();
            newSubject.setCode(request.getCode());
            newSubject.setName(request.getName());
            newSubject.setDepartment(request.getDepartment());
            return subjectRepository.save(newSubject);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Subject with code '" + request.getCode() + "' already exists");
        }
    }

    public Subject getSubjectByCode(String code) {
        return subjectRepository.findById(code)
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }
}