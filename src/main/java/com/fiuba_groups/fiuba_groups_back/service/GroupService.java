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

    /**
     * Permite a un estudiante salir de un grupo.
     * Si el creador abandona y es el único miembro, el grupo se elimina.
     * Si el creador abandona y hay otros miembros, el ownership se transfiere a otro miembro.
     * 
     * @param groupId ID del grupo
     * @param studentId ID del estudiante que quiere salir
     * @return El grupo actualizado, o null si el grupo fue eliminado
     * @throws ResourceNotFoundException si el grupo no existe
     * @throws BadRequestException si el grupo ya terminó o el estudiante no es miembro
     */
    @Transactional
    public Group leaveGroup(Long groupId, Long studentId) {
        Group group = getGroupById(groupId);
        
        // Verificar que el grupo no esté terminado
        if (group.getStatus() == GroupStatus.FINISHED) {
            throw new BadRequestException("Cannot leave a finished group");
        }
        
        // Buscar al estudiante en los miembros del grupo
        Student studentToRemove = group.getMembers().stream()
                .filter(member -> member.getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Student is not a member of this group"));
        
        // Verificar si el que abandona es el creador
        boolean isCreator = studentToRemove.getRegister() == group.getCreatorStudentRegister();
        
        // Si es el creador y es el único miembro, eliminar el grupo
        if (isCreator && group.getMembers().size() == 1) {
            groupRepository.delete(group);
            return null; // Grupo eliminado
        }
        
        // Remover al estudiante del grupo
        group.getMembers().remove(studentToRemove);
        group.setMemberCount(group.getMemberCount() - 1);
        
        // Si era el creador, transferir ownership al primer miembro restante
        if (isCreator && !group.getMembers().isEmpty()) {
            Student newOwner = group.getMembers().get(0);
            group.setCreatorStudentRegister(newOwner.getRegister());
        }
        
        return groupRepository.save(group);
    }
}
