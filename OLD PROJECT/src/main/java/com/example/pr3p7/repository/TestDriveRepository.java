package com.example.pr3p7.repository;

import com.example.pr3p7.model.TestDriveModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TestDriveRepository extends JpaRepository<TestDriveModel, Integer> {

    @Query("""
        select td from TestDriveModel td
        join fetch td.user
        join fetch td.carModel
        join fetch td.dealer
        order by td.id
    """)
    List<TestDriveModel> findAllWithRefs();

    @Query("""
        select td from TestDriveModel td
        join fetch td.user
        join fetch td.carModel
        join fetch td.dealer
        where td.id = :id
    """)
    Optional<TestDriveModel> findByIdWithRefs(@Param("id") Integer id);

    @Query("""
        select td from TestDriveModel td
        join fetch td.user
        join fetch td.carModel
        join fetch td.dealer
        where lower(td.status)  like lower(concat('%', :keyword, '%'))
           or lower(td.comment) like lower(concat('%', :keyword, '%'))
        order by td.id
    """)
    List<TestDriveModel> searchWithRefs(@Param("keyword") String keyword);

    @Query("""
        select td from TestDriveModel td
        join fetch td.user
        join fetch td.carModel
        join fetch td.dealer
        where td.user.id = :userId
        order by td.id
    """)
    List<TestDriveModel> findAllByUserId(@Param("userId") Integer userId);
}