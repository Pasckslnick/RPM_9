
package com.example.newsservice.repository;

import com.example.newsservice.model.NewsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NewsRepository extends JpaRepository<NewsModel, Integer> {
    List<NewsModel> findAllByOrderByPublishDateDescIdDesc();

    List<NewsModel> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title, String content);
}
