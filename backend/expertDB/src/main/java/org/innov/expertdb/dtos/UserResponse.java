package org.innov.expertdb.dtos;

import org.innov.expertdb.models.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String fullName,
    TitlePrefix titlePrefix,
    AcademicRank academicRank,
    String universityEmail,
    String phoneNumber,
    String highestQualification,
    String professionalMemberships,
    String complianceAccreditation,
    String faculty,
    Department department,
    YearsOfExperience yearsConsultancyExperience,
    String consultancyExperience,
    Availability consultancyAvailability,
    Set<String> preferredConsultancyTypes,
    GeographicScope geographicScope,
    Set<String> skillsCompetences,
    Set<String> languagesSpoken,
    Set<String> areasOfExpertise,
    Set<String> industrialAreasExpertise,
    String notes,
    Set<Role> roles,
    UserStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}