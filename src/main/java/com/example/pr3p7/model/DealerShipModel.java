package com.example.pr3p7.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "dealers")
public class DealerShipModel {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Название дилера обязательно")
    @Size(min = 2, max = 120, message = "Название дилера должно содержать от 2 до 120 символов")
    @Column(name = "dealer_name", nullable = false, length = 120)
    private String dealerName;

    @NotBlank(message = "Город обязателен")
    @Size(min = 2, max = 80, message = "Название города должно содержать от 2 до 80 символов")
    @Column(nullable = false, length = 80)
    private String city;

    @NotBlank(message = "Адрес обязателен")
    @Size(min = 5, max = 200, message = "Адрес должен содержать от 5 до 200 символов")
    @Column(nullable = false, length = 200)
    private String address;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Телефон должен содержать от 10 до 15 цифр")
    @Column(nullable = false, length = 20)
    private String phone;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Введите корректный email")
    @Column(nullable = false, length = 120)
    private String email;

    @NotBlank(message = "Часы работы обязательны")
    @Size(min = 3, max = 100, message = "Часы работы должны содержать от 3 до 100 символов")
    @Column(name = "work_hours", nullable = false, length = 100)
    private String workHours;

    @JsonIgnore
    @OneToMany(mappedBy = "dealer")
    private List<TestDriveModel> testDrives;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWorkHours() {
        return workHours;
    }

    public void setWorkHours(String workHours) {
        this.workHours = workHours;
    }

    public List<TestDriveModel> getTestDrives() {
        return testDrives;
    }

    public void setTestDrives(List<TestDriveModel> testDrives) {
        this.testDrives = testDrives;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", дилер: " + dealerName + ", город: " + city;
    }
}
