package com.example.pr3p7.service;

import com.example.pr3p7.exception.ResourceNotFoundException;
import com.example.pr3p7.model.TestDriveModel;
import com.example.pr3p7.repository.TestDriveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestDriveService {

    private final TestDriveRepository repository;

    public TestDriveService(TestDriveRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TestDriveModel> getAll() {
        return repository.findAllWithRefs();
    }

    @Transactional(readOnly = true)
    public List<TestDriveModel> getPage(int page, int size) {
        List<TestDriveModel> all = repository.findAllWithRefs();
        int from = Math.min(page * size, all.size());
        int to   = Math.min(from + size, all.size());
        return all.subList(from, to);
    }

    @Transactional(readOnly = true)
    public List<TestDriveModel> search(String keyword) {
        return repository.searchWithRefs(keyword == null ? "" : keyword.trim());
    }

    @Transactional(readOnly = true)
    public TestDriveModel getById(int id) {
        return repository.findByIdWithRefs(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestDrive not found with id: " + id));
    }

    @Transactional
    public TestDriveModel save(TestDriveModel item) {
        return repository.save(item);
    }

    @Transactional
    public void delete(int id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TestDriveModel> getByUserId(Integer userId) {
        return repository.findAllByUserId(userId);
    }
}