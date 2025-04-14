package com.jam2in.example.user.service;

import com.jam2in.example.user.dto.UserSessionDto;
import com.jam2in.example.user.entity.Role;
import com.jam2in.example.user.entity.User;
import com.jam2in.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final FindByIndexNameSessionRepository<? extends Session> findByIndexNameSessionRepository;
  private final PasswordEncoder passwordEncoder;

  public User join(String username, String password, Role role) {
    if (userRepository.existsByUsername(username)) {
      throw new RuntimeException("Username already exists");
    }
    User user = User.of(username, passwordEncoder.encode(password), role);
    return userRepository.save(user);
  }

  /**
   * Support only when activating ArcusIndexedSessionRepository
   */
  public List<UserSessionDto> getAllSessionsByUser(String username) {
    return findByIndexNameSessionRepository.findByPrincipalName(username).values()
        .stream().map(UserSessionDto::new).toList();
  }
}
