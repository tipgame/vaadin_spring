package de.tipgame.backend.service;

import de.tipgame.backend.data.entity.FinalResultEntity;
import de.tipgame.backend.repository.FinalResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinalResultService {

    private FinalResultRepository finalResultRepository;

    public FinalResultService(FinalResultRepository finalResultRepository) {
        this.finalResultRepository = finalResultRepository;
    }

    public Iterable<FinalResultEntity> getFinalResults() {
        return finalResultRepository.findAll();
    }
}
