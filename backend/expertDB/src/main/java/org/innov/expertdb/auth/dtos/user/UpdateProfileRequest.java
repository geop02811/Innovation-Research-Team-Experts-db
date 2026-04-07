package org.innov.expertdb.auth.dtos.user;

public record UpdateProfileRequest(
        String fullName,
        String titlePrefix,
        String phoneNumber,
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
        String preferredConsultancyTypes,
        String areasOfExpertise,
        String industrialAreasOfExpertise,
        String skillsAndCompetences,
        String languagesSpoken,
        String notes,
        String profilePhotoDataUrl
) {}
