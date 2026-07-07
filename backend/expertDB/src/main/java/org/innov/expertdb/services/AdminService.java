package org.innov.expertdb.services;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.innov.expertdb.auth.dtos.admin.AdminNotificationsResponse;
import org.innov.expertdb.auth.dtos.admin.AdminNotificationsResponse.PendingUserNotification;
import org.innov.expertdb.auth.dtos.admin.AdminUserResponse;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.Role;
import org.innov.expertdb.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void approveUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Role.VIEWER);
        user.setStatus(AccountStatus.ACTIVE);
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    @Transactional
    public void rejectUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(AccountStatus.DISABLED);
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    @Transactional
    public void makeAdmin(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Role.ADMIN);
        user.setStatus(AccountStatus.ACTIVE);
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toAdminResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateRole(UUID id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Role.valueOf(role));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);
    }

    public AdminNotificationsResponse getNotifications() {
        List<User> pending = userRepository.findByStatus(AccountStatus.PENDING);
        List<PendingUserNotification> items = pending.stream()
                .map(u -> new PendingUserNotification(
                        u.getId().toString(),
                        displayName(u),
                        u.getEmail(),
                        u.getFaculty(),
                        u.getAcademicRank(),
                        timeAgo(u.getCreatedAt())
                ))
                .collect(Collectors.toList());
        return new AdminNotificationsResponse(pending.size(), items);
    }

    private String displayName(User u) {
        String full = u.getFullName();
        if (full != null && !full.isBlank()) return full;
        return (u.getName() + " " + u.getSurname()).trim();
    }

    private String timeAgo(Instant createdAt) {
        if (createdAt == null) return "recently";
        Duration d = Duration.between(createdAt, Instant.now());
        if (d.toMinutes() < 60) return d.toMinutes() + "m ago";
        if (d.toHours() < 24) return d.toHours() + "h ago";
        long days = d.toDays();
        if (days < 7) return days + "d ago";
        return (days / 7) + "w ago";
    }

    private AdminUserResponse toAdminResponse(User u) {
        return new AdminUserResponse(
                u.getId(), u.getName(), u.getSurname(), u.getEmail(), u.getRole(), u.getStatus(),
                u.getTitlePrefix(), u.getFullName(), u.getContactDetails(), u.getAcademicRank(),
                u.getUniversityEmail(), u.getPhoneNumber(), u.getHighestQualification(),
                u.getProfessionalMemberships(), u.getComplianceAccreditation(),
                u.getFaculty(), u.getDepartment(), u.getYearsOfConsultancyExperience(),
                u.getConsultancyExperience(), u.getConsultancyAvailability(),
                u.getPreferredConsultancyTypes(), u.getGeographicScope(),
                u.getSkillsAndCompetences(), u.getLanguagesSpoken(), u.getLanguageProficiencies(),
                u.getAreasOfExpertise(), u.getIndustrialAreasOfExpertise(),
                u.getNotes(), u.getProfilePhotoDataUrl(),
                u.getCvDataUrl(), u.getUniversityIdDataUrl(),
                u.getProfessionalExperiences(), u.getProfileLinks()
        );
    }
}
