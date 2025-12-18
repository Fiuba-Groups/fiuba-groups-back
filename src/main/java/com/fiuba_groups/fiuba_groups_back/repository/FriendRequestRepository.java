package com.fiuba_groups.fiuba_groups_back.repository;

import com.fiuba_groups.fiuba_groups_back.model.FriendRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    
    /**
     * Busca solicitudes enviadas por un estudiante
     */
    @Query("SELECT fr FROM FriendRequest fr JOIN FETCH fr.sender JOIN FETCH fr.receiver WHERE fr.senderId = :senderId")
    List<FriendRequest> findBySenderId(@Param("senderId") Long senderId);
    
    /**
     * Busca solicitudes recibidas por un estudiante
     */
    @Query("SELECT fr FROM FriendRequest fr JOIN FETCH fr.sender JOIN FETCH fr.receiver WHERE fr.receiverId = :receiverId")
    List<FriendRequest> findByReceiverId(@Param("receiverId") Long receiverId);
    
    /**
     * Busca solicitudes enviadas por un estudiante con un estado específico
     */
    List<FriendRequest> findBySenderIdAndStatus(Long senderId, FriendRequest.Status status);
    
    /**
     * Busca solicitudes recibidas por un estudiante con un estado específico
     */
    List<FriendRequest> findByReceiverIdAndStatus(Long receiverId, FriendRequest.Status status);
    
    /**
     * Busca una solicitud entre dos estudiantes (en cualquier dirección)
     */
    @Query("SELECT fr FROM FriendRequest fr WHERE " +
           "(fr.senderId = :studentId1 AND fr.receiverId = :studentId2) OR " +
           "(fr.senderId = :studentId2 AND fr.receiverId = :studentId1)")
    Optional<FriendRequest> findBetweenStudents(@Param("studentId1") Long studentId1, 
                                                 @Param("studentId2") Long studentId2);
    
    /**
     * Busca una solicitud pendiente entre dos estudiantes
     */
    @Query("SELECT fr FROM FriendRequest fr WHERE " +
           "((fr.senderId = :studentId1 AND fr.receiverId = :studentId2) OR " +
           "(fr.senderId = :studentId2 AND fr.receiverId = :studentId1)) " +
           "AND fr.status = :status")
    Optional<FriendRequest> findBetweenStudentsWithStatus(@Param("studentId1") Long studentId1, 
                                                           @Param("studentId2") Long studentId2,
                                                           @Param("status") FriendRequest.Status status);
    
    /**
     * Verifica si existe una solicitud pendiente entre dos estudiantes
     */
    @Query("SELECT CASE WHEN COUNT(fr) > 0 THEN true ELSE false END FROM FriendRequest fr WHERE " +
           "((fr.senderId = :studentId1 AND fr.receiverId = :studentId2) OR " +
           "(fr.senderId = :studentId2 AND fr.receiverId = :studentId1)) " +
           "AND fr.status = 'PENDING'")
    boolean existsPendingBetween(@Param("studentId1") Long studentId1, 
                                  @Param("studentId2") Long studentId2);
    
    /**
     * Busca todas las solicitudes aceptadas donde el estudiante es sender o receiver
     * Esto retorna la lista de amistades
     */
    @Query("SELECT fr FROM FriendRequest fr WHERE " +
           "(fr.senderId = :studentId OR fr.receiverId = :studentId) " +
           "AND fr.status = 'ACCEPTED'")
    List<FriendRequest> findAcceptedFriendships(@Param("studentId") Long studentId);
    
    /**
     * Verifica si dos estudiantes son amigos
     */
    @Query("SELECT CASE WHEN COUNT(fr) > 0 THEN true ELSE false END FROM FriendRequest fr WHERE " +
           "((fr.senderId = :studentId1 AND fr.receiverId = :studentId2) OR " +
           "(fr.senderId = :studentId2 AND fr.receiverId = :studentId1)) " +
           "AND fr.status = 'ACCEPTED'")
    boolean areFriends(@Param("studentId1") Long studentId1, 
                       @Param("studentId2") Long studentId2);
}
