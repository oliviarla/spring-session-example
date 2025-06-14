package com.jam2in.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SessionTestApplication {
  public static void main(String[] args) {
    SpringApplication.run(SessionTestApplication.class, args);
  }
}
