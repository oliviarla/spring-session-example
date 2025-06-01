package com.jam2in.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jam2in.example.security.CustomUserDetailService;
import com.jam2in.example.security.LoginFilter;
import com.jam2in.example.security.SessionAuthenticateFilter;
import com.jam2in.example.security.SessionAuthenticationProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
	private final ObjectMapper objectMapper;
	private final CustomUserDetailService userDetailsService;
	private final SessionAuthenticateFilter sessionAuthenticateFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable);
    http.authorizeHttpRequests(
        authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
            .anyRequest()
            .permitAll());
    http.headers(configurer ->
        configurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
    loginFilter(http).setRequiresAuthenticationRequestMatcher(
        new AntPathRequestMatcher("/login", "POST")
    );
    http.addFilterBefore(loginFilter(http), UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(sessionAuthenticateFilter, UsernamePasswordAuthenticationFilter.class);
    http.sessionManagement(configurer -> configurer.sessionFixation().changeSessionId());
    http.logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
        .logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK)));
    return http.build();
  }

  @Bean
  public AuthenticationManager authManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder authenticationManagerBuilder =
        http.getSharedObject(AuthenticationManagerBuilder.class);
    authenticationManagerBuilder.authenticationProvider(authenticationProvider());
    return authenticationManagerBuilder.build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    return new SessionAuthenticationProvider(userDetailsService, passwordEncoder());
  }

  @Bean
  public LoginFilter loginFilter(HttpSecurity http) throws Exception {
    return new LoginFilter(authManager(http), objectMapper);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
