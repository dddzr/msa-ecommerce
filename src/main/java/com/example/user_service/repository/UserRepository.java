package com.example.user_service.repository;

import com.example.user_service.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    public Optional<User> findByUsername(String username);

    public boolean existsByUsername(String username);

    public boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.user_id = :id")
    Optional<User> findByUserIdCustom(@Param("id") Integer id);
    
}
