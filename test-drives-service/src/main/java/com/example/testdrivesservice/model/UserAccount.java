package com.example.testdrivesservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserAccount {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String username;

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
