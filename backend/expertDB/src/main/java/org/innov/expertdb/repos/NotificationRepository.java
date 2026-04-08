package org.innov.expertdb.repos;

import org.innov.expertdb.notifications.Notification;
import org.innov.expertdb.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    
    List<Notification> findByUserAndIsReadFalse(User user);
    
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    
    List<Notification> findByIsReadFalse();
    
    long countByUserAndIsReadFalse(User user);
}