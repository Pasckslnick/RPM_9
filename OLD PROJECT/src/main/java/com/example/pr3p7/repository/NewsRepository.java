package com.example.pr3p7.repository;

import com.example.pr3p7.model.NewsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<NewsModel, Integer> {

    @Query("""
        select n from NewsModel n
        join fetch n.author
        order by n.publishDate desc, n.id desc
    """)
    List<NewsModel> findAllWithAuthor();

    @Query("""
        select n from NewsModel n
        join fetch n.author
        where n.id = :id
    """)
    Optional<NewsModel> findByIdWithAuthor(@Param("id") Integer id);

    @Query("""
        select n from NewsModel n
        join fetch n.author
        where lower(n.title) like lower(concat('%', :keyword, '%'))
           or lower(n.content) like lower(concat('%', :keyword, '%'))
        order by n.publishDate desc, n.id desc
    """)
    List<NewsModel> searchWithAuthor(@Param("keyword") String keyword);
}