package org.innov.expertdb.auth.dtos.alumni;

import java.util.UUID;

import org.innov.expertdb.classes.AlumniNews;

public record AlumniNewsResponse(
        UUID id,
        String personName,
        String headline,
        String body,
        String newsDate) {

    public static AlumniNewsResponse from(AlumniNews a) {
        return new AlumniNewsResponse(a.getId(), a.getPersonName(),
                a.getHeadline(), a.getBody(), a.getNewsDate());
    }
}
