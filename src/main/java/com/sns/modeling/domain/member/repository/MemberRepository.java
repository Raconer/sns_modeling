package com.sns.modeling.domain.member.repository;

import com.sns.modeling.domain.member.entity.Member;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
public class MemberRepository {

  static final private String TABLE = "Member";
  final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  final private RowMapper<Member> rowMapper = (ResultSet resultSet, int rownum) -> Member.builder()
      .id(resultSet.getLong("id"))
      .email(resultSet.getString("email"))
      .nickname(resultSet.getString("nickname"))
      .birthDay(resultSet.getObject("birthDay", LocalDate.class))
      .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
      .build();

  public Member save(Member member) {
    if (member.getId() == null) {
      return this.insert(member);
    }
    return this.update(member);
  }

  private Member insert(Member member) {
    SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(
        namedParameterJdbcTemplate.getJdbcTemplate()).withTableName(TABLE)
        .usingGeneratedKeyColumns("id");

    SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(member);
    long id = simpleJdbcInsert.executeAndReturnKey(parameterSource).longValue();

    return Member.builder()
        .id(id)
        .email(member.getEmail())
        .nickname(member.getNickname())
        .birthDay(member.getBirthDay())
        .createdAt(member.getCreatedAt())
        .build();
  }

  public Optional<Member> findById(Long id) {

    var sql = String.format("SELECT * FROM %s WHERE id = :id", TABLE);
    var param = new MapSqlParameterSource()
        .addValue("id", id);


    var member = namedParameterJdbcTemplate.queryForObject(sql, param, rowMapper);
    return Optional.ofNullable(member);
  }

  public List<Member> findAllByIdIn(List<Long> ids){
    if(ids.isEmpty()) return List.of();
    var sql = String.format("SELECT * FROM %s WHERE id in (:ids)", TABLE);
    var params = new MapSqlParameterSource().addValue("ids", ids);
    return this.namedParameterJdbcTemplate.query(sql, params, rowMapper);
  }

  // TODO : 추가 예정
  private Member update(Member member) {
    var sql = String.format("UPDATE %s set email = :email, nickname = :nickname, birthDay = :birthDay WHERE id = :id", TABLE);
    SqlParameterSource params = new BeanPropertySqlParameterSource(member);
    this.namedParameterJdbcTemplate.update(sql, params);
    return member;
  }

}
