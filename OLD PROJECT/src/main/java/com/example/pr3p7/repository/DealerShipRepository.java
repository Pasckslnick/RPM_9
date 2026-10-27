package com.example.pr3p7.repository;

import com.example.pr3p7.model.DealerShipModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DealerShipRepository extends JpaRepository<DealerShipModel, Integer> {

    @Query(value = """
            select * from dealers
            where cast(id as text) like concat('%', :keyword, '%')
               or lower(dealer_name) like lower(concat('%', :keyword, '%'))
               or lower(city) like lower(concat('%', :keyword, '%'))
            order by id
            """, nativeQuery = true)
    List<DealerShipModel> search(@Param("keyword") String keyword);
}
