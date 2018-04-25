package de.tipgame.backend.service;

import de.tipgame.backend.data.entity.StatisticTimelineEntity;
import de.tipgame.backend.repository.StatisticTimelineRepository;
import org.springframework.stereotype.Service;

@Service
public class StatisticTimelineService {

    private StatisticTimelineRepository statisticTimelineRepository;

    public StatisticTimelineService(StatisticTimelineRepository statisticTimelineRepository) {
        this.statisticTimelineRepository = statisticTimelineRepository;
    }

    public void saveStatisticTimeline(StatisticTimelineEntity statisticTimelineEntity) {
        statisticTimelineRepository.save(statisticTimelineEntity);
    }
}
