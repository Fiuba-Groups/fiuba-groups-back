package com.fiuba_groups.fiuba_groups_back.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.Course;
import com.fiuba_groups.fiuba_groups_back.model.CourseOffering;
import com.fiuba_groups.fiuba_groups_back.model.Group;
import com.fiuba_groups.fiuba_groups_back.service.dto.CourseCreateRequest;
import com.fiuba_groups.fiuba_groups_back.service.dto.CourseOfferingCreateRequest;
import com.fiuba_groups.fiuba_groups_back.service.dto.GroupCreateRequest;
import com.fiuba_groups.fiuba_groups_back.service.dto.SubjectCreateRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class GroupServiceTest {
    @Autowired private GroupService groupService;
    @Autowired private CourseOfferingService courseOfferingService;
    @Autowired private CourseService courseService;
    @Autowired private SubjectService subjectService;

    private CourseOffering testCourseOffering;

    @BeforeEach
    void setUp() {
        // Create test subject
        SubjectCreateRequest subjectReq = new SubjectCreateRequest();
        subjectReq.setCode("CS101");
        subjectReq.setName("Computer Science");
        subjectReq.setDepartment("Engineering");
        subjectService.addSubject(subjectReq);

        // Create test course
        CourseCreateRequest courseReq = new CourseCreateRequest();
        courseReq.setCommission("A");
        courseReq.setActive(true);
        courseReq.setSubjectCode("CS101");
        Course course = courseService.addCourse(courseReq);

        // Create test course offering
        CourseOfferingCreateRequest offeringReq = new CourseOfferingCreateRequest();
        offeringReq.setQuarter("2024Q1");
        offeringReq.setYear("2024");
        offeringReq.setCourseId(course.getId());
        testCourseOffering = courseOfferingService.addCourseOffering(offeringReq);
    }

    @Test
    public void test00_AddGroupWithInvalidCourseOfferingIdThrowsBadRequestException() {
        GroupCreateRequest req = new GroupCreateRequest();
        req.setCourseOfferingId(999L); // Non-existent course offering
        req.setTitle("Test Group");
        req.setDescription("Test Description");
        req.setMaxMembers(5);
        req.setCreatorStudentRegister(12345);

        assertThrows(BadRequestException.class, () -> groupService.addGroup(req));
    }

    @Test
    public void test01_AddGroupSuccessfullyWithValidData() {
        GroupCreateRequest req = new GroupCreateRequest();
        req.setCourseOfferingId(testCourseOffering.getId());
        req.setTitle("Test Group");
        req.setDescription("Test Description");
        req.setMaxMembers(5);
        req.setCreatorStudentRegister(12345);

        Group created = groupService.addGroup(req);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Test Group", created.getTitle());
        assertEquals("Test Description", created.getDescription());
        assertEquals(5, created.getMaxMembers());
        assertEquals(12345, created.getCreatorStudentRegister());
        assertEquals(testCourseOffering.getId(), created.getCourseOfferingId());
    }

    @Test
    public void test02_GetExistingGroupSuccessfully() {
        // Create a group first
        GroupCreateRequest req = new GroupCreateRequest();
        req.setCourseOfferingId(testCourseOffering.getId());
        req.setTitle("Test Group");
        req.setDescription("Test Description");
        req.setMaxMembers(5);
        req.setCreatorStudentRegister(12345);
        Group created = groupService.addGroup(req);

        // Retrieve the group
        Group found = groupService.getGroupById(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("Test Group", found.getTitle());
        assertEquals("Test Description", found.getDescription());
    }

    @Test
    public void test03_GetNonExistingGroupThrowsResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> groupService.getGroupById(999L));
    }

    @Test
    public void test04_GetAllGroupsSuccessfully() {
        // Create two groups
        GroupCreateRequest req1 = new GroupCreateRequest();
        req1.setCourseOfferingId(testCourseOffering.getId());
        req1.setTitle("Group 1");
        req1.setDescription("Description 1");
        req1.setMaxMembers(5);
        req1.setCreatorStudentRegister(12345);
        groupService.addGroup(req1);

        GroupCreateRequest req2 = new GroupCreateRequest();
        req2.setCourseOfferingId(testCourseOffering.getId());
        req2.setTitle("Group 2");
        req2.setDescription("Description 2");
        req2.setMaxMembers(8);
        req2.setCreatorStudentRegister(67890);
        groupService.addGroup(req2);

        // Get all groups
        List<Group> groups = groupService.getAllGroups();

        assertEquals(2, groups.size());
    }

    @Test
    public void test05_DeleteExistingGroupSuccessfully() {
        // Create a group first
        GroupCreateRequest req = new GroupCreateRequest();
        req.setCourseOfferingId(testCourseOffering.getId());
        req.setTitle("Test Group");
        req.setDescription("Test Description");
        req.setMaxMembers(5);
        req.setCreatorStudentRegister(12345);
        Group created = groupService.addGroup(req);

        // Delete the group
        Group deleted = groupService.deleteGroup(created.getId());
        assertEquals(created.getId(), deleted.getId());

        // Verify it's deleted
        assertThrows(ResourceNotFoundException.class, () -> groupService.getGroupById(created.getId()));
    }

    @Test
    public void test06_DeleteNonExistingGroupThrowsResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> groupService.deleteGroup(999L));
    }

    @Test
    public void test07_AddGroupWithNullDescriptionThrowsIllegalArgumentException() {
        GroupCreateRequest req = new GroupCreateRequest();
        req.setCourseOfferingId(testCourseOffering.getId());
        req.setTitle("Test Group");
        req.setDescription(null);
        req.setMaxMembers(5);
        req.setCreatorStudentRegister(12345);

        assertThrows(IllegalArgumentException.class, () -> groupService.addGroup(req));
    }

    @Test
    public void test08_AddGroupWithEmptyDescriptionThrowsIllegalArgumentException() {
        GroupCreateRequest req = new GroupCreateRequest();
        req.setCourseOfferingId(testCourseOffering.getId());
        req.setTitle("Test Group");
        req.setDescription("");
        req.setMaxMembers(5);
        req.setCreatorStudentRegister(12345);

        assertThrows(IllegalArgumentException.class, () -> groupService.addGroup(req));
    }
}
