package com.jam2in.example.user.repository;

import com.jam2in.example.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(@Param("username") String username);

  boolean existsByUsername(@Param("username") String username);
}
