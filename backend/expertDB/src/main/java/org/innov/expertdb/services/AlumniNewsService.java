package org.innov.expertdb.services;

import java.util.List;
import java.util.UUID;

import org.innov.expertdb.auth.dtos.alumni.AlumniNewsRequest;
import org.innov.expertdb.auth.dtos.alumni.AlumniNewsResponse;
import org.innov.expertdb.classes.AlumniNews;
import org.innov.expertdb.repos.AlumniNewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlumniNewsService {

    private final AlumniNewsRepository alumniNewsRepository;

    public List<AlumniNewsResponse> getAll() {
        return alumniNewsRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(AlumniNewsResponse::from).toList();
    }

    @Transactional
    public AlumniNewsResponse create(AlumniNewsRequest req) {
        AlumniNews a = new AlumniNews();
        apply(a, req);
        return AlumniNewsResponse.from(alumniNewsRepository.save(a));
    }

    @Transactional
    public AlumniNewsResponse update(UUID id, AlumniNewsRequest req) {
        AlumniNews a = alumniNewsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumni news not found"));
        apply(a, req);
        return AlumniNewsResponse.from(alumniNewsRepository.save(a));
    }

    @Transactional
    public void delete(UUID id) {
        alumniNewsRepository.deleteById(id);
    }

    private void apply(AlumniNews a, AlumniNewsRequest req) {
        a.setPersonName(req.personName());
        a.setHeadline(req.headline());
        a.setBody(req.body());
        a.setNewsDate(req.newsDate());
    }
}
