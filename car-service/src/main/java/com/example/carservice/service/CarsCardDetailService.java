package com.example.carservice.service;

import com.example.carservice.exception.ResourceNotFoundException;
import com.example.carservice.model.CarsCardDetailModel;
import com.example.carservice.repository.CarsCardDetailRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarsCardDetailService {

    private final CarsCardDetailRepository repository;

    public CarsCardDetailService(CarsCardDetailRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CarsCardDetailModel> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<CarsCardDetailModel> getPage(int page, int size) {
        return repository.findAll(PageRequest.of(page, size)).getContent();
    }

    @Transactional(readOnly = true)
    public List<CarsCardDetailModel> search(String keyword) {
        return repository.search(keyword == null ? "" : keyword.trim());
    }

    @Transactional(readOnly = true)
    public CarsCardDetailModel getById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CardDetail not found: " + id));
    }

    /** Позиции конкретной корзины */
    @Transactional(readOnly = true)
    public List<CarsCardDetailModel> getByCardId(Integer cardId) {
        return repository.findByCardId(cardId);
    }

    @Transactional
    public CarsCardDetailModel save(CarsCardDetailModel item) {
        return repository.save(item);
    }

    @Transactional
    public void delete(int id) {
        repository.deleteById(id);
    }
}