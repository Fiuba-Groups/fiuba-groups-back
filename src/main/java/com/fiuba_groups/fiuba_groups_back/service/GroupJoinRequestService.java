package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.Group;
import com.fiuba_groups.fiuba_groups_back.model.GroupJoinRequest;
import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.repository.GroupJoinRequestRepository;
import com.fiuba_groups.fiuba_groups_back.repository.GroupRepository;
import com.fiuba_groups.fiuba_groups_back.repository.StudentRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.GroupJoinRequestCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupJoinRequestService {
    @Autowired GroupJoinRequestRepository groupJoinRequestRepository;
    @Autowired GroupRepository groupRepository;
    @Autowired StudentRepository studentRepository;

    public GroupJoinRequest addGroupJoinRequest(GroupJoinRequestCreateRequest request) {
        try {
            // Verificar que el grupo existe
            Group group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Group with id " + request.getGroupId() + " not found"));

            // Verificar que el estudiante existe
            Student student = studentRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Student with id " + request.getStudentId() + " not found"));

            // Verificar si ya es miembro del grupo
            if (group.getMembers().contains(student)) {
                throw new BadRequestException("You are already a member of this group");
            }

            // Verificar si ya tiene una solicitud pendiente
            boolean hasPendingRequest = groupJoinRequestRepository
                    .findByGroupId(request.getGroupId())
                    .stream()
                    .anyMatch(r -> r.getStudentId() != null && r.getStudentId().equals(request.getStudentId()));
            if (hasPendingRequest) {
                throw new BadRequestException("You already have a pending request for this group");
            }

            GroupJoinRequest newRequest = new GroupJoinRequest();
            newRequest.setGroupId(request.getGroupId());
            newRequest.setStudentId(request.getStudentId());
            return groupJoinRequestRepository.save(newRequest);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException(e.getMostSpecificCause().getMessage());
        }
    }

    public List<GroupJoinRequest> getRequestsByStudentId(Long studentId) {
        return groupJoinRequestRepository.findByStudentId(studentId);
    }

    public List<GroupJoinRequest> getRequestsByGroupId(Long groupId) {
        return groupJoinRequestRepository.findByGroupId(groupId);
    }

    public List<GroupJoinRequest> getAllGroupJoinRequests() {
        return groupJoinRequestRepository.findAll();
    }

    @Transactional
    public Group acceptGroupJoinRequest(Long requestId) {
        // Find the join request
        GroupJoinRequest joinRequest = groupJoinRequestRepository
                .findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Group join request with id " + requestId + " not found"));

        // Load the group
        Group group = groupRepository
                .findById(joinRequest.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Group with id " + joinRequest.getGroupId() + " not found"));

        // Load the student
        Student student = studentRepository
                .findById(joinRequest.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student with id " + joinRequest.getStudentId() + " not found"));

        // Check if group is full
        if (group.getMemberCount() >= group.getMaxMembers()) {
            throw new BadRequestException("Group is already full");
        }

        // Check if student is already a member
        if (group.getMembers().contains(student)) {
            throw new BadRequestException("Student is already a member of this group");
        }

        // Add student to group
        group.getMembers().add(student);
        group.setMemberCount(group.getMemberCount() + 1);

        // Save the group
        Group updatedGroup = groupRepository.save(group);

        // Delete the join request
        groupJoinRequestRepository.delete(joinRequest);

        return updatedGroup;
    }

    public void deleteGroupJoinRequest(Long requestId) {
        if (!groupJoinRequestRepository.existsById(requestId)) {
            throw new ResourceNotFoundException("Group join request with id " + requestId + " not found");
        }
        groupJoinRequestRepository.deleteById(requestId);
    }
}