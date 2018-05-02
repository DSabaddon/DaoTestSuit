package ru.mds.dbtest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mds.dbtest.dao.TestDao;
import ru.mds.dbtest.model.TestData;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author MDS
 * @since 02.05.2018 (1.0)
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RealDbTest {
  @Autowired
  private DataSource dataSource;

  private TestDao sut;

  @Before
  public void setUp() {
    sut = new TestDao(new NamedParameterJdbcTemplate(dataSource));
  }

  @Test
  public void getFromDualTest() {
    Optional<String> x = sut.getFromDual();

    assertTrue(x.isPresent());
    assertEquals("1", x.get());
  }

  @Test
  public void getTestDataTest() {
    TestData testData = new TestData(1, "2");

    List<TestData> testDatas = sut.getTestDataBy(1);

    assertEquals(testDatas.size(), 1);
    assertEquals(testDatas.get(0).toString(), testData.toString());
  }
}
