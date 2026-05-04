package org.innov.expertdb.auth.dtos.alumni;

public record AlumniNewsRequest(
        String personName,
        String headline,
        String body,
        String newsDate) {
}
