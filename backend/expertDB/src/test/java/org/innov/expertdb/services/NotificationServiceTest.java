package org.innov.expertdb.services;

import org.innov.expertdb.notifications.Notification;
import org.innov.expertdb.repos.NotificationRepository;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.Role;
import org.innov.expertdb.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User pendingUser;
    private User admin1;
    private User admin2;
    private User regularUser;

    @BeforeEach
    void setUp() {
        pendingUser = new User();
        pendingUser.setId(UUID.randomUUID());
        pendingUser.setName("John");
        pendingUser.setSurname("Doe");
        pendingUser.setEmail("john@university.edu");
        pendingUser.setStatus(AccountStatus.PENDING);
        pendingUser.setRole(Role.USER);

        admin1 = new User();
        admin1.setId(UUID.randomUUID());
        admin1.setName("Admin");
        admin1.setSurname("One");
        admin1.setEmail("admin1@university.edu");
        admin1.setStatus(AccountStatus.ACTIVE);
        admin1.setRole(Role.ADMIN);

        admin2 = new User();
        admin2.setId(UUID.randomUUID());
        admin2.setName("Admin");
        admin2.setSurname("Two");
        admin2.setEmail("admin2@university.edu");
        admin2.setStatus(AccountStatus.ACTIVE);
        admin2.setRole(Role.ADMIN);

        regularUser = new User();
        regularUser.setId(UUID.randomUUID());
        regularUser.setName("Regular");
        regularUser.setSurname("User");
        regularUser.setEmail("user@university.edu");
        regularUser.setStatus(AccountStatus.ACTIVE);
        regularUser.setRole(Role.USER);
    }

    @Test
    void notifyAdminsOfPendingUser_ShouldCreateNotificationsForAllAdmins() {
        // Arrange
        List<User> allUsers = Arrays.asList(admin1, admin2, regularUser);
        when(userRepository.findAll()).thenReturn(allUsers);

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);

        // Act
        notificationService.notifyAdminsOfPendingUser(pendingUser);

        // Assert
        verify(notificationRepository, times(2)).save(notificationCaptor.capture());
        
        List<Notification> savedNotifications = notificationCaptor.getAllValues();
        
        assertEquals(2, savedNotifications.size());
        
        // Check first admin notification
        Notification firstNotification = savedNotifications.get(0);
        assertEquals(admin1.getId(), firstNotification.getUser().getId());
        assertEquals("New User Pending Approval", firstNotification.getTitle());
        assertEquals("PENDING_APPROVAL", firstNotification.getNotificationType());
        assertEquals(pendingUser.getId().toString(), firstNotification.getRelatedEntityId());
        assertFalse(firstNotification.isRead());
        
        // Check second admin notification
        Notification secondNotification = savedNotifications.get(1);
        assertEquals(admin2.getId(), secondNotification.getUser().getId());
    }

    @Test
    void notifyAdminsOfPendingUser_WhenNoAdmins_ShouldNotCreateNotifications() {
        // Arrange
        List<User> onlyRegularUsers = Arrays.asList(regularUser);
        when(userRepository.findAll()).thenReturn(onlyRegularUsers);

        // Act
        notificationService.notifyAdminsOfPendingUser(pendingUser);

        // Assert
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void notifyAdminsOfPendingUser_WhenAdminIsNotActive_ShouldNotNotify() {
        // Arrange
        User inactiveAdmin = new User();
        inactiveAdmin.setId(UUID.randomUUID());
        inactiveAdmin.setEmail("inactive@university.edu");
        inactiveAdmin.setStatus(AccountStatus.PENDING);
        inactiveAdmin.setRole(Role.ADMIN);
        
        List<User> allUsers = Arrays.asList(inactiveAdmin);
        when(userRepository.findAll()).thenReturn(allUsers);

        // Act
        notificationService.notifyAdminsOfPendingUser(pendingUser);

        // Assert
        verify(notificationRepository, never()).save(any());
    }
}