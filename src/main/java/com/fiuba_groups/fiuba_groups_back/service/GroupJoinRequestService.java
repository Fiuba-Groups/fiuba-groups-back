package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.model.GroupJoinRequest;
import com.fiuba_groups.fiuba_groups_back.repository.GroupJoinRequestRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.GroupJoinRequestCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupJoinRequestService {
    @Autowired GroupJoinRequestRepository groupJoinRequestRepository;

    public GroupJoinRequest addGroupJoinRequest(GroupJoinRequestCreateRequest request) {
        try {
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
}