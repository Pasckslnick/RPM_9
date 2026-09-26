package com.example.pr3p7.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @NotNull(message = "Нужно выбрать пользователя")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer", "handler",
            "password", "cards", "news", "testDrives", "position"
    })
    private UserModel user;

    @NotNull(message = "Дата создания обязательна")
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @NotBlank(message = "Статус обязателен")
    @Size(min = 2, max = 100, message = "Статус должен содержать от 2 до 100 символов")
    @Column(nullable = false, length = 100)
    private String status;

    @JsonIgnore                          // ← ЭТО ИСПРАВЛЯЕТ 500
    @OneToMany(mappedBy = "card")
    private List<CarsCardDetailModel> details;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public UserModel getUser() { return user; }
    public void setUser(UserModel user) { this.user = user; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<CarsCardDetailModel> getDetails() { return details; }
    public void setDetails(List<CarsCardDetailModel> details) { this.details = details; }

    @Override
    public String toString() {
        String username = user == null ? "не выбран" : user.getUsername();
        return "ID: " + id + ", пользователь: " + username
                + ", дата: " + createdAt + ", статус: " + status;
    }
}