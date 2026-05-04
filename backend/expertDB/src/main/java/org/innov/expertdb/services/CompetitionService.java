package org.innov.expertdb.services;

import java.util.List;
import java.util.UUID;

import org.innov.expertdb.auth.dtos.competitions.CompetitionRequest;
import org.innov.expertdb.auth.dtos.competitions.CompetitionResponse;
import org.innov.expertdb.classes.Competition;
import org.innov.expertdb.repos.CompetitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository competitionRepository;

    public List<CompetitionResponse> getAll() {
        return competitionRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(CompetitionResponse::from).toList();
    }

    @Transactional
    public CompetitionResponse create(CompetitionRequest req) {
        Competition c = new Competition();
        apply(c, req);
        return CompetitionResponse.from(competitionRepository.save(c));
    }

    @Transactional
    public CompetitionResponse update(UUID id, CompetitionRequest req) {
        Competition c = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competition not found"));
        apply(c, req);
        return CompetitionResponse.from(competitionRepository.save(c));
    }

    @Transactional
    public void delete(UUID id) {
        competitionRepository.deleteById(id);
    }

    private void apply(Competition c, CompetitionRequest req) {
        c.setTitle(req.title());
        c.setDescription(req.description());
        c.setDeadline(req.deadline());
        c.setStatus(req.status() != null ? req.status() : "OPEN");
        c.setPrize(req.prize());
        c.setCtaLabel(req.ctaLabel());
        c.setCtaUrl(req.ctaUrl());
    }
}
