package com.example.carservice.service;

import com.example.carservice.exception.ResourceNotFoundException;
import com.example.carservice.model.CarModel;
import com.example.carservice.repository.CarModelRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarModelService {

    private final CarModelRepository repository;

    public CarModelService(CarModelRepository repository) {
        this.repository = repository;
    }

    public List<CarModel> getAll() {
        return repository.findAll();
    }

    public List<CarModel> getPage(int page, int size) {
        return repository.findAll(PageRequest.of(page, size)).getContent();
    }

    public List<CarModel> search(String keyword) {
        return repository.search(keyword == null ? "" : keyword.trim());
    }

    public CarModel getById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CarModel not found with id: " + id));
    }

    public CarModel save(CarModel item) {
        repository.save(item);
        return item;
    }

    public void delete(int id) {
        repository.deleteById(id);
    }
}
