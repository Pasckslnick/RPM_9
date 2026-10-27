package com.example.carservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "car_complecttions")
public class CarComplecttionModel {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Нужно выбрать модель автомобиля")
    @ManyToOne
    @JoinColumn(name = "car_model_id", nullable = false)
    private CarModel carModel;

    @NotBlank(message = "Название комплектации обязательно")
    @Size(min = 2, max = 120, message = "Название комплектации должно содержать от 2 до 120 символов")
    @Column(name = "configuration_name", nullable = false, length = 120)
    private String configurationName;

    @NotNull(message = "Емкость батареи обязательна")
    @DecimalMin(value = "0.1", message = "Емкость батареи должна быть больше 0")
    @Column(name = "battery_capacity", nullable = false, precision = 8, scale = 2)
    private BigDecimal batteryCapacity;

    @NotNull(message = "Мощность обязательна")
    @Min(value = 1, message = "Мощность должна быть больше 0")
    @Column(name = "power_hp", nullable = false)
    private Integer powerHp;

    @NotNull(message = "Запас хода обязателен")
    @Min(value = 1, message = "Запас хода должен быть больше 0")
    @Column(name = "range_km", nullable = false)
    private Integer rangeKm;

    @NotNull(message = "Максимальная скорость обязательна")
    @Min(value = 1, message = "Максимальная скорость должна быть больше 0")
    @Column(name = "max_speed", nullable = false)
    private Integer maxSpeed;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CarModel getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModel carModel) {
        this.carModel = carModel;
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public void setConfigurationName(String configurationName) {
        this.configurationName = configurationName;
    }

    public BigDecimal getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(BigDecimal batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public Integer getPowerHp() {
        return powerHp;
    }

    public void setPowerHp(Integer powerHp) {
        this.powerHp = powerHp;
    }

    public Integer getRangeKm() {
        return rangeKm;
    }

    public void setRangeKm(Integer rangeKm) {
        this.rangeKm = rangeKm;
    }

    public Integer getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Integer maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        String modelName = carModel == null ? "не выбрана" : carModel.getModelName();
        return "ID: " + id + ", комплектация: " + configurationName
                + ", модель: " + modelName + ", цена: " + price;
    }
}
