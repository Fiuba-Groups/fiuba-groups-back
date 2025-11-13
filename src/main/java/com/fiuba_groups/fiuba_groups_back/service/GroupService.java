package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.Group;
import com.fiuba_groups.fiuba_groups_back.repository.GroupRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.GroupCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {
    @Autowired private GroupRepository groupRepository;

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
        try {
            Group newGroup = new Group();
            if (request.getTitle() == null || request.getTitle().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be null or empty");
            } else {
                newGroup.setTitle(request.getTitle());
            }
            if (request.getDescription() == null || request.getDescription().isEmpty()) {
                throw new IllegalArgumentException("Description cannot be null or empty");
            } else {
                newGroup.setDescription(request.getDescription());
            }
            if (request.getMaxMembers() <= 0) {
                throw new IllegalArgumentException("Max members must be greater than zero");
            } else {
                newGroup.setMaxMembers(request.getMaxMembers());
            }
            if (request.getCreatorStudentRegister() <= 0) {
                throw new IllegalArgumentException("The creator student register must be greater than zero");
            } else {
                newGroup.setCreatorStudentRegister(request.getCreatorStudentRegister());
            }
            newGroup.setCourseOfferingId(request.getCourseOfferingId());
            return groupRepository.save(newGroup);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(e.getMostSpecificCause().getMessage());
        }
    }

    public Group deleteGroup(Long groupId) {
        Group group = getGroupById(groupId);
        groupRepository.delete(group);
        return group;
    }
}
