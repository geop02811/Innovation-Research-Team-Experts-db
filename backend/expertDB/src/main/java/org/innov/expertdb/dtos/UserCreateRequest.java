package org.innov.expertdb.dtos;

import org.innov.expertdb.models.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record UserCreateRequest(
    
    @NotBlank(message = "Full name is required")
    String fullName,
    
    TitlePrefix titlePrefix,
    
    AcademicRank academicRank,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String universityEmail,
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password,
    
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
    
    Set<Role> roles
) {}