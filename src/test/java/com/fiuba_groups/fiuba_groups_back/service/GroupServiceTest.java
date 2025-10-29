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
import java.util.List;

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

    @Test
    public void test02_GetExistingGroupSuccessfully() {
        // setup
        Group expected = new Group();
        expected.setId(1L);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(expected));

        // request
        Group found = groupService.getGroupById(1L);

        assertEquals(found, expected);
    }

    @Test
    public void test03_GetNonExistingGroupThrowsResourceNotFoundException() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> groupService.getGroupById(1L));
    }

    @Test
    public void test04_GetAllGroupsSuccessfully() {
        // setup
        Group group1 = new Group();
        group1.setId(1L);
        Group group2 = new Group();
        group2.setId(2L);
        when(groupRepository.findAll()).thenReturn(List.of(group1, group2));

        // request
        List<Group> groups = groupService.getAllGroups();

        assertEquals(2, groups.size());
        assertEquals(group1, groups.get(0));
        assertEquals(group2, groups.get(1));
    }

    @Test
    public void test05_DeleteExistingGroupSuccessfully() {
        // setup

        // request

    }

    @Test
    public void test06_DeleteNonExistingGroupThrowsResourceNotFoundException() {
        // setup

        // request

    }

    @Test
    public void test07_AddGroupWithNullDescriptionThrowsIllegalArgumentException() {
        // setup
        CourseOffering course = new CourseOffering();
        course.setId(1L);
        when(courseOfferingRepository.findById(1L)).thenReturn(Optional.of(course));

        // request
        GroupCreateRequest req = new GroupCreateRequest();
        req.setCourseOfferingId(1L);
        req.setDescription(null);

        assertThrows(IllegalArgumentException.class, () -> groupService.addGroup(req));
    }

    @Test
    public void test08_AddGroupWithEmptyDescriptionThrowsIllegalArgumentException() {
        // setup
        CourseOffering course = new CourseOffering();
        course.setId(1L);
        when(courseOfferingRepository.findById(1L)).thenReturn(Optional.of(course));

        // request
        GroupCreateRequest req = new GroupCreateRequest();
        req.setCourseOfferingId(1L);
        req.setDescription("");
        assertThrows(IllegalArgumentException.class, () -> groupService.addGroup(req));
    }
}
