package com.fiuba_groups.fiuba_groups_back.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.model.Course;
import com.fiuba_groups.fiuba_groups_back.model.CourseOffering;
import com.fiuba_groups.fiuba_groups_back.model.Group;
import com.fiuba_groups.fiuba_groups_back.model.GroupJoinRequest;
import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.model.Subject;
import com.fiuba_groups.fiuba_groups_back.repository.CourseOfferingRepository;
import com.fiuba_groups.fiuba_groups_back.repository.CourseRepository;
import com.fiuba_groups.fiuba_groups_back.repository.GroupRepository;
import com.fiuba_groups.fiuba_groups_back.repository.StudentRepository;
import com.fiuba_groups.fiuba_groups_back.repository.SubjectRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.GroupJoinRequestCreateRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class GroupJoinRequestServiceTest {
    @Autowired private GroupJoinRequestService groupJoinRequestService;
    @Autowired private GroupRepository groupRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private CourseOfferingRepository courseOfferingRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private SubjectRepository subjectRepository;

    private Group testGroup;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        // Create test data
        Subject subject = new Subject();
        subject.setCode("CS101");
        subject.setName("Computer Science");
        subject.setDepartment("Engineering");
        subjectRepository.save(subject);

        Course course = new Course();
        course.setCommission("A");
        course.setActive(true);
        course.setSubject(subject);
        courseRepository.save(course);

        CourseOffering courseOffering = new CourseOffering();
        courseOffering.setQuarter("2024Q1");
        courseOffering.setYear("2024");
        courseOffering.setCourseEntity(course);
        courseOfferingRepository.save(courseOffering);

        testGroup = new Group();
        testGroup.setTitle("Test Group");
        testGroup.setDescription("Test Description");
        testGroup.setMaxMembers(5);
        testGroup.setCourseOffering(courseOffering);
        groupRepository.save(testGroup);

        testStudent = new Student();
        testStudent.setName("Test Student");
        testStudent.setRegister(12345);
        studentRepository.save(testStudent);
    }

    @Test
    public void test00_AddGroupJoinRequestSuccessfullyWithValidData() {
        // request
        GroupJoinRequestCreateRequest req = new GroupJoinRequestCreateRequest();
        req.setGroupId(testGroup.getId());
        req.setStudentId(testStudent.getId());

        GroupJoinRequest created = groupJoinRequestService.addGroupJoinRequest(req);

        assertNotNull(created);
        assertEquals(testGroup.getId(), created.getGroupId());
        assertEquals(testStudent.getId(), created.getStudentId());
    }

    @Test
    public void test01_AddDuplicateGroupJoinRequestThrowsBadRequestException() {
        // Create first request
        GroupJoinRequestCreateRequest req = new GroupJoinRequestCreateRequest();
        req.setGroupId(testGroup.getId());
        req.setStudentId(testStudent.getId());
        groupJoinRequestService.addGroupJoinRequest(req);

        // Try to create duplicate
        GroupJoinRequestCreateRequest duplicateReq = new GroupJoinRequestCreateRequest();
        duplicateReq.setGroupId(testGroup.getId());
        duplicateReq.setStudentId(testStudent.getId());

        assertThrows(BadRequestException.class, () -> 
            groupJoinRequestService.addGroupJoinRequest(duplicateReq));
    }

    @Test
    public void test02_AddGroupJoinRequestWithInvalidGroupIdThrowsBadRequestException() {
        GroupJoinRequestCreateRequest req = new GroupJoinRequestCreateRequest();
        req.setGroupId(999L); // Non-existent group
        req.setStudentId(testStudent.getId());

        assertThrows(BadRequestException.class, () -> 
            groupJoinRequestService.addGroupJoinRequest(req));
    }

    @Test
    public void test03_AddGroupJoinRequestWithInvalidStudentIdThrowsBadRequestException() {
        GroupJoinRequestCreateRequest req = new GroupJoinRequestCreateRequest();
        req.setGroupId(testGroup.getId());
        req.setStudentId(999L); // Non-existent student

        assertThrows(BadRequestException.class, () -> 
            groupJoinRequestService.addGroupJoinRequest(req));
    }

    @Test
    public void test04_GetRequestsByStudentIdReturnsCorrectRequests() {
        // Create another group and student for testing
        Student anotherStudent = new Student();
        anotherStudent.setName("Another Student");
        anotherStudent.setRegister(54321);
        studentRepository.save(anotherStudent);

        // Create requests for testStudent
        GroupJoinRequestCreateRequest req1 = new GroupJoinRequestCreateRequest();
        req1.setGroupId(testGroup.getId());
        req1.setStudentId(testStudent.getId());
        groupJoinRequestService.addGroupJoinRequest(req1);

        // Create request for anotherStudent (should not be returned)
        GroupJoinRequestCreateRequest req2 = new GroupJoinRequestCreateRequest();
        req2.setGroupId(testGroup.getId());
        req2.setStudentId(anotherStudent.getId());
        groupJoinRequestService.addGroupJoinRequest(req2);

        List<GroupJoinRequest> requests = groupJoinRequestService.getRequestsByStudentId(testStudent.getId());

        assertEquals(1, requests.size());
        assertEquals(testStudent.getId(), requests.get(0).getStudentId());
    }

    @Test
    public void test05_GetRequestsByGroupIdReturnsCorrectRequests() {
        // Create another student
        Student anotherStudent = new Student();
        anotherStudent.setName("Another Student");
        anotherStudent.setRegister(54321);
        studentRepository.save(anotherStudent);

        // Create requests for testGroup
        GroupJoinRequestCreateRequest req1 = new GroupJoinRequestCreateRequest();
        req1.setGroupId(testGroup.getId());
        req1.setStudentId(testStudent.getId());
        groupJoinRequestService.addGroupJoinRequest(req1);

        GroupJoinRequestCreateRequest req2 = new GroupJoinRequestCreateRequest();
        req2.setGroupId(testGroup.getId());
        req2.setStudentId(anotherStudent.getId());
        groupJoinRequestService.addGroupJoinRequest(req2);

        List<GroupJoinRequest> requests = groupJoinRequestService.getRequestsByGroupId(testGroup.getId());

        assertEquals(2, requests.size());
        assertEquals(testGroup.getId(), requests.get(0).getGroupId());
        assertEquals(testGroup.getId(), requests.get(1).getGroupId());
    }
}