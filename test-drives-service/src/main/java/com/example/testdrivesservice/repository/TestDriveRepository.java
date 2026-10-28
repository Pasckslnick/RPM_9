package com.example.testdrivesservice.repository;

import com.example.testdrivesservice.model.TestDrive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestDriveRepository extends JpaRepository<TestDrive, Integer> {

    List<TestDrive> findByUserIdOrderByIdDesc(Integer userId);

    List<TestDrive> findByStatusIgnoreCase(String status);

    List<TestDrive> findByCommentContainingIgnoreCase(String comment);
}
