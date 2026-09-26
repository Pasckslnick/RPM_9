package com.example.pr3p7.service;

import com.example.pr3p7.exception.ResourceNotFoundException;
import com.example.pr3p7.model.CarsCardModel;
import com.example.pr3p7.repository.CarsCardRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarsCardService {

    private final CarsCardRepository repository;

    public CarsCardService(CarsCardRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CarsCardModel> getAll() {
        return repository.findAllWithUser();
    }

    @Transactional(readOnly = true)
    public List<CarsCardModel> getPage(int page, int size) {
        List<CarsCardModel> all = repository.findAllWithUser();
        int from = Math.min(page * size, all.size());
        int to   = Math.min(from + size, all.size());
        return all.subList(from, to);
    }

    @Transactional(readOnly = true)
    public List<CarsCardModel> search(String keyword) {
        return repository.search(keyword == null ? "" : keyword.trim());
    }

    @Transactional(readOnly = true)
    public CarsCardModel getById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<CarsCardModel> getByUserId(Integer userId) {
        return repository.findAllByUserId(userId);
    }

    @Transactional
    public CarsCardModel save(CarsCardModel item) {
        return repository.save(item);
    }

    @Transactional
    public void delete(int id) {
        repository.deleteById(id);
    }
}