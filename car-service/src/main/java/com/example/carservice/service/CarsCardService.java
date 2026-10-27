package com.example.carservice.service;

import com.example.carservice.exception.ResourceNotFoundException;
import com.example.carservice.model.CarsCardModel;
import com.example.carservice.repository.CarsCardRepository;
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
        if (page < 0 || size < 1) {
            throw new IllegalArgumentException(
                    "Номер страницы должен быть неотрицательным, размер — положительным"
            );
        }

        return repository.findAll(PageRequest.of(page, size)).getContent();
    }

    @Transactional(readOnly = true)
    public List<CarsCardModel> search(String keyword) {
        return repository.search(keyword == null ? "" : keyword.trim());
    }

    @Transactional(readOnly = true)
    public CarsCardModel getById(int id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Card not found: " + id));
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
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Card not found: " + id);
        }

        repository.deleteById(id);
    }
}