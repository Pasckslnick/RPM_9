package com.example.pr3p7.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "news")
public class NewsModel {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Заголовок обязателен")
    @Size(min = 3, max = 150, message = "Заголовок должен содержать от 3 до 150 символов")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "Содержание обязательно")
    @Size(min = 10, max = 2000, message = "Содержание должно содержать от 10 до 2000 символов")
    @Column(nullable = false, length = 2000)
    private String content;

    @NotBlank(message = "Ссылка на изображение обязательна")
    @Size(min = 3, max = 255, message = "Путь к изображению должен содержать от 3 до 255 символов")
    @Column(nullable = false, length = 255)
    private String image;

    @NotNull(message = "Дата публикации обязательна")
    @Column(name = "publish_date", nullable = false)
    private LocalDate publishDate;

    @NotNull(message = "Нужно выбрать автора")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnoreProperties({
            "hibernateLazyInitializer", "handler",
            "password", "cards", "news", "testDrives", "position"
    })
    private UserModel author;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public LocalDate getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDate publishDate) { this.publishDate = publishDate; }

    public UserModel getAuthor() { return author; }
    public void setAuthor(UserModel author) { this.author = author; }

    @Override
    public String toString() {
        String username = author == null ? "не выбран" : author.getUsername();
        return "ID: " + id + ", новость: " + title + ", дата: " + publishDate
                + ", автор: " + username;
    }
}