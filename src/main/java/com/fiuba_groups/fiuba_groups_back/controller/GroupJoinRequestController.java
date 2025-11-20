package com.fiuba_groups.fiuba_groups_back.controller;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.model.GroupJoinRequest;
import com.fiuba_groups.fiuba_groups_back.service.GroupJoinRequestService;
import com.fiuba_groups.fiuba_groups_back.service.dto.GroupJoinRequestCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("group-join-requests")
public class GroupJoinRequestController {
    @Autowired private GroupJoinRequestService groupJoinRequestService;

    @GetMapping
    public ResponseEntity<List<GroupJoinRequest>> getAllGroupJoinRequests() {
        return ResponseEntity.ok(groupJoinRequestService.getAllGroupJoinRequests());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GroupJoinRequest>> getRequestsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(groupJoinRequestService.getRequestsByStudentId(studentId));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<GroupJoinRequest>> getRequestsByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupJoinRequestService.getRequestsByGroupId(groupId));
    }

    @PostMapping
    public ResponseEntity<?> addGroupJoinRequest(@RequestBody GroupJoinRequestCreateRequest request) {
        try {
            GroupJoinRequest created = groupJoinRequestService.addGroupJoinRequest(request);
            URI location = URI.create("/group-join-requests/" + created.getGroupId() + "/" + created.getStudentId());
            return ResponseEntity.created(location).body(created);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}