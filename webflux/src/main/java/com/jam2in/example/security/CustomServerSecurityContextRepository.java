package com.jam2in.example.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public class CustomServerSecurityContextRepository implements ServerSecurityContextRepository {

  @Override
  public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
    return Mono.empty();
  }

  @Override
  public Mono<SecurityContext> load(ServerWebExchange exchange) {
    return exchange.getSession()
        .flatMap(session -> {
          Object principal = session.getAttribute("principal");
          List<String> authorities = session.getAttribute("authorities");
          if (principal == null || CollectionUtils.isEmpty(authorities)) {
            return Mono.empty();
          }
          List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
              .map(SimpleGrantedAuthority::new)
              .collect(Collectors.toList());

          Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, grantedAuthorities);
          return Mono.just(new SecurityContextImpl(authentication));
        });
  }
}
