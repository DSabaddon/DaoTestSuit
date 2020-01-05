package ru.mds.testing.dao.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author MDS
 * @since 22.05.2018 (v1.0)
 */
@Slf4j
@Primary
@Component
class NamedParameterJdbcTemplateWrapper extends NamedParameterJdbcTemplate {
  private final PreliminaryParameterResolver preliminaryParameterResolver;
  private final MergeRequestResolver mergeRequestResolver;

  @Autowired
  public NamedParameterJdbcTemplateWrapper(DataSource dataSource, PreliminaryParameterResolver preliminaryParameterResolver, MergeRequestResolver mergeRequestResolver) {
    super(dataSource);
    this.preliminaryParameterResolver = preliminaryParameterResolver;
    this.mergeRequestResolver = mergeRequestResolver;
  }

  @Override
  public int update(String sql, SqlParameterSource paramSource) throws DataAccessException {
    sql = mergeRequestResolver.tryResolveMergeRequest(sql, paramSource);
    sql = preliminaryParameterResolver.tryResolveParameter(sql, paramSource);
    return super.update(sql, paramSource);
  }

  @Override
  public <T> List<T> query(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) throws DataAccessException {
    sql = preliminaryParameterResolver.tryResolveParameter(sql, paramSource);
    return super.query(sql, paramSource, rowMapper);
  }


  @Override
  public int[] batchUpdate(String sql, SqlParameterSource[] batchArgs) {
    for (SqlParameterSource source : batchArgs) {
      sql = mergeRequestResolver.tryResolveMergeRequest(sql, source);
      sql = preliminaryParameterResolver.tryResolveParameter(sql, source);
    }

    return super.batchUpdate(sql, batchArgs);
  }
}