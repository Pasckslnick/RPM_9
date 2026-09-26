package com.example.pr3p7.model;

import com.example.pr3p7.exception.ResourceNotFoundException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "car_models")
public class CarModel {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Название модели обязательно")
    @Size(min = 2, max = 120, message = "Название модели должно содержать от 2 до 120 символов")
    @Column(name = "model_name", nullable = false, length = 120)
    private String modelName;

    @NotBlank(message = "Тип кузова обязателен")
    @Size(min = 2, max = 80, message = "Тип кузова должен содержать от 2 до 80 символов")
    @Column(name = "body_type", nullable = false, length = 80)
    private String bodyType;

    @NotBlank(message = "Описание обязательно")
    @Size(min = 5, max = 500, message = "Описание должно содержать от 5 до 500 символов")
    @Column(nullable = false, length = 500)
    private String description;

    @NotNull(message = "Базовая цена обязательна")
    @DecimalMin(value = "0.01", message = "Базовая цена должна быть больше 0")
    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @NotNull(message = "Год выпуска обязателен")
    @Min(value = 2000, message = "Год выпуска должен быть не меньше 2000")
    @Max(value = 2100, message = "Год выпуска должен быть реалистичным")
    @Column(name = "release_year", nullable = false)
    private Integer releaseYear;

    @NotNull(message = "Нужно указать доступность")
    @Column(nullable = false)
    private Boolean available = Boolean.TRUE;

    @JsonIgnore
    @OneToMany(mappedBy = "carModel")
    private List<CarComplecttionModel> complecttions;

    @JsonIgnore
    @OneToMany(mappedBy = "carModel")
    private List<TestDriveModel> testDrives;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public List<CarComplecttionModel> getComplecttions() {
        return complecttions;
    }

    public void setComplecttions(List<CarComplecttionModel> complecttions) {
        this.complecttions = complecttions;
    }

    public List<TestDriveModel> getTestDrives() {
        return testDrives;
    }

    public void setTestDrives(List<TestDriveModel> testDrives) {
        this.testDrives = testDrives;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", модель: " + modelName + ", кузов: " + bodyType
                + ", цена: " + basePrice + ", год: " + releaseYear;
    }
}
