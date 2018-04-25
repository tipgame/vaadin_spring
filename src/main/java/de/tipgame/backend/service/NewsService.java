package de.tipgame.backend.service;

import de.tipgame.backend.data.entity.NewsEntity;
import de.tipgame.backend.repository.NewsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {
    private NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public void saveNews(NewsEntity newsEntity) {
        newsRepository.save(newsEntity);
    }

    public List<NewsEntity> getAllNewsOrderdByIdDesc() {
        return newsRepository.findByOrderByIdDesc();
    }
}
