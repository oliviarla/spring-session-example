package com.jam2in.example.user.controller;

import com.jam2in.example.user.dto.JoinDto;
import com.jam2in.example.user.dto.UserSessionDto;
import com.jam2in.example.user.entity.User;
import com.jam2in.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/join")
  public String join(@RequestBody JoinDto joinDto) {
    return userService.join(joinDto.getUsername(), joinDto.getPassword(), joinDto.getRole())
        .toString();
  }

  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public String getMe(@AuthenticationPrincipal User user) {
    return user.toString();
  }

  @GetMapping("/users")
  @PreAuthorize("isAuthenticated() && hasRole('ADMIN')")
  public List<UserSessionDto> getUsers(@AuthenticationPrincipal String principal) {
    return userService.getAllSessionsByUser(principal);
  }
}
