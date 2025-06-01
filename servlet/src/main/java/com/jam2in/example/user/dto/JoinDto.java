package com.jam2in.example.user.dto;

import com.jam2in.example.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@AllArgsConstructor
@ToString
@NoArgsConstructor(force = true)
@Getter
public class JoinDto {
  private String username;
  private String password;
  @NonNull
  private Role role;
}
