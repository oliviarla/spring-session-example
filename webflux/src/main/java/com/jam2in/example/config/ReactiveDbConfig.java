package com.jam2in.example.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories
public class ReactiveDbConfig extends AbstractR2dbcConfiguration {

  @Override
  public ConnectionFactory connectionFactory() {
    return null;
  }

}
