package org.innov.expertdb.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.innov.expertdb.auth.dtos.login.LoginRequest;
import org.innov.expertdb.auth.dtos.register.RegisterRequest;
import org.innov.expertdb.auth.dtos.register.RegisterResponse;
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
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse createUser(RegisterRequest request, AccountStatus status, Role role) {
        User user = new User();
        user.setName(request.name());
        user.setSurname(request.surname());
        user.setEmail(request.email());
        user.setRole(role);
        user.setStatus(status);

        String hashedPassword = passwordEncoder.encode(request.password());
        user.setPasswordHash(hashedPassword);

        // Expert profile fields
        user.setTitlePrefix(request.titlePrefix());
        user.setFullName(request.fullName());
        user.setContactDetails(request.contactDetails());
        user.setAcademicRank(request.academicRank());
        user.setUniversityEmail(request.universityEmail());
        user.setPhoneNumber(request.phoneNumber());
        user.setHighestQualification(request.highestQualification());
        user.setProfessionalMemberships(request.professionalMemberships());
        user.setComplianceAccreditation(request.complianceAccreditation());
        user.setFaculty(request.faculty());
        user.setDepartment(request.department());
        user.setYearsOfConsultancyExperience(request.yearsOfConsultancyExperience());
        user.setConsultancyExperience(request.consultancyExperience());
        user.setConsultancyAvailability(request.consultancyAvailability());
        user.setPreferredConsultancyTypes(request.preferredConsultancyTypes());
        user.setGeographicScope(request.geographicScope());
        user.setSkillsAndCompetences(request.skillsAndCompetences());
        user.setLanguagesSpoken(request.languagesSpoken());
        user.setAreasOfExpertise(request.areasOfExpertise());
        user.setIndustrialAreasOfExpertise(request.industrialAreasOfExpertise());
        user.setNotes(request.notes());
        user.setProfilePhotoDataUrl(request.profilePhotoDataUrl());

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    public List<RegisterResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RegisterResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    public RegisterResponse userLogIn(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            return mapToResponse(user);
        } else {
            throw new RuntimeException("Invalid email or password");
        }
    }

    private RegisterResponse mapToResponse(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }
}