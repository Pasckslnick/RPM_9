package com.example.pr3p7.repository;

import com.example.pr3p7.model.UserRoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRoleModel, Integer> {

    @Query(value = """
            select * from user_roles
            where cast(id as text) like concat('%', :keyword, '%')
               or lower(role_name) like lower(concat('%', :keyword, '%'))
            order by id
            """, nativeQuery = true)
    List<UserRoleModel> search(@Param("keyword") String keyword);

    Optional<UserRoleModel> findByRoleName(String roleName);
}
