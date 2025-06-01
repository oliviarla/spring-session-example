package com.jam2in.example.config;

import com.jam2in.example.security.CustomServerSecurityContextRepository;
import com.jam2in.example.security.LoginAuthenticationWebFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.util.CollectionUtils;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
public class ReactiveSecurityConfig {

  @Bean
  public SecurityWebFilterChain filterChain(ServerHttpSecurity http, ReactiveAuthenticationManager manager,
                                            LoginAuthenticationWebFilter loginFilter) {
    http.csrf(ServerHttpSecurity.CsrfSpec::disable);
    http.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
        .pathMatchers("/join", "/login").permitAll()
        .anyExchange().authenticated());
    http.headers(configurer ->
        configurer.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable));
    http.addFilterAt(loginFilter, SecurityWebFiltersOrder.AUTHENTICATION);
    http.logout(logoutSpec -> logoutSpec
        .logoutUrl("/logout")
        .logoutHandler((exchange, authentication) -> exchange.getExchange().getSession()
            .flatMap(session -> {
              if (!CollectionUtils.isEmpty(session.getAttributes())) {
                return session.invalidate();
              } else {
                // Session which is internally made when user didn't log in
                // doesn't have any attributes and will not be saved into redis.
                // So just return 401 Response.
                ServerHttpResponse response = exchange.getExchange().getResponse();
                response.setStatusCode(HttpStatusCode.valueOf(401));
                return response.setComplete();
              }
            }))
        .logoutSuccessHandler((exchange, authentication) -> {
          ServerHttpResponse response = exchange.getExchange().getResponse();
          response.setStatusCode(HttpStatus.OK);
          return response.setComplete();
        }));
    http.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);
    http.formLogin(ServerHttpSecurity.FormLoginSpec::disable);
    http.securityContextRepository(serverSecurityContextRepository());
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ServerSecurityContextRepository serverSecurityContextRepository() {
    return new CustomServerSecurityContextRepository();
  }
}
