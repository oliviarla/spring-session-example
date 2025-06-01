package com.jam2in.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisIndexedWebSession;

@Configuration
@EnableRedisIndexedWebSession
public class ReactiveCacheConfig {
  /*
   For Spring Session Redis
   */
  @Bean
  public LettuceConnectionFactory lettuceconnectionFactory() {
    return new LettuceConnectionFactory("localhost", 6379);
  }

  @Bean
  public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
    return new GenericJackson2JsonRedisSerializer(objectMapper());
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.registerSubtypes(SecurityContextImpl.class);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return objectMapper;
  }
}
