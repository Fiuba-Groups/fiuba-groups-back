package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.CourseOffering;
import com.fiuba_groups.fiuba_groups_back.model.Group;
import com.fiuba_groups.fiuba_groups_back.repository.CourseOfferingRepository;
import com.fiuba_groups.fiuba_groups_back.repository.GroupRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.GroupCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {
    @Autowired private GroupRepository groupRepository;
    @Autowired private CourseOfferingRepository courseOfferingRepository;

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(Long groupId) {
        return groupRepository
                            .findById(groupId)
                            .orElseThrow(
                                    () -> new ResourceNotFoundException("Group with id " + groupId + " not found"));
    }

    public Group addGroup(GroupCreateRequest request) {
        CourseOffering courseOffering =
                courseOfferingRepository
                        .findById(request.getCourseOfferingId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("CourseOffering not found"));
        Group newGroup = new Group();
        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        } else {
            newGroup.setDescription(request.getDescription());
        }
        newGroup.setCourseOffering(courseOffering);
        return groupRepository.save(newGroup);
    }
}
