package com.jam2in.example.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(force = true)
public class LoginDto {
  @NonNull
  private String username;
  @NonNull
  private String password;
}
