package com.sns.modeling.domain.post.repository;

import com.sns.modeling.domain.post.entity.Timeline;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@AllArgsConstructor
public class TimelineRepository {
    final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    final private String TABLE = "TIMELINE";

    final static private RowMapper<Timeline> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Timeline.builder()
            .id(resultSet.getLong("id"))
            .memberId(resultSet.getLong("memberId"))
            .postId(resultSet.getLong("postId"))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
            .build();

    public Timeline save(Timeline timeline) {
        if (timeline.getId() == null) {
            return this.insert(timeline);
        }

        throw new UnsupportedOperationException("Timeline은 갱신을 지원하지 않습니다.");
    }
    // CREATE
    private Timeline insert(Timeline timeline) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(this.TABLE)
                .usingGeneratedKeyColumns("id");

        SqlParameterSource params = new BeanPropertySqlParameterSource(timeline);

        var id = jdbcInsert
                .executeAndReturnKey(params)
                .longValue();

        return Timeline.builder()
                .id(id)
                .memberId(timeline.getMemberId())
                .postId(timeline.getPostId())
                .createdAt(timeline.getCreatedAt())
                .build();
    }

    public void bulkInsert(List<Timeline> timelines) {
        var sql = String.format("""
                                        INSERT INTO `%s` (memberId, postId, createdAt)
                                        VALUES (:memberId, :postId, :createdAt)""",
                                this.TABLE);

        SqlParameterSource[] params = timelines.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);

        this.namedParameterJdbcTemplate.batchUpdate(sql, params);

    }

    // READ
    public List<Timeline> findAllByMemberIdAndOrderByIdDesc(Long memberId, int size) {
        var params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("size", size);
        var sql = String.format("""
                                        SELECT *
                                        FROM %s
                                        WHERE memberId = :memberId
                                        ORDER BY id desc
                                        LIMIT :size""", this.TABLE);
        return this.namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    public List<Timeline> findAllByLessThanIdAndMemberIdAndOrderByIdDesc(Long id, Long memberId, int size) {
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("memberId", memberId)
                .addValue("size", size);
        var sql = String.format("""
                                        SELECT *
                                        FROM %s
                                        WHERE memberId = :memberId and id < :id
                                        ORDER BY id desc
                                        LIMIT :size""", this.TABLE);
        return this.namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }
}
