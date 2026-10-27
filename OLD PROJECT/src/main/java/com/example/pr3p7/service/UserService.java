package com.example.pr3p7.service;

import com.example.pr3p7.exception.ResourceNotFoundException;
import com.example.pr3p7.model.UserModel;
import com.example.pr3p7.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserModel> getAll() {
        return repository.findAll();
    }

    public List<UserModel> getPage(int page, int size) {
        return repository.findAll(PageRequest.of(page, size)).getContent();
    }

    public List<UserModel> search(String keyword) {
        return repository.search(keyword == null ? "" : keyword.trim());
    }

    public UserModel getById(int id) {

            return repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("CarModel not found with id: " + id));
        }

    @Transactional
    public UserModel update(int id, UserModel item) {
        UserModel existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        existing.setUsername(item.getUsername());
        existing.setEmail(item.getEmail());
        existing.setPhone(item.getPhone());
        existing.setRoleId(item.getRoleId());
        existing.setPosition(item.getPosition());

        // Пароль — только если задан явно
        String newPwd = item.getPassword();
        if (newPwd != null && !newPwd.isBlank()) {
            existing.setPassword(passwordEncoder.encode(newPwd));
        }

        return repository.save(existing);
    }


    public UserModel save(UserModel item) {
        repository.save(item);
        return item;
    }

    public void delete(int id) {
        repository.deleteById(id);
    }


    public boolean existsByUsername(String username) {
        return repository.findByUsernameIgnoreCase(username).isPresent();
    }


    public boolean existsByEmail(String email) {
        // Нужно добавить метод в репозиторий: Optional<UserModel> findByEmailIgnoreCase(String email)
        return repository.findByEmailIgnoreCase(email).isPresent();
    }

}
