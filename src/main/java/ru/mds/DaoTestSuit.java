package ru.mds;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author MDS
 * @since 30.04.2018 (1.0)
 */
@SpringBootApplication
@Slf4j
public class DaoTestSuit implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(DaoTestSuit.class, args);
  }

  @Override
  public void run(String... args) {
    log.info("!!! HELLO !!!");
  }
}
