package com.example.pr3p7.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "test_drives")
public class TestDriveModel {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Нужно выбрать пользователя")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @NotNull(message = "Нужно выбрать автомобиль")
    @ManyToOne
    @JoinColumn(name = "car_model_id", nullable = false)
    private CarModel carModel;

    @NotNull(message = "Нужно выбрать дилера")
    @ManyToOne
    @JoinColumn(name = "dealer_id", nullable = false)
    private DealerShipModel dealer;

    @NotNull(message = "Дата тест-драйва обязательна")
    @Column(name = "test_drive_date", nullable = false)
    private LocalDate testDriveDate;

    @NotBlank(message = "Статус обязателен")
    @Size(min = 2, max = 100, message = "Статус должен содержать от 2 до 100 символов")
    @Column(nullable = false, length = 100)
    private String status;

    @NotBlank(message = "Комментарий обязателен")
    @Size(min = 3, max = 255, message = "Комментарий должен содержать от 3 до 255 символов")
    @Column(nullable = false, length = 255)
    private String comment;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public CarModel getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModel carModel) {
        this.carModel = carModel;
    }

    public DealerShipModel getDealer() {
        return dealer;
    }

    public void setDealer(DealerShipModel dealer) {
        this.dealer = dealer;
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

    @Override
    public String toString() {
        String username = user == null ? "не выбран" : user.getUsername();
        String modelName = carModel == null ? "не выбрана" : carModel.getModelName();
        String dealerName = dealer == null ? "не выбран" : dealer.getDealerName();
        return "ID: " + id + ", пользователь: " + username
                + ", авто: " + modelName + ", дилер: " + dealerName
                + ", дата: " + testDriveDate;
    }
}
