package org.innov.expertdb.auth.dtos.user;

import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String fullName,
        String titlePrefix,
        String phoneNumber,
        String universityEmail,
        String bio,
        String researchInterests,
        String researchGroups,
        String faculty,
        String department,
        String academicRank,
        String highestQualification,
        String professionalMemberships,
        String complianceAccreditation,
        String yearsOfConsultancyExperience,
        String consultancyExperience,
        String consultancyAvailability,
        String geographicScope,
        List<String> preferredConsultancyTypes,
        List<String> areasOfExpertise,
        List<String> industrialAreasOfExpertise,
        List<String> skillsAndCompetences,
        List<String> languagesSpoken,
        String languageProficiencies,
        String notes,
        String profilePhotoDataUrl,
        String professionalExperiences,
        String profileLinks
) {}
