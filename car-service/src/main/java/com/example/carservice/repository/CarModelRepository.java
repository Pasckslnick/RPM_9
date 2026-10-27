package com.example.carservice.repository;

import com.example.carservice.model.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarModelRepository extends JpaRepository<CarModel, Integer> {

    @Query(value = """
            select * from car_models
            where cast(id as text) like concat('%', :keyword, '%')
               or lower(model_name) like lower(concat('%', :keyword, '%'))
               or lower(body_type) like lower(concat('%', :keyword, '%'))
            order by id
            """, nativeQuery = true)
    List<CarModel> search(@Param("keyword") String keyword);
}
