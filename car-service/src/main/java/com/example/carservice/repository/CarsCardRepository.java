package com.example.carservice.repository;

import com.example.carservice.model.CarsCardModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarsCardRepository extends JpaRepository<CarsCardModel, Integer> {

    @Query("""
        select c from CarsCardModel c
        order by c.id
    """)
    List<CarsCardModel> findAllWithUser();

    @Query("""
        select c from CarsCardModel c
        where c.userId = :userId
        order by c.id
    """)
    List<CarsCardModel> findAllByUserId(@Param("userId") Integer userId);

    @Query("""
        select c from CarsCardModel c
        where lower(c.status) like lower(concat('%', :keyword, '%'))
        order by c.id
    """)
    List<CarsCardModel> search(@Param("keyword") String keyword);
}