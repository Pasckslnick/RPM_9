package com.example.pr3p7.service;

import com.example.pr3p7.exception.ResourceNotFoundException;
import com.example.pr3p7.model.DealerShipModel;
import com.example.pr3p7.repository.DealerShipRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealerShipService {

    private final DealerShipRepository repository;

    public DealerShipService(DealerShipRepository repository) {
        this.repository = repository;
    }

    public List<DealerShipModel> getAll() {
        return repository.findAll();
    }

    public List<DealerShipModel> getPage(int page, int size) {
        return repository.findAll(PageRequest.of(page, size)).getContent();
    }

    public List<DealerShipModel> search(String keyword) {
        return repository.search(keyword == null ? "" : keyword.trim());
    }

    public DealerShipModel getById(int id) {

            return repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("CarModel not found with id: " + id));
        }

    public DealerShipModel save(DealerShipModel item) {
        repository.save(item);
        return item;
    }

    public void delete(int id) {
        repository.deleteById(id);
    }
}
