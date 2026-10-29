
package com.example.newsservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "news")
public class NewsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(min = 3, max = 150)
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank
    @Size(min = 10, max = 2000)
    @Column(nullable = false, length = 2000)
    private String content;

    @NotBlank
    @Size(min = 3, max = 255)
    @Column(nullable = false, length = 255)
    private String image;

    @NotNull
    @Column(name = "publish_date", nullable = false)
    private LocalDate publishDate;

    @NotNull
    @Column(name = "author_id", nullable = false)
    private Integer authorId;

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
    public Integer getAuthorId() { return authorId; }
    public void setAuthorId(Integer authorId) { this.authorId = authorId; }
}
