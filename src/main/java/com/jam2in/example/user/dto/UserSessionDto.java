package com.jam2in.example.user.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.session.Session;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;

@Getter
public class UserSessionDto {
  private static final ZoneId ZONE_KR = ZoneId.of("Asia/Tokyo");

  private final String sessionId;
  private final String username;
  private final Collection<? extends GrantedAuthority> role;
  private final Long maxInactiveInterval;
  private final LocalDateTime createdAt;
  private final LocalDateTime lastAccessedAt;

  public UserSessionDto(Session session) {
    this.sessionId = session.getId();
    this.username = session.getAttribute("principal");
    this.role = session.getAttribute("authorities");
    this.maxInactiveInterval = session.getMaxInactiveInterval().toSeconds();
    this.createdAt = LocalDateTime.ofInstant(session.getCreationTime(), ZONE_KR);
    this.lastAccessedAt = LocalDateTime.ofInstant(session.getLastAccessedTime(), ZONE_KR);
  }
}
