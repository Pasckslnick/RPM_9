package com.example.pr3p7.service;

import com.example.pr3p7.exception.ResourceNotFoundException;
import com.example.pr3p7.model.PositionModel;
import com.example.pr3p7.repository.PositionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {

    private final PositionRepository repository;

    public PositionService(PositionRepository repository) {
        this.repository = repository;
    }

    public List<PositionModel> getAll() {
        return repository.findAll();
    }

    public List<PositionModel> getPage(int page, int size) {
        return repository.findAll(PageRequest.of(page, size)).getContent();
    }

    public List<PositionModel> search(String keyword) {
        return repository.search(keyword == null ? "" : keyword.trim());
    }

    public PositionModel getById(int id) {
            return repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("CarModel not found with id: " + id));
        }

    public PositionModel save(PositionModel item) {
        repository.save(item);
        return item;
    }

    public void delete(int id) {
        repository.deleteById(id);
    }
}
