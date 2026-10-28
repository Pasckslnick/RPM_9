package com.example.testdrivesservice.service;

import com.example.testdrivesservice.model.TestDrive;
import com.example.testdrivesservice.repository.TestDriveRepository;
import com.example.testdrivesservice.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TestDriveService {

    private final TestDriveRepository repository;
    private final UserAccountRepository userRepository;

    public TestDriveService(
            TestDriveRepository repository,
            UserAccountRepository userRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<TestDrive> findAll() {
        return repository.findAll();
    }

    public List<TestDrive> findByUserId(Integer userId) {
        return repository.findByUserIdOrderByIdDesc(userId);
    }

    public List<TestDrive> search(String query) {
        return repository
                .findByCommentContainingIgnoreCase(query);
    }

    public TestDrive findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Тест-драйв с ID " + id + " не найден"
                ));
    }

    public TestDrive create(TestDrive testDrive) {
        testDrive.setId(null);

        if (testDrive.getStatus() == null
                || testDrive.getStatus().isBlank()) {
            testDrive.setStatus("PENDING");
        }

        return repository.save(testDrive);
    }

    public Integer getUserId(String username) {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Пользователь не найден"))
                .getId();
    }

    public TestDrive findByIdForUser(Integer id, Integer userId) {
        TestDrive testDrive = findById(id);
        if (!testDrive.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Нет доступа к этой записи");
        }
        return testDrive;
    }

    public TestDrive update(Integer id, TestDrive updated) {
        TestDrive existing = findById(id);

        existing.setCarModelId(updated.getCarModelId());
        existing.setDealerId(updated.getDealerId());
        existing.setTestDriveDate(updated.getTestDriveDate());
        existing.setStatus(updated.getStatus());
        existing.setComment(updated.getComment());

        return repository.save(existing);
    }

    public void delete(Integer id) {
        TestDrive existing = findById(id);
        repository.delete(existing);
    }
}
