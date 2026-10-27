package com.example.carservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "cars_cards")
public class CarsCardModel {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Нужно указать пользователя")
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @NotNull(message = "Дата создания обязательна")
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @NotBlank(message = "Статус обязателен")
    @Size(min = 2, max = 100,
            message = "Статус должен содержать от 2 до 100 символов")
    @Column(nullable = false, length = 100)
    private String status;

    @JsonIgnore
    @OneToMany(mappedBy = "card")
    private List<CarsCardDetailModel> details;

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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CarsCardDetailModel> getDetails() {
        return details;
    }

    public void setDetails(List<CarsCardDetailModel> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "ID: " + id
                + ", пользователь ID: " + userId
                + ", дата: " + createdAt
                + ", статус: " + status;
    }
}