package com.jam2in.example.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jam2in.example.user.dto.LoginDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.session.FindByIndexNameSessionRepository;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final ObjectMapper objectMapper;

  public LoginFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper) {
    super(authenticationManager);
    this.objectMapper = objectMapper;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
                                              HttpServletResponse response)
      throws AuthenticationException {
    Authentication authentication;
    try {
      LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);
      authentication = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
    } catch (IOException e) {
      throw new InternalAuthenticationServiceException(e.getMessage(), e);
    }
    return this.getAuthenticationManager().authenticate(authentication);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain chain,
                                          Authentication authResult) {
    request.getSession().setAttribute("principal", authResult.getPrincipal());
    request.getSession().setAttribute("authorities", authResult.getAuthorities());
    request.getSession().setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, authResult.getPrincipal());
    try {
      response.getWriter().write(
          "login success");
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            AuthenticationException failed) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write("login failed");
    response.getWriter().flush();
    response.getWriter().close();
  }
}
