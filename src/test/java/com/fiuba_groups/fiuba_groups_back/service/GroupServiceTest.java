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
import com.fiuba_groups.fiuba_groups_back.service.GroupService;
import com.fiuba_groups.fiuba_groups_back.service.dto.GroupCreateRequest;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

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
}
