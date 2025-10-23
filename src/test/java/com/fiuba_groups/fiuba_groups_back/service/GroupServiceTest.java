package com.fiuba_groups.fiuba_groups_back.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.CourseOffering;
import com.fiuba_groups.fiuba_groups_back.model.Group;
import com.fiuba_groups.fiuba_groups_back.repository.CourseOfferingRepository;
import com.fiuba_groups.fiuba_groups_back.repository.GroupRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.GroupCreateRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {
    @Mock private GroupRepository groupRepository;
    @Mock private CourseOfferingRepository courseOfferingRepository;
    @InjectMocks private GroupService groupService;

    @Test
    public void test00_GetNonExistingGroupThrowsResourceNotFoundException() {
        GroupCreateRequest req = new GroupCreateRequest();
        req.setCourseOfferingId(1L);
        req.setDescription("test");

        when(courseOfferingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groupService.addGroup(req));
    }

    @Test
    public void test01_AddGroupSuccessfullyReturnsTheAddedGroup() {
        // setup
        CourseOffering course = new CourseOffering();
        course.setId(1L);
        when(courseOfferingRepository.findById(1L)).thenReturn(Optional.of(course));

        Group expected = new Group();
        expected.setId(1L);
        expected.setDescription("test");
        expected.setCourseOffering(course);
        when(groupRepository.save(any(Group.class))).thenReturn(expected);

        // request
        GroupCreateRequest req = new GroupCreateRequest();
        req.setCourseOfferingId(1L);
        req.setDescription("test");

        Group created = groupService.addGroup(req);

        assertEquals(created, expected);
    }
}
