package com.jam2in.example.user.repository;

import com.jam2in.example.user.entity.User;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
  Mono<User> findByUsername(@Param("username") String username);

  Mono<Boolean> existsByUsername(@Param("username") String username);
}
