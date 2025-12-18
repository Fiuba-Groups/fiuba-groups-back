package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.FriendRequest;
import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.repository.FriendRequestRepository;
import com.fiuba_groups.fiuba_groups_back.repository.StudentRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.FriendRequestCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendRequestService {
    
    @Autowired
    private FriendRequestRepository friendRequestRepository;
    
    @Autowired
    private StudentRepository studentRepository;

    /**
     * Envía una solicitud de amistad
     */
    @Transactional
    public FriendRequest sendFriendRequest(Long senderId, FriendRequestCreateRequest request) {
        Long receiverId = request.getReceiverId();
        
        // Validar que no sea uno mismo
        if (senderId.equals(receiverId)) {
            throw new BadRequestException("No puedes enviarte una solicitud de amistad a ti mismo");
        }
        
        // Verificar que el receptor existe
        studentRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudiante con id " + receiverId + " no encontrado"));
        
        // Verificar si ya son amigos
        if (friendRequestRepository.areFriends(senderId, receiverId)) {
            throw new BadRequestException("Ya son amigos");
        }
        
        // Verificar si ya existe una solicitud pendiente
        if (friendRequestRepository.existsPendingBetween(senderId, receiverId)) {
            throw new BadRequestException("Ya existe una solicitud de amistad pendiente");
        }
        
        try {
            FriendRequest friendRequest = new FriendRequest(senderId, receiverId);
            return friendRequestRepository.saveAndFlush(friendRequest);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Error al crear la solicitud de amistad");
        }
    }

    /**
     * Obtiene las solicitudes de amistad enviadas por un estudiante
     */
    public List<FriendRequest> getSentRequests(Long studentId) {
        return friendRequestRepository.findBySenderId(studentId);
    }

    /**
     * Obtiene las solicitudes de amistad pendientes enviadas por un estudiante
     */
    public List<FriendRequest> getPendingSentRequests(Long studentId) {
        return friendRequestRepository.findBySenderIdAndStatus(studentId, FriendRequest.Status.PENDING);
    }

    /**
     * Obtiene las solicitudes de amistad recibidas por un estudiante
     */
    public List<FriendRequest> getReceivedRequests(Long studentId) {
        return friendRequestRepository.findByReceiverId(studentId);
    }

    /**
     * Obtiene las solicitudes de amistad pendientes recibidas por un estudiante
     */
    public List<FriendRequest> getPendingReceivedRequests(Long studentId) {
        return friendRequestRepository.findByReceiverIdAndStatus(studentId, FriendRequest.Status.PENDING);
    }

    /**
     * Acepta una solicitud de amistad
     */
    @Transactional
    public FriendRequest acceptFriendRequest(Long requestId, Long currentUserId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Solicitud de amistad con id " + requestId + " no encontrada"));
        
        // Solo el receptor puede aceptar
        if (!request.getReceiverId().equals(currentUserId)) {
            throw new BadRequestException("Solo el receptor puede aceptar la solicitud");
        }
        
        if (request.getStatus() != FriendRequest.Status.PENDING) {
            throw new BadRequestException("La solicitud ya fue respondida");
        }
        
        request.setStatus(FriendRequest.Status.ACCEPTED);
        request.setRespondedAt(LocalDateTime.now());

        // Mantener la relación bidireccional de amigos en la entidad Student
        Student sender = studentRepository.findById(request.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudiante con id " + request.getSenderId() + " no encontrado"));

        Student receiver = studentRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudiante con id " + request.getReceiverId() + " no encontrado"));

        // Evitar duplicados
        if (!sender.getFriends().contains(receiver)) {
            sender.getFriends().add(receiver);
        }
        if (!receiver.getFriends().contains(sender)) {
            receiver.getFriends().add(sender);
        }

        // Guardar los cambios en students y en la solicitud
        studentRepository.save(sender);
        studentRepository.save(receiver);

        return friendRequestRepository.save(request);
    }

    /**
     * Rechaza una solicitud de amistad
     */
    @Transactional
    public FriendRequest rejectFriendRequest(Long requestId, Long currentUserId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Solicitud de amistad con id " + requestId + " no encontrada"));
        
        // Solo el receptor puede rechazar
        if (!request.getReceiverId().equals(currentUserId)) {
            throw new BadRequestException("Solo el receptor puede rechazar la solicitud");
        }
        
        if (request.getStatus() != FriendRequest.Status.PENDING) {
            throw new BadRequestException("La solicitud ya fue respondida");
        }
        
        request.setStatus(FriendRequest.Status.REJECTED);
        request.setRespondedAt(LocalDateTime.now());
        
        return friendRequestRepository.save(request);
    }

    /**
     * Cancela una solicitud de amistad enviada
     */
    @Transactional
    public void cancelFriendRequest(Long requestId, Long currentUserId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Solicitud de amistad con id " + requestId + " no encontrada"));
        
        // Solo el sender puede cancelar
        if (!request.getSenderId().equals(currentUserId)) {
            throw new BadRequestException("Solo el remitente puede cancelar la solicitud");
        }
        
        if (request.getStatus() != FriendRequest.Status.PENDING) {
            throw new BadRequestException("Solo se pueden cancelar solicitudes pendientes");
        }
        
        friendRequestRepository.delete(request);
    }

    /**
     * Obtiene la lista de amigos de un estudiante
     */
    public List<Student> getFriends(Long studentId) {
    Student student = studentRepository.findById(studentId)
        .orElseThrow(() -> new ResourceNotFoundException("Estudiante con id " + studentId + " no encontrado"));

    // Devolver copia para evitar modificaciones accidentales del entity list en capa superior
    return student.getFriends().stream().collect(Collectors.toList());
    }

    /**
     * Elimina una amistad
     */
    @Transactional
    public void removeFriend(Long studentId, Long friendId) {
        FriendRequest friendship = friendRequestRepository
                .findBetweenStudentsWithStatus(studentId, friendId, FriendRequest.Status.ACCEPTED)
                .orElseThrow(() -> new ResourceNotFoundException("Amistad no encontrada"));
        
    // Eliminar la entidad de friendship y además actualizar la relación many-to-many
    friendRequestRepository.delete(friendship);
    friendRequestRepository.flush();

    Student s1 = studentRepository.findById(studentId)
        .orElseThrow(() -> new ResourceNotFoundException("Estudiante con id " + studentId + " no encontrado"));
    Student s2 = studentRepository.findById(friendId)
        .orElseThrow(() -> new ResourceNotFoundException("Estudiante con id " + friendId + " no encontrado"));

    s1.getFriends().removeIf(f -> f.getId().equals(friendId));
    s2.getFriends().removeIf(f -> f.getId().equals(studentId));

    studentRepository.save(s1);
    studentRepository.save(s2);
    }

    /**
     * Verifica si dos estudiantes son amigos
     */
    public boolean areFriends(Long studentId1, Long studentId2) {
        return friendRequestRepository.areFriends(studentId1, studentId2);
    }

    /**
     * Obtiene el estado de la relación entre dos estudiantes
     * @return "FRIENDS", "PENDING_SENT", "PENDING_RECEIVED", o "NONE"
     */
    public String getFriendshipStatus(Long studentId, Long otherStudentId) {
        if (friendRequestRepository.areFriends(studentId, otherStudentId)) {
            return "FRIENDS";
        }
        
        var pendingRequest = friendRequestRepository
                .findBetweenStudentsWithStatus(studentId, otherStudentId, FriendRequest.Status.PENDING);
        
        if (pendingRequest.isPresent()) {
            FriendRequest request = pendingRequest.get();
            if (request.getSenderId().equals(studentId)) {
                return "PENDING_SENT";
            } else {
                return "PENDING_RECEIVED";
            }
        }
        
        return "NONE";
    }
}
