package com.jam2in.example.security;

import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomReactiveAuthenticationManager extends UserDetailsRepositoryReactiveAuthenticationManager {

  public CustomReactiveAuthenticationManager(PasswordEncoder passwordEncoder,
                                             CustomReactiveUserDetailService userDetailsService) {
    super(userDetailsService);
    this.setPasswordEncoder(passwordEncoder);
  }

}
