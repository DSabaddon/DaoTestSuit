package ru.ftc.pc.testing.dao;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ftc.pc.testing.dao.component.DatabaseInitializer;

import java.io.IOException;

/**
 * @author MDS
 * @since 03.05.2018 (v1.0)
 */
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TestController {
  private final DatabaseInitializer databaseInitializer;

  @GetMapping("/init-db")
  void initDb() throws IOException {
    databaseInitializer.initDb("/create_tables.sql");
  }
}