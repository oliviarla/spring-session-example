package com.jam2in.example.user.controller;

import com.jam2in.example.user.dto.JoinDto;
import com.jam2in.example.user.dto.UserSessionDto;
import com.jam2in.example.user.entity.User;
import com.jam2in.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/join")
  public Mono<String> join(@RequestBody JoinDto joinDto) {
    return userService.join(joinDto.getUsername(), joinDto.getPassword(), joinDto.getRole())
        .map(User::toString);
  }

  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public Mono<Object> getMe() {
    return ReactiveSecurityContextHolder.getContext()
        .map(SecurityContext::getAuthentication)
        .map(Authentication::getPrincipal);
  }

  @GetMapping("/users")
  @PreAuthorize("isAuthenticated() && hasRole('ADMIN')")
  public Flux<UserSessionDto> getUsers(Mono<String> principal) {
    return userService.getAllSessionsByUser(principal);
  }
}
