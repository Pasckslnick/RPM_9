package com.example.testdrivesservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "test_drives")
public class TestDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Нужно выбрать пользователя")
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @NotNull(message = "Нужно выбрать автомобиль")
    @Column(name = "car_model_id", nullable = false)
    private Integer carModelId;

    @NotNull(message = "Нужно выбрать дилера")
    @Column(name = "dealer_id", nullable = false)
    private Integer dealerId;

    @NotNull(message = "Дата тест-драйва обязательна")
    @Column(name = "test_drive_date", nullable = false)
    private LocalDate testDriveDate;

    @NotBlank(message = "Статус обязателен")
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String status = "PENDING";

    @NotBlank(message = "Комментарий обязателен")
    @Size(min = 3, max = 255)
    @Column(nullable = false, length = 255)
    private String comment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCarModelId() {
        return carModelId;
    }

    public void setCarModelId(Integer carModelId) {
        this.carModelId = carModelId;
    }

    public Integer getDealerId() {
        return dealerId;
    }

    public void setDealerId(Integer dealerId) {
        this.dealerId = dealerId;
    }

    public LocalDate getTestDriveDate() {
        return testDriveDate;
    }

    public void setTestDriveDate(LocalDate testDriveDate) {
        this.testDriveDate = testDriveDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
