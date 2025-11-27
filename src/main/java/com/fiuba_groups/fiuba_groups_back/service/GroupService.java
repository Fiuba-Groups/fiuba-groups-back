package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.Group;
import com.fiuba_groups.fiuba_groups_back.model.GroupStatus;
import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.repository.GroupRepository;
import com.fiuba_groups.fiuba_groups_back.repository.StudentRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.GroupCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {
    @Autowired private GroupRepository groupRepository;
    @Autowired private StudentRepository studentRepository;

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(Long groupId) {
        return groupRepository
                            .findById(groupId)
                            .orElseThrow(
                                    () -> new ResourceNotFoundException("Group with id " + groupId + " not found"));
    }

    @Transactional
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
            
            // Buscar el estudiante creador y agregarlo como miembro
            Student creator = studentRepository.findByRegister(request.getCreatorStudentRegister())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Student with register " + request.getCreatorStudentRegister() + " not found"));
            newGroup.getMembers().add(creator);
            newGroup.setMemberCount(1); // El creador cuenta como primer miembro
            
            return groupRepository.save(newGroup);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(e.getMostSpecificCause().getMessage());
        }
    }

    @Transactional
    public Group finishGroup(Long groupId, int creatorRegister) {
        Group group = getGroupById(groupId);
        
        // Verificar que es el creador quien termina el grupo
        if (group.getCreatorStudentRegister() != creatorRegister) {
            throw new BadRequestException("Only the group creator can finish the group");
        }
        
        // Verificar que el grupo no está ya terminado
        if (group.getStatus() == GroupStatus.FINISHED) {
            throw new BadRequestException("Group is already finished");
        }
        
        group.setStatus(GroupStatus.FINISHED);
        return groupRepository.save(group);
    }

    public Group deleteGroup(Long groupId) {
        Group group = getGroupById(groupId);
        groupRepository.delete(group);
        return group;
    }
}
