package com.jam2in.example.user.service;

import com.jam2in.example.user.dto.UserSessionDto;
import com.jam2in.example.user.entity.Role;
import com.jam2in.example.user.entity.User;
import com.jam2in.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.ReactiveFindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final ReactiveFindByIndexNameSessionRepository<? extends Session> findByIndexNameSessionRepository;
  private final PasswordEncoder passwordEncoder;

  public Mono<User> join(String username, String password, Role role) {
    return userRepository.existsByUsername(username)
        .flatMap(exists -> {
          if (exists) {
            return Mono.error(new RuntimeException("Username already exists"));
          } else {
            User user = User.of(username, passwordEncoder.encode(password), role);
            return userRepository.save(user);
          }
        });
  }

  /**
   * Support only when IndexedSessionRepository is enabled
   */
  public Flux<UserSessionDto> getAllSessionsByUser(Mono<String> username) {
    return username
        .flatMap(findByIndexNameSessionRepository::findByPrincipalName)
        .flatMapMany(map -> Flux.fromIterable(map.values()))
        .map(UserSessionDto::new);
  }
}
