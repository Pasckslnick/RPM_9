package com.example.pr3p7.repository;

import com.example.pr3p7.model.CarsCardDetailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarsCardDetailRepository extends JpaRepository<CarsCardDetailModel, Integer> {

    @Query(value = """
            select * from cars_card_details
            where cast(id as text) like concat('%', :keyword, '%')
               or cast(card_id as text) like concat('%', :keyword, '%')
               or cast(complecttion_id as text) like concat('%', :keyword, '%')
            order by id
            """, nativeQuery = true)
    List<CarsCardDetailModel> search(@Param("keyword") String keyword);

    @Query("""
    select d from CarsCardDetailModel d
    join fetch d.card
    join fetch d.complecttion cc
    join fetch cc.carModel
    where d.card.id = :cardId
    order by d.id
""")
    List<CarsCardDetailModel> findByCardId(@Param("cardId") Integer cardId);

}
