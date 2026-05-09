package org.innov.expertdb.controllers;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.innov.expertdb.auth.dtos.user.UpdateProfileRequest;
import org.innov.expertdb.auth.dtos.user.UserProfileResponse;
import org.innov.expertdb.auth.dtos.viewer.ExpertSummaryResponse;
import org.innov.expertdb.auth.dtos.viewer.ViewerNotificationsResponse;
import org.innov.expertdb.auth.dtos.viewer.ViewerNotificationsResponse.NewExpertNotification;
import org.innov.expertdb.repos.UserRepository;
import org.innov.expertdb.user.AccountStatus;
import org.innov.expertdb.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/viewer")
@RequiredArgsConstructor
public class ViewerController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of(
                "role", user.getRole().name(),
                "status", user.getStatus().name()
        ));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(toProfile(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @AuthenticationPrincipal User user,
            @RequestBody UpdateProfileRequest req) {
        if (req.fullName() != null) user.setFullName(req.fullName());
        if (req.titlePrefix() != null) user.setTitlePrefix(req.titlePrefix());
        if (req.phoneNumber() != null) user.setPhoneNumber(req.phoneNumber());
        if (req.faculty() != null) user.setFaculty(req.faculty());
        if (req.department() != null) user.setDepartment(req.department());
        if (req.academicRank() != null) user.setAcademicRank(req.academicRank());
        if (req.highestQualification() != null) user.setHighestQualification(req.highestQualification());
        if (req.professionalMemberships() != null) user.setProfessionalMemberships(req.professionalMemberships());
        if (req.complianceAccreditation() != null) user.setComplianceAccreditation(req.complianceAccreditation());
        if (req.yearsOfConsultancyExperience() != null) user.setYearsOfConsultancyExperience(req.yearsOfConsultancyExperience());
        if (req.consultancyExperience() != null) user.setConsultancyExperience(req.consultancyExperience());
        if (req.consultancyAvailability() != null) user.setConsultancyAvailability(req.consultancyAvailability());
        if (req.geographicScope() != null) user.setGeographicScope(req.geographicScope());
        if (req.preferredConsultancyTypes() != null) user.setPreferredConsultancyTypes(req.preferredConsultancyTypes());
        if (req.areasOfExpertise() != null) user.setAreasOfExpertise(req.areasOfExpertise());
        if (req.industrialAreasOfExpertise() != null) user.setIndustrialAreasOfExpertise(req.industrialAreasOfExpertise());
        if (req.skillsAndCompetences() != null) user.setSkillsAndCompetences(req.skillsAndCompetences());
        if (req.languagesSpoken() != null) user.setLanguagesSpoken(req.languagesSpoken());
        if (req.notes() != null) user.setNotes(req.notes());
        if (req.profilePhotoDataUrl() != null) user.setProfilePhotoDataUrl(req.profilePhotoDataUrl());
        if (req.cvDataUrl() != null) user.setCvDataUrl(req.cvDataUrl());
        if (req.universityIdDataUrl() != null) user.setUniversityIdDataUrl(req.universityIdDataUrl());
        userRepository.save(user);
        return ResponseEntity.ok(toProfile(user));
    }

    private UserProfileResponse toProfile(User u) {
        return new UserProfileResponse(
                u.getId(),
                u.getEmail(),
                u.getFullName(),
                u.getTitlePrefix(),
                u.getPhoneNumber(),
                u.getUniversityEmail(),
                u.getFaculty(),
                u.getDepartment(),
                u.getAcademicRank(),
                u.getHighestQualification(),
                u.getProfessionalMemberships(),
                u.getComplianceAccreditation(),
                u.getYearsOfConsultancyExperience(),
                u.getConsultancyExperience(),
                u.getConsultancyAvailability(),
                u.getGeographicScope(),
                splitCsv(u.getPreferredConsultancyTypes()),
                splitCsv(u.getAreasOfExpertise()),
                splitCsv(u.getIndustrialAreasOfExpertise()),
                splitCsv(u.getSkillsAndCompetences()),
                splitCsv(u.getLanguagesSpoken()),
                u.getNotes(),
                u.getProfilePhotoDataUrl()
        );
    }

    private List<String> splitCsv(String val) {
        if (val == null || val.isBlank()) return List.of();
        return Arrays.stream(val.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    @GetMapping("/experts")
    public ResponseEntity<List<ExpertSummaryResponse>> listExperts(
            @RequestParam(required = false) String q) {
        List<User> users;
        if (q != null && !q.isBlank()) {
            users = userRepository.searchActive(AccountStatus.ACTIVE, q.trim());
        } else {
            users = userRepository.findAll().stream()
                    .filter(u -> u.getStatus() == AccountStatus.ACTIVE && u.getFullName() != null)
                    .sorted((a, b) -> {
                        String nameA = a.getFullName() != null ? a.getFullName() : "";
                        String nameB = b.getFullName() != null ? b.getFullName() : "";
                        return nameA.compareToIgnoreCase(nameB);
                    })
                    .toList();
        }
        List<ExpertSummaryResponse> experts = users.stream().map(this::toSummary).toList();
        return ResponseEntity.ok(experts);
    }

    @GetMapping("/notifications")
    public ResponseEntity<ViewerNotificationsResponse> getNotifications(@AuthenticationPrincipal User currentUser) {
        Instant since = Instant.now().minus(Duration.ofDays(30));
        List<User> newExperts = userRepository.findByStatusAndCreatedAtAfter(AccountStatus.ACTIVE, since)
                .stream()
                .filter(u -> !u.getId().equals(currentUser.getId()) && u.getFullName() != null)
                .collect(Collectors.toList());

        List<NewExpertNotification> items = newExperts.stream()
                .map(u -> new NewExpertNotification(
                        u.getId().toString(),
                        expertDisplayName(u),
                        u.getFullName() != null ? u.getFullName() : (u.getName() + " " + u.getSurname()).trim(),
                        u.getFaculty(),
                        u.getAcademicRank(),
                        u.getAreasOfExpertise(),
                        viewerTimeAgo(u.getCreatedAt())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ViewerNotificationsResponse(newExperts.size(), items));
    }

    private String expertDisplayName(User u) {
        String prefix = u.getTitlePrefix() != null ? u.getTitlePrefix() + " " : "";
        String full = u.getFullName() != null ? u.getFullName() : (u.getName() + " " + u.getSurname()).trim();
        return (prefix + full).trim();
    }

    private String viewerTimeAgo(Instant createdAt) {
        if (createdAt == null) return "recently";
        Duration d = Duration.between(createdAt, Instant.now());
        if (d.toMinutes() < 60) return d.toMinutes() + "m ago";
        if (d.toHours() < 24) return d.toHours() + "h ago";
        long days = d.toDays();
        if (days < 7) return days + "d ago";
        return (days / 7) + "w ago";
    }

    private ExpertSummaryResponse toSummary(User u) {
        return new ExpertSummaryResponse(
                u.getId(),
                u.getFullName(),
                u.getTitlePrefix(),
                u.getAcademicRank(),
                u.getHighestQualification(),
                u.getFaculty(),
                u.getDepartment(),
                u.getUniversityEmail(),
                u.getPhoneNumber(),
                u.getConsultancyExperience(),
                u.getConsultancyAvailability(),
                u.getYearsOfConsultancyExperience(),
                u.getGeographicScope(),
                u.getPreferredConsultancyTypes(),
                u.getAreasOfExpertise(),
                u.getIndustrialAreasOfExpertise(),
                u.getSkillsAndCompetences(),
                u.getLanguagesSpoken(),
                u.getNotes(),
                u.getProfilePhotoDataUrl()
        );
    }
}
