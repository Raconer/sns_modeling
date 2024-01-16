package com.sns.modeling.domain.post.repository;

import com.sns.modeling.domain.post.dto.DailyPostCount;
import com.sns.modeling.domain.post.dto.DailyPostCountRequest;
import com.sns.modeling.domain.post.entity.Post;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepository {

  final static String TABLE = "POST";
  final static private RowMapper<DailyPostCount> DAILY_POST_COUNT_MEPPER = (ResultSet resultSet, int rowNum) -> new DailyPostCount(
      resultSet.getLong("memberId"),
      resultSet.getObject("createdDate", LocalDate.class),
      resultSet.getLong("count")
  );

  final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public Post save(Post post) {
    if (post.getId() == null) {
      return this.insert(post);
    }

    throw new UnsupportedOperationException("Post는 갱신을 지원하지 않습니다.");
  }

  private Post insert(Post post) {
    SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(
        namedParameterJdbcTemplate.getJdbcTemplate()).withTableName(this.TABLE)
        .usingGeneratedKeyColumns("id");

    SqlParameterSource params = new BeanPropertySqlParameterSource(post);
    var id = jdbcInsert.executeAndReturnKey(params).longValue();

    return Post.builder()
        .id(id)
        .memberId(post.getMemberId())
        .contents(post.getContents())
        .createdDate(post.getCreatedDate())
        .createdAt(post.getCreatedAt())
        .build();
  }

  public List<DailyPostCount> groupByCreatedDate(DailyPostCountRequest request) {
    var sql = String.format("SELECT createdDate, memberId, count(id) AS count "
        + "FROM %s "
        + "WHERE memberId = :memberId AND createdDate between :firstDate AND :lastDate "
        + "GROUP BY memberId, createdDate", TABLE);

    var params = new BeanPropertySqlParameterSource(request);
    return namedParameterJdbcTemplate.query(sql, params, DAILY_POST_COUNT_MEPPER);

  }
}