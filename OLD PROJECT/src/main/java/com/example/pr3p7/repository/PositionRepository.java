package com.example.pr3p7.repository;

import com.example.pr3p7.model.PositionModel;
import com.example.pr3p7.model.UserRoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<PositionModel, Integer> {

    @Query(value = """
            select * from positions
            where cast(id as text) like concat('%', :keyword, '%')
               or lower(position_name) like lower(concat('%', :keyword, '%'))
            order by id
            """, nativeQuery = true)
    List<PositionModel> search(@Param("keyword") String keyword);

    Optional<PositionModel> findByPositionName(String roleName);
}
