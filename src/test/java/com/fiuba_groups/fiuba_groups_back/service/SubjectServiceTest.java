package com.fiuba_groups.fiuba_groups_back.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.Subject;
import com.fiuba_groups.fiuba_groups_back.service.dto.SubjectCreateRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class SubjectServiceTest {
    @Autowired private SubjectService subjectService;

    @Test
    public void test00_AddSubjectSuccessfullyReturnsTheAddedSubject() {
        // request
        SubjectCreateRequest req = new SubjectCreateRequest();
        req.setCode("CS101");
        req.setName("Computer Science");
        req.setDepartment("Engineering");

        Subject created = subjectService.addSubject(req);

        assertNotNull(created);
        assertEquals("CS101", created.getCode());
        assertEquals("Computer Science", created.getName());
        assertEquals("Engineering", created.getDepartment());
    }

    @Test
    public void test01_AddSubjectWithSameCodeUpdatesExistingSubject() {
        // Create first subject
        SubjectCreateRequest req = new SubjectCreateRequest();
        req.setCode("CS101");
        req.setName("Computer Science");
        req.setDepartment("Engineering");
        Subject first = subjectService.addSubject(req);
        assertEquals("Computer Science", first.getName());

        // Create subject with same code but different data (should update)
        SubjectCreateRequest updateReq = new SubjectCreateRequest();
        updateReq.setCode("CS101");
        updateReq.setName("Updated Computer Science");
        updateReq.setDepartment("Updated Engineering");
        Subject updated = subjectService.addSubject(updateReq);

        // Should have updated the existing subject
        assertEquals("CS101", updated.getCode());
        assertEquals("Updated Computer Science", updated.getName());
        assertEquals("Updated Engineering", updated.getDepartment());
        
        // Verify only one subject exists with this code
        Subject found = subjectService.getSubjectByCode("CS101");
        assertEquals("Updated Computer Science", found.getName());
    }

    @Test
    public void test02_GetExistingSubjectSuccessfully() {
        // Create subject first
        SubjectCreateRequest req = new SubjectCreateRequest();
        req.setCode("CS101");
        req.setName("Computer Science");
        req.setDepartment("Engineering");
        subjectService.addSubject(req);

        // Get subject
        Subject found = subjectService.getSubjectByCode("CS101");

        assertEquals("CS101", found.getCode());
        assertEquals("Computer Science", found.getName());
        assertEquals("Engineering", found.getDepartment());
    }

    @Test
    public void test03_GetNonExistingSubjectThrowsResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> 
            subjectService.getSubjectByCode("NONEXISTENT"));
    }

    @Test
    public void test04_GetAllSubjectsIncludesCreatedSubjects() {
        // Get initial count
        List<Subject> initialSubjects = subjectService.getAllSubjects();
        int initialCount = initialSubjects.size();

        // Create two subjects
        SubjectCreateRequest req1 = new SubjectCreateRequest();
        req1.setCode("CS101");
        req1.setName("Computer Science");
        req1.setDepartment("Engineering");
        subjectService.addSubject(req1);

        SubjectCreateRequest req2 = new SubjectCreateRequest();
        req2.setCode("MATH101");
        req2.setName("Mathematics");
        req2.setDepartment("Math");
        subjectService.addSubject(req2);

        // Get all subjects
        List<Subject> subjects = subjectService.getAllSubjects();

        assertEquals(initialCount + 2, subjects.size());
        // Verify both subjects are in the list
        boolean foundCS = subjects.stream().anyMatch(s -> "CS101".equals(s.getCode()));
        boolean foundMath = subjects.stream().anyMatch(s -> "MATH101".equals(s.getCode()));
        assertEquals(true, foundCS);
        assertEquals(true, foundMath);
    }
}