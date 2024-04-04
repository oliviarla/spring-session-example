package com.jam2in.example.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Role implements GrantedAuthority, Serializable {
  ROLE_ADMIN, ROLE_CREW;

  private static final long serialVersionUID = 1;

  @JsonCreator
  public static Role from(String value) {
    for (Role role : Role.values()) {
      if (role.getValue().equals(value.toUpperCase())) {
        return role;
      }
    }
    return null;
  }

  @Override
  public String getAuthority() {
    return this.name();
  }

  @JsonValue
  public String getValue() {
    return name();
  }
}
