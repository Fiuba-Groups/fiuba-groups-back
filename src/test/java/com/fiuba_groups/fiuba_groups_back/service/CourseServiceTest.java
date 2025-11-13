package com.fiuba_groups.fiuba_groups_back.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.Course;
import com.fiuba_groups.fiuba_groups_back.model.Subject;
import com.fiuba_groups.fiuba_groups_back.service.dto.CourseCreateRequest;
import com.fiuba_groups.fiuba_groups_back.service.dto.SubjectCreateRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class CourseServiceTest {
    @Autowired private CourseService courseService;
    @Autowired private SubjectService subjectService;

    private Subject testSubject;

    @BeforeEach
    void setUp() {
        // Create test subject
        SubjectCreateRequest subjectReq = new SubjectCreateRequest();
        subjectReq.setCode("CS101");
        subjectReq.setName("Computer Science");
        subjectReq.setDepartment("Engineering");
        testSubject = subjectService.addSubject(subjectReq);
    }

    @Test
    public void test00_AddCourseSuccessfullyWithValidSubject() {
        // request
        CourseCreateRequest req = new CourseCreateRequest();
        req.setCommission("A");
        req.setActive(true);
        req.setSubjectCode("CS101");

        Course created = courseService.addCourse(req);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("A", created.getCommission());
        assertEquals(true, created.getActive());
        assertEquals("CS101", created.getSubjectCode());
    }

    @Test
    public void test01_AddCourseWithInvalidSubjectThrowsBadRequestException() {
        // request
        CourseCreateRequest req = new CourseCreateRequest();
        req.setCommission("A");
        req.setActive(true);
        req.setSubjectCode("NONEXISTENT");

        // This should fail with foreign key constraint violation
        assertThrows(BadRequestException.class, () -> 
            courseService.addCourse(req));
    }

    @Test
    public void test02_GetExistingCourseSuccessfully() {
        // Create course first
        CourseCreateRequest req = new CourseCreateRequest();
        req.setCommission("A");
        req.setActive(true);
        req.setSubjectCode("CS101");
        Course created = courseService.addCourse(req);

        // Get course
        Course found = courseService.getCourseById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("A", found.getCommission());
        assertEquals(true, found.getActive());
        assertEquals("CS101", found.getSubjectCode());
    }

    @Test
    public void test03_GetNonExistingCourseThrowsResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> 
            courseService.getCourseById(999L));
    }

    @Test
    public void test04_GetAllCoursesIncludesCreatedCourses() {
        // Create two courses
        CourseCreateRequest req1 = new CourseCreateRequest();
        req1.setCommission("A");
        req1.setActive(true);
        req1.setSubjectCode("CS101");
        Course course1 = courseService.addCourse(req1);

        CourseCreateRequest req2 = new CourseCreateRequest();
        req2.setCommission("B");
        req2.setActive(false);
        req2.setSubjectCode("CS101");
        Course course2 = courseService.addCourse(req2);

        // Get all courses
        List<Course> courses = courseService.getAllCourses();

        assertEquals(2, courses.size());
        // Verify both courses are in the list
        boolean foundA = courses.stream().anyMatch(c -> "A".equals(c.getCommission()));
        boolean foundB = courses.stream().anyMatch(c -> "B".equals(c.getCommission()));
        assertEquals(true, foundA);
        assertEquals(true, foundB);
    }
}