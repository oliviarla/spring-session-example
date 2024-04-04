package com.jam2in.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class SessionAuthenticateFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    Authentication authentication = attemptAuthentication(request);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    filterChain.doFilter(request, response);
  }

  @SuppressWarnings("unchecked")
  public Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
    HttpSession session = request.getSession(false);
    if (session == null) {
      return null;
    }

    String principal = (String) session.getAttribute("principal");
    Collection<String> authorities = (Collection<String>) session.getAttribute("authorities");
    if (principal == null) {
      return null;
    }
    return new UsernamePasswordAuthenticationToken(principal, null,
        authorities.stream().map(SimpleGrantedAuthority::new).toList());
  }
}
