package com.fiuba_groups.fiuba_groups_back.controller;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.Group;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.service.GroupService;
import com.fiuba_groups.fiuba_groups_back.service.UserService;
import com.fiuba_groups.fiuba_groups_back.service.dto.GroupCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("groups")
public class GroupController {
    @Autowired private GroupService groupService;
    @Autowired private UserService userService;

    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroup(@PathVariable Long groupId) {
        Group group = groupService.getGroupById(groupId);
        if (group == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(group);
    }

    @PostMapping
    public ResponseEntity<?> addGroup(@RequestBody GroupCreateRequest request) {
        try {
            Group created = groupService.addGroup(request);
            URI location = URI.create("/groups/" + created.getId());
            return ResponseEntity.created(location).body(created);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Terminar un grupo - solo el creador puede hacerlo
     * PUT /groups/{groupId}/finish
     */
    @PutMapping("/{groupId}/finish")
    public ResponseEntity<?> finishGroup(Authentication auth, @PathVariable Long groupId) {
        try {
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            User user = userService.getUserByEmail(auth.getName());
            if (user.getStudent() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "User does not have a student profile"));
            }

            Group finished = groupService.finishGroup(groupId, user.getStudent().getRegister());
            return ResponseEntity.ok(finished);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Salir de un grupo - el usuario actual deja de ser miembro
     * DELETE /groups/{groupId}/leave
     */
    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<?> leaveGroup(Authentication auth, @PathVariable Long groupId) {
        try {
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            User user = userService.getUserByEmail(auth.getName());
            if (user.getStudent() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "User does not have a student profile"));
            }

            Group updated = groupService.leaveGroup(groupId, user.getStudent().getId());
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
