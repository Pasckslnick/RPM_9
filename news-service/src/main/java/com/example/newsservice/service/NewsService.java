
package com.example.newsservice.service;

import com.example.newsservice.model.NewsModel;
import com.example.newsservice.repository.NewsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class NewsService {
    private final NewsRepository repository;

    public NewsService(NewsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<NewsModel> getAll() {
        return repository.findAllByOrderByPublishDateDescIdDesc();
    }

    @Transactional(readOnly = true)
    public NewsModel getById(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));
    }

    @Transactional(readOnly = true)
    public List<NewsModel> search(String keyword) {
        String value = keyword == null ? "" : keyword.trim();
        return repository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                value, value);
    }

    @Transactional
    public NewsModel save(NewsModel item) {
        item.setId(null);
        return repository.save(item);
    }

    @Transactional
    public NewsModel update(Integer id, NewsModel item) {
        NewsModel existing = getById(id);
        existing.setTitle(item.getTitle());
        existing.setContent(item.getContent());
        existing.setImage(item.getImage());
        existing.setPublishDate(item.getPublishDate());
        existing.setAuthorId(item.getAuthorId());
        return repository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found");
        }
        repository.deleteById(id);
    }
}
