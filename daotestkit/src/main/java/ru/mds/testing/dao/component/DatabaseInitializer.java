package ru.mds.testing.dao.component;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import ru.mds.testing.dao.model.Row;
import ru.mds.testing.dao.model.TableDescription;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author MDS
 * @since 03.05.2018 (v1.0)
 */
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DatabaseInitializer {
  private final OracleScriptParser oracleScriptParser;
  private final H2ScriptComposer h2ScriptComposer;
  private final NamedParameterJdbcTemplate jdbcTemplate;

  public void initDb(String scriptFileName) throws IOException {
    String oracleScript = resourceToString(scriptFileName);
    List<TableDescription> tableDescriptions = oracleScriptParser.parseCreateTable(oracleScript);
    String h2Script = h2ScriptComposer.composeDropWithCreateTableScripts(tableDescriptions);
    execute(h2Script);
  }

  public void insertRows(List<Row> rows) {
    String h2script = h2ScriptComposer.composeInsertScripts(rows);
    execute(h2script);
  }

  public void insertRows(Row... rows) {
    insertRows(Arrays.asList(rows));
  }

  void execute(String query) {
    jdbcTemplate.update(query, EmptySqlParameterSource.INSTANCE);
  }

  private String resourceToString(String name) throws IOException {
    InputStream inputStream = this.getClass().getResourceAsStream(name);
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int length;
    while ((length = inputStream.read(buffer)) != -1) {
      result.write(buffer, 0, length);
    }
    return result.toString("UTF-8");
  }
}