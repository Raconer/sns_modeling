package com.sns.modeling.domain.member.repository;

import com.sns.modeling.domain.member.entity.MemberNicknameHistory;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberNicknameHistoryRepository {

  final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  static final private String TABLE = "MemberNicknameHistory";
  static final RowMapper<MemberNicknameHistory> rowMapper = (ResultSet resultSet, int rownum) -> MemberNicknameHistory.builder()
      .id(resultSet.getLong("id"))
      .memberId(resultSet.getLong("memberId"))
      .nickname(resultSet.getString("nickname"))
      .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
      .build();


  public MemberNicknameHistory save(MemberNicknameHistory history) {
    if (history.getId() == null) {
      return this.insert(history);
    }
    throw new UnsupportedOperationException("MemberNicknameHistory는 갱신을 지원하지 않습니다.");
  }

  private MemberNicknameHistory insert(MemberNicknameHistory history) {
    SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(
        namedParameterJdbcTemplate.getJdbcTemplate()).withTableName(TABLE)
        .usingGeneratedKeyColumns("id");

    SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(history);
    long id = simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue();

    return MemberNicknameHistory.builder()
        .id(id)
        .memberId(history.getMemberId())
        .nickname(history.getNickname())
        .createdAt(history.getCreatedAt())
        .build();
  }

  public List<MemberNicknameHistory> finaAllbyMemberId(Long memberId){
    var sql = String.format("SELECT * FROM %s WHERE memberId = :memberId", TABLE);
    var params = new MapSqlParameterSource().addValue("memberId", memberId);
    return this.namedParameterJdbcTemplate.query(sql, params, rowMapper);
  }

}
