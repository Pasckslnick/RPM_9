package com.example.pr3p7.repository;

import com.example.pr3p7.model.CarsCardModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarsCardRepository extends JpaRepository<CarsCardModel, Integer> {

    @Query("""
        select c from CarsCardModel c
        join fetch c.user
        order by c.id
    """)
    List<CarsCardModel> findAllWithUser();

    @Query("""
        select c from CarsCardModel c
        join fetch c.user
        where c.user.id = :userId
        order by c.id
    """)
    List<CarsCardModel> findAllByUserId(@Param("userId") Integer userId);

    @Query("""
        select c from CarsCardModel c
        join fetch c.user
        where lower(c.status) like lower(concat('%', :keyword, '%'))
        order by c.id
    """)
    List<CarsCardModel> search(@Param("keyword") String keyword);
}