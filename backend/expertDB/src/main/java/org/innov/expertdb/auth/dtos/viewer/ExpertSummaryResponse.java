package org.innov.expertdb.auth.dtos.viewer;

import java.util.UUID;

public record ExpertSummaryResponse(
        UUID id,
        String fullName,
        String titlePrefix,
        String academicRank,
        String highestQualification,
        String faculty,
        String department,
        String universityEmail,
        String phoneNumber,
        String consultancyExperience,
        String consultancyAvailability,
        String yearsOfConsultancyExperience,
        String geographicScope,
        String preferredConsultancyTypes,
        String areasOfExpertise,
        String industrialAreasOfExpertise,
        String skillsAndCompetences,
        String languagesSpoken,
        String languageProficiencies,
        String notes,
        String profilePhotoDataUrl,
        String professionalExperiences,
        String profileLinks
) {}
