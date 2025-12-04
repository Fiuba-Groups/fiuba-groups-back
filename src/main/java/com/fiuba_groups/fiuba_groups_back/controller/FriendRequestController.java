package com.fiuba_groups.fiuba_groups_back.controller;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.FriendRequest;
import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.service.FriendRequestService;
import com.fiuba_groups.fiuba_groups_back.service.UserService;
import com.fiuba_groups.fiuba_groups_back.service.dto.FriendRequestCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("friend-requests")
public class FriendRequestController {
    
    @Autowired
    private FriendRequestService friendRequestService;
    
    @Autowired
    private UserService userService;

    /**
     * Envía una solicitud de amistad
     */
    @PostMapping
    public ResponseEntity<?> sendFriendRequest(Authentication auth, 
                                                @RequestBody FriendRequestCreateRequest request) {
        try {
            Long studentId = getStudentIdFromAuth(auth);
            FriendRequest created = friendRequestService.sendFriendRequest(studentId, request);
            URI location = URI.create("/friend-requests/" + created.getId());
            return ResponseEntity.created(location).body(created);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene las solicitudes de amistad enviadas por el usuario actual
     */
    @GetMapping("/sent")
    public ResponseEntity<?> getSentRequests(Authentication auth) {
        try {
            Long studentId = getStudentIdFromAuth(auth);
            List<FriendRequest> requests = friendRequestService.getSentRequests(studentId);
            return ResponseEntity.ok(requests);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene las solicitudes de amistad pendientes enviadas por el usuario actual
     */
    @GetMapping("/sent/pending")
    public ResponseEntity<?> getPendingSentRequests(Authentication auth) {
        try {
            Long studentId = getStudentIdFromAuth(auth);
            List<FriendRequest> requests = friendRequestService.getPendingSentRequests(studentId);
            return ResponseEntity.ok(requests);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene las solicitudes de amistad recibidas por el usuario actual
     */
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedRequests(Authentication auth) {
        try {
            Long studentId = getStudentIdFromAuth(auth);
            List<FriendRequest> requests = friendRequestService.getReceivedRequests(studentId);
            return ResponseEntity.ok(requests);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene las solicitudes de amistad pendientes recibidas por el usuario actual
     */
    @GetMapping("/received/pending")
    public ResponseEntity<?> getPendingReceivedRequests(Authentication auth) {
        try {
            Long studentId = getStudentIdFromAuth(auth);
            List<FriendRequest> requests = friendRequestService.getPendingReceivedRequests(studentId);
            return ResponseEntity.ok(requests);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Acepta una solicitud de amistad
     */
    @PutMapping("/{requestId}/accept")
    public ResponseEntity<?> acceptFriendRequest(Authentication auth, 
                                                  @PathVariable Long requestId) {
        try {
            Long studentId = getStudentIdFromAuth(auth);
            FriendRequest accepted = friendRequestService.acceptFriendRequest(requestId, studentId);
            return ResponseEntity.ok(accepted);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Rechaza una solicitud de amistad
     */
    @PutMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectFriendRequest(Authentication auth, 
                                                  @PathVariable Long requestId) {
        try {
            Long studentId = getStudentIdFromAuth(auth);
            FriendRequest rejected = friendRequestService.rejectFriendRequest(requestId, studentId);
            return ResponseEntity.ok(rejected);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Cancela una solicitud de amistad enviada
     */
    @DeleteMapping("/{requestId}")
    public ResponseEntity<?> cancelFriendRequest(Authentication auth, 
                                                  @PathVariable Long requestId) {
        try {
            Long studentId = getStudentIdFromAuth(auth);
            friendRequestService.cancelFriendRequest(requestId, studentId);
            return ResponseEntity.noContent().build();
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene la lista de amigos del usuario actual
     */
    @GetMapping("/friends")
    public ResponseEntity<?> getFriends(Authentication auth) {
        try {
            Long studentId = getStudentIdFromAuth(auth);
            List<Student> friends = friendRequestService.getFriends(studentId);
            return ResponseEntity.ok(friends);
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Elimina un amigo
     */
    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<?> removeFriend(Authentication auth, 
                                          @PathVariable Long friendId) {
        try {
            Long studentId = getStudentIdFromAuth(auth);
            friendRequestService.removeFriend(studentId, friendId);
            return ResponseEntity.noContent().build();
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verifica el estado de la relación con otro estudiante
     */
    @GetMapping("/status/{otherStudentId}")
    public ResponseEntity<?> getFriendshipStatus(Authentication auth, 
                                                  @PathVariable Long otherStudentId) {
        try {
            Long studentId = getStudentIdFromAuth(auth);
            String status = friendRequestService.getFriendshipStatus(studentId, otherStudentId);
            return ResponseEntity.ok(Map.of("status", status));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene el ID del estudiante del usuario autenticado
     */
    private Long getStudentIdFromAuth(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new BadRequestException("Usuario no autenticado");
        }
        
        User user = userService.getUserByEmail(auth.getName());
        if (user.getStudent() == null) {
            throw new BadRequestException("El usuario no tiene un perfil de estudiante");
        }
        
        return user.getStudent().getId();
    }
}
