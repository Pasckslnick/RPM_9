package com.example.pr3p7.service;

import com.example.pr3p7.exception.ResourceNotFoundException;
import com.example.pr3p7.model.UserRoleModel;
import com.example.pr3p7.repository.UserRoleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService {

    private final UserRoleRepository repository;

    public UserRoleService(UserRoleRepository repository) {
        this.repository = repository;
    }

    public List<UserRoleModel> getAll() {
        return repository.findAll();
    }

    public List<UserRoleModel> getPage(int page, int size) {
        return repository.findAll(PageRequest.of(page, size)).getContent();
    }

    public List<UserRoleModel> search(String keyword) {
        return repository.search(keyword == null ? "" : keyword.trim());
    }

    public UserRoleModel getById(int id) {

            return repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("CarModel not found with id: " + id));
        }

    public UserRoleModel save(UserRoleModel item) {
        repository.save(item);
        return item;
    }

    public void delete(int id) {
        repository.deleteById(id);
    }
}
