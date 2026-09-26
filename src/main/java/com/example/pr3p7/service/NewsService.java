package com.example.pr3p7.service;

import com.example.pr3p7.exception.ResourceNotFoundException;
import com.example.pr3p7.model.NewsModel;
import com.example.pr3p7.repository.NewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NewsService {

    private final NewsRepository repository;

    public NewsService(NewsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<NewsModel> getAll() {
        return repository.findAllWithAuthor();
    }

    @Transactional(readOnly = true)
    public List<NewsModel> getPage(int page, int size) {
        List<NewsModel> all = repository.findAllWithAuthor();
        int from = Math.min(page * size, all.size());
        int to   = Math.min(from + size, all.size());
        return all.subList(from, to);
    }

    @Transactional(readOnly = true)
    public List<NewsModel> search(String keyword) {
        return repository.searchWithAuthor(keyword == null ? "" : keyword.trim());
    }

    @Transactional(readOnly = true)
    public NewsModel getById(int id) {
        return repository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("News not found with id: " + id));
    }

    @Transactional
    public NewsModel save(NewsModel item) {
        return repository.save(item);
    }

    @Transactional
    public void delete(int id) {
        repository.deleteById(id);
    }
}