package com.example.carservice.repository;

import com.example.carservice.model.CarComplecttionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarComplecttionRepository extends JpaRepository<CarComplecttionModel, Integer> {

    @Query(value = """
            select * from car_complecttions
            where cast(id as text) like concat('%', :keyword, '%')
               or lower(configuration_name) like lower(concat('%', :keyword, '%'))
            order by id
            """, nativeQuery = true)
    List<CarComplecttionModel> search(@Param("keyword") String keyword);

    List<CarComplecttionModel> findByCarModelId(Integer carModelId);
}
