package ru.mds.dbtest.dao;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.mds.dbtest.model.TestData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author MDS
 * @since 30.04.2018 (1.0)
 */
@AllArgsConstructor
public class TestDao {
  private final NamedParameterJdbcTemplate jdbcTemplate;

  public Optional<String> getFromDual() {
    String query = "SELECT X FROM DUAL";

    return jdbcTemplate.query(query, (rs, rowNum) -> rs.getString(1))
        .stream()
        .findFirst();
  }

  public List<TestData> getTestDataBy(Integer colNum) {
    String query = "SELECT COL_NUM, COL_CHAR " +
        " FROM TEST_DATA " +
        " WHERE COL_NUM = :colNum";

    Map<String, Object> params = new HashMap<>();
    params.put("colNum", colNum);

    return jdbcTemplate.query(query, params,
        (rs, rowNum) -> new TestData(rs.getInt(1), rs.getString(2)));
  }
}
