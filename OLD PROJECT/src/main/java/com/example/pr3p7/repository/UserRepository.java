package com.example.pr3p7.repository;

import com.example.pr3p7.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Integer> {

    @Query(value = """
            select * from users
            where cast(id as text) like concat('%', :keyword, '%')
               or lower(username) like lower(concat('%', :keyword, '%'))
               or lower(email) like lower(concat('%', :keyword, '%'))
            order by id
            """, nativeQuery = true)
    List<UserModel> search(@Param("keyword") String keyword);

    Optional<UserModel> findByUsernameIgnoreCase(String username);
    Optional<UserModel> findByEmailIgnoreCase(String email);
}
