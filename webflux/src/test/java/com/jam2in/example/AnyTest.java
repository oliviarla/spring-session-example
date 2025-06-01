package com.jam2in.example;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class AnyTest {
  @Test
  void test() {
    Duration duration = Duration.ofMillis(-1);
    long l = duration.get(ChronoUnit.MILLIS);
    System.out.println();
  }
}
