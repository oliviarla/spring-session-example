package com.jam2in.example.security;

import com.jam2in.example.user.repository.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class CustomReactiveUserDetailService implements ReactiveUserDetailsService {
  private final UserRepository userRepository;

  public CustomReactiveUserDetailService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return userRepository.findByUsername(username)
        .map(user -> (UserDetails) new User(user.getUsername(), user.getPassword(), List.of(user.getRole())))
        .or(Mono.error(() -> new UsernameNotFoundException("User not found")));
  }
}
