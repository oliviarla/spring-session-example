package com.jam2in.example.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Table(name = "users")
@ToString
public class User implements Serializable {

  @Serial
  private static final long serialVersionUID = -1;

  @Id
  private Long id;
  private String password;
  private String username;
  private Role role;

  @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
  private LocalDateTime lastAccessedAt;

  @Builder
  public User(Long id, String password, String username, Role role,
              LocalDateTime createdAt, LocalDateTime lastAccessedAt) {
    this.id = id;
    this.password = password;
    this.username = username;
    this.role = role;
    this.createdAt = createdAt;
    this.lastAccessedAt = lastAccessedAt;
  }

  public static User of(String username, String encodedPassword, Role role) {
    return User.builder()
        .username(username)
        .password(encodedPassword)
        .role(role)
        .createdAt(LocalDateTime.now())
        .lastAccessedAt(LocalDateTime.now())
        .build();
  }
}
