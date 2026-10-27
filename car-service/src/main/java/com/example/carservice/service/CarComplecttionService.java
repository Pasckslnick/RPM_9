package com.example.carservice.service;

import com.example.carservice.exception.ResourceNotFoundException;
import com.example.carservice.model.CarComplecttionModel;
import com.example.carservice.repository.CarComplecttionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarComplecttionService {

    private final CarComplecttionRepository repository;

    public CarComplecttionService(CarComplecttionRepository repository) {
        this.repository = repository;
    }

    public List<CarComplecttionModel> getAll() {
        return repository.findAll();
    }

    public List<CarComplecttionModel> getPage(int page, int size) {
        return repository.findAll(PageRequest.of(page, size)).getContent();
    }

    public List<CarComplecttionModel> search(String keyword) {
        return repository.search(keyword == null ? "" : keyword.trim());
    }


    public List<CarComplecttionModel> getByCarModel(Integer carModelId) {
        return repository.findByCarModelId(carModelId);
    }
    public CarComplecttionModel getById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "CarComplecttion not found with id: " + id
                ));
    }

    public CarComplecttionModel save(CarComplecttionModel item) {
        repository.save(item);
        return item;
    }

    public void delete(int id) {
        repository.deleteById(id);
    }
}
