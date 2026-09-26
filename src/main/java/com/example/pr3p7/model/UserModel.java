package com.example.pr3p7.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Entity
@Table(name = "users")
public class UserModel {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<CarsCardModel> cards;

    @JsonIgnore
    @OneToMany(mappedBy = "author")
    private List<NewsModel> news;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<TestDriveModel> testDrives;

    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 50, message = "Логин должен содержать от 3 до 50 символов")
    @Column(nullable = false, length = 50)
    private String username;

    @Size(min = 3, max = 100, message = "Пароль должен содержать от 3 до 100 символов")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    private String password;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Введите корректный email")
    @Column(nullable = false, length = 120, unique = true)
    private String email;

    @NotBlank(message = "Телефон обязателен")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Телефон должен содержать от 10 до 15 цифр")
    @Column(nullable = false, length = 20)
    private String phone;

    @NotNull(message = "Нужно выбрать роль")
    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @NotNull(message = "Нужно выбрать должность")
    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private PositionModel position;

    // Геттеры и сеттеры
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public PositionModel getPosition() {
        return position;
    }

    public void setPosition(PositionModel position) {
        this.position = position;
    }

    public List<CarsCardModel> getCards() {
        return cards;
    }

    public void setCards(List<CarsCardModel> cards) {
        this.cards = cards;
    }

    public List<NewsModel> getNews() {
        return news;
    }

    public void setNews(List<NewsModel> news) {
        this.news = news;
    }

    public List<TestDriveModel> getTestDrives() {
        return testDrives;
    }

    public void setTestDrives(List<TestDriveModel> testDrives) {
        this.testDrives = testDrives;
    }

    @Override
    public String toString() {
        String positionName = position == null ? "не выбрана" : position.getPositionName();
        return "ID: " + id + ", логин: " + username
                + ", email: " + email + ", роль ID: " + roleId
                + ", должность: " + positionName;
    }
}