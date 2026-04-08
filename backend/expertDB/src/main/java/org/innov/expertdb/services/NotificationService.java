package org.innov.expertdb.services;

import org.innov.expertdb.auth.dtos.notification.NotificationResponse;
import org.innov.expertdb.notifications.Notification;
import org.innov.expertdb.repos.NotificationRepository;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.Role;
import org.innov.expertdb.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void notifyAdminsOfPendingUser(User pendingUser) {
        // Find all admin users
        List<User> admins = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .filter(user -> user.getStatus() == AccountStatus.ACTIVE)
                .collect(Collectors.toList());

        // Create notification for each admin
        for (User admin : admins) {
            Notification notification = new Notification();
            notification.setUser(admin);
            notification.setTitle("New User Pending Approval");
            notification.setMessage(String.format(
                    "User '%s %s' (%s) has registered and is pending approval.",
                    pendingUser.getName(),
                    pendingUser.getSurname(),
                    pendingUser.getEmail()
            ));
            notification.setNotificationType("PENDING_APPROVAL");
            notification.setRelatedEntityId(pendingUser.getId().toString());
            notification.setRead(false);
            
            notificationRepository.save(notification);
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalse(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Transactional
    public void markAsRead(UUID notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only mark your own notifications as read");
        }
        
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markMultipleAsRead(List<UUID> notificationIds, User user) {
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);
        
        for (Notification notification : notifications) {
            if (notification.getUser().getId().equals(user.getId())) {
                notification.setRead(true);
            }
        }
        
        notificationRepository.saveAll(notifications);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isRead(),
                notification.getNotificationType(),
                notification.getRelatedEntityId(),
                notification.getCreatedAt()
        );
    }
}