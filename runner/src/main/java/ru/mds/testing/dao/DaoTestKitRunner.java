package ru.mds.testing.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author MDS
 * @since 30.04.2018 (v1.0)
 */
@SpringBootApplication
@Slf4j
public class DaoTestKitRunner {
  public static void main(String[] args) {
    SpringApplication.run(DaoTestKitRunner.class, args);
  }
}
