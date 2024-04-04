package com.jam2in.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.spy.memcached.ArcusClient;
import net.spy.memcached.ArcusClientPool;
import net.spy.memcached.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;

@Configuration
@EnableRedisIndexedHttpSession
public class CacheConfig {

  /*
  For Spring Session Arcus
   */
  @Bean
  public ArcusClientPool arcusClientPool() {
    return ArcusClient.createArcusClientPool("test", new ConnectionFactoryBuilder(), 8);
  }

  /*
   For Spring Session Redis
   */
  @Bean
  public LettuceConnectionFactory connectionFactory() {
    return new LettuceConnectionFactory("localhost", 6379);
  }

  @Bean
  public RedisSerializer<Object> redisSerializer(ObjectMapper objectMapper) {
    return new GenericJackson2JsonRedisSerializer(objectMapper);
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return objectMapper;
  }
}
