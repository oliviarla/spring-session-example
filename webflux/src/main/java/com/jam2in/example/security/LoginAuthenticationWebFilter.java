package com.jam2in.example.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jam2in.example.user.dto.LoginDto;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.session.ReactiveFindByIndexNameSessionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 이 필터에서는 로그인 시 사용자의 정보를 UsernamePasswordAuthenticationToken 으로 변환해준다.
 */
@Component
public class LoginAuthenticationWebFilter extends AuthenticationWebFilter {

  public LoginAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager) {
    super(authenticationManager);

    this.setRequiresAuthenticationMatcher(
        ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/login"));

    this.setServerAuthenticationConverter(
        exchange -> exchange.getRequest().getBody()
            .next()
            .flatMap(dataBuffer -> {
              LoginDto loginDto;
              try {
                loginDto = new ObjectMapper().readValue(dataBuffer.asInputStream(), LoginDto.class);
              } catch (IOException e) {
                return Mono.error(e);
              }
              return Mono.just(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
            }));
    this.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
    this.setAuthenticationSuccessHandler(new LoginAuthenticationWebFilterSuccessHandler());
    this.setAuthenticationFailureHandler(new LoginAuthenticationWebFilterFailureHandler());
  }

  /**
   * 로그인 성공 시 세션에 사용자 정보를 저장하고, 응답을 작성한다.
   */
  private static class LoginAuthenticationWebFilterSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return exchange.getSession()
            .doOnNext(session -> {
              Map<String, Object> attributes = session.getAttributes();
              UserDetails userDetails = (UserDetails) authentication.getPrincipal();
              attributes.put("principal", userDetails.getUsername());
              attributes.put("authorities", userDetails.getAuthorities());
              attributes.put(ReactiveFindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, userDetails.getUsername());
//              session.setMaxIdleTime(Duration.ZERO);
            })
            .then(Mono.defer(() -> {
              byte[] body = "login success".getBytes(StandardCharsets.UTF_8);
              DataBuffer buffer = response.bufferFactory().wrap(body);
              return response.writeWith(Mono.just(buffer));
            }));
    }
  }

  private static class LoginAuthenticationWebFilterFailureHandler implements ServerAuthenticationFailureHandler {

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
      ServerWebExchange exchange = webFilterExchange.getExchange();
      ServerHttpResponse response = exchange.getResponse();
      response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
      response.setStatusCode(HttpStatus.UNAUTHORIZED);
      return Mono.defer(() -> {
        DataBuffer buffer = response.bufferFactory().wrap("login failed".getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
      });
    }
  }
}
