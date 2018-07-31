package ru.ftc.pc.testing.dao.component;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.ftc.pc.testing.dao.DaoTestRootScanMarker;

/**
 * Класс, конфигурирующий все необходимые Bean'ы для запуска DAO тестов
 *
 * @author maksimenko
 * @implSpec Наследники должны:
 * <ul><li>создать таблицы в тестовой БД: <nobr>{@link DatabaseInitializer#initDb(String) databaseInitializer.initDb("/create_tables_script.sql")}</nobr>;</li>
 * <li>создать SUT с использованием <b>namedParameterJdbcTemplate</b>.</li></ul>
 * @since 26.07.2018
 */
@SpringJUnitConfig
@SpringBootTest
abstract public class DaoTest {
  /**
   * Класс-конфигуратор. Выполняет три задачи:
   * <ul><li>указывает Spring'у, где находится описание необходимых Bean'ов ({@link ComponentScan});</li>
   * <li>включает автоконфигурацию необходимых (найденных сканированием и всех, от которых они зависят) Bean'ов ({@link EnableAutoConfiguration});</li>
   * <li>предотвращает подъем всего контекста микросервиса в тесте, т.к. является внутренним классом аннотированным {@link Configuration}
   * (аннотация {@link SpringBootTest} не упирается в корневой класс микросервиса, в попытке найти <code>@SpringBootConfiguration</code>).</li></ul>
   */
  @Configuration
  @EnableAutoConfiguration
  @ComponentScan(basePackageClasses = {DaoTestRootScanMarker.class})
  public static class DaoTestConfiguration {
  }
}