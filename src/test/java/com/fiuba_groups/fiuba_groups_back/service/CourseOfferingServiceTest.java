package com.fiuba_groups.fiuba_groups_back.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.model.Course;
import com.fiuba_groups.fiuba_groups_back.model.CourseOffering;
import com.fiuba_groups.fiuba_groups_back.model.Subject;
import com.fiuba_groups.fiuba_groups_back.service.dto.CourseCreateRequest;
import com.fiuba_groups.fiuba_groups_back.service.dto.CourseOfferingCreateRequest;
import com.fiuba_groups.fiuba_groups_back.service.dto.SubjectCreateRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class CourseOfferingServiceTest {
    @Autowired private CourseOfferingService courseOfferingService;
    @Autowired private CourseService courseService;
    @Autowired private SubjectService subjectService;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Create test subject
        SubjectCreateRequest subjectReq = new SubjectCreateRequest();
        subjectReq.setCode("CS101");
        subjectReq.setName("Computer Science");
        subjectReq.setDepartment("Engineering");
        Subject subject = subjectService.addSubject(subjectReq);

        // Create test course
        CourseCreateRequest courseReq = new CourseCreateRequest();
        courseReq.setCommission("A");
        courseReq.setActive(true);
        courseReq.setSubjectCode("CS101");
        testCourse = courseService.addCourse(courseReq);
    }

    @Test
    public void test00_AddCourseOfferingSuccessfullyWithValidData() {
        // request
        CourseOfferingCreateRequest req = new CourseOfferingCreateRequest();
        req.setQuarter("2024Q1");
        req.setYear("2024");
        req.setCourseId(testCourse.getId());

        CourseOffering created = courseOfferingService.addCourseOffering(req);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("2024Q1", created.getQuarter());
        assertEquals("2024", created.getYear());
        assertEquals(testCourse.getId(), created.getCourseId());
    }

    @Test
    public void test01_AddCourseOfferingWithInvalidCourseIdThrowsBadRequestException() {
        // request
        CourseOfferingCreateRequest req = new CourseOfferingCreateRequest();
        req.setQuarter("2024Q1");
        req.setYear("2024");
        req.setCourseId(999L); // Non-existent course

        assertThrows(BadRequestException.class, () -> 
            courseOfferingService.addCourseOffering(req));
    }

    @Test
    public void test02_AddDuplicateCourseOfferingThrowsBadRequestException() {
        // Create first course offering
        CourseOfferingCreateRequest req = new CourseOfferingCreateRequest();
        req.setQuarter("2024Q1");
        req.setYear("2024");
        req.setCourseId(testCourse.getId());
        courseOfferingService.addCourseOffering(req);

        // Try to create duplicate
        CourseOfferingCreateRequest duplicateReq = new CourseOfferingCreateRequest();
        duplicateReq.setQuarter("2024Q1");
        duplicateReq.setYear("2024");
        duplicateReq.setCourseId(testCourse.getId());

        assertThrows(BadRequestException.class, () -> 
            courseOfferingService.addCourseOffering(duplicateReq));
    }

    @Test
    public void test03_AddCourseOfferingsWithDifferentQuartersSuccessfully() {
        // Create course offering for Q1
        CourseOfferingCreateRequest req1 = new CourseOfferingCreateRequest();
        req1.setQuarter("2024Q1");
        req1.setYear("2024");
        req1.setCourseId(testCourse.getId());
        CourseOffering offering1 = courseOfferingService.addCourseOffering(req1);

        // Create course offering for Q2 (should succeed)
        CourseOfferingCreateRequest req2 = new CourseOfferingCreateRequest();
        req2.setQuarter("2024Q2");
        req2.setYear("2024");
        req2.setCourseId(testCourse.getId());
        CourseOffering offering2 = courseOfferingService.addCourseOffering(req2);

        assertNotNull(offering1);
        assertNotNull(offering2);
        assertEquals("2024Q1", offering1.getQuarter());
        assertEquals("2024Q2", offering2.getQuarter());
    }
}