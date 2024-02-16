package com.sns.modeling.domain.post.repository;

import com.sns.modeling.domain.post.entity.PostLike;
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

@Repository
@AllArgsConstructor
public class PostLikeRepository {
    final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    final private String TABLE = "PostLike";

    final static private RowMapper<PostLike> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> PostLike.builder()
            .id(resultSet.getLong("id"))
            .memberId(resultSet.getLong("memberId"))
            .postId(resultSet.getLong("postId"))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
            .build();

    public PostLike save(PostLike postLike) {
        if (postLike.getId() == null) {
            return this.insert(postLike);
        }

        throw new UnsupportedOperationException("PostLike은 갱신을 지원하지 않습니다.");
    }
    public Long count(Long postId) {
        var params = new MapSqlParameterSource().addValue("postId", postId);
        var sql = String.format("""
                                        SELECT COUNT(id)
                                        FROM %s
                                        WHERE postId = :postId""", this.TABLE);
        return this.namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
    }

    // CREATE
    private PostLike insert(PostLike postLike) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(this.TABLE)
                .usingGeneratedKeyColumns("id");

        SqlParameterSource params = new BeanPropertySqlParameterSource(postLike);

        var id = jdbcInsert
                .executeAndReturnKey(params)
                .longValue();

        return PostLike.builder()
                .id(id)
                .memberId(postLike.getMemberId())
                .postId(postLike.getPostId())
                .createdAt(postLike.getCreatedAt())
                .build();
    }
}
