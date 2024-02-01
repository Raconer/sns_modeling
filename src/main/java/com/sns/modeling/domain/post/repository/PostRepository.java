package com.sns.modeling.domain.post.repository;

import com.sns.modeling.util.PageHelper;
import com.sns.modeling.domain.post.dto.DailyPostCount;
import com.sns.modeling.domain.post.dto.DailyPostCountRequest;
import com.sns.modeling.domain.post.entity.Post;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    final private String TABLE = "POST";
    final static private RowMapper<Post> ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Post.builder()
            .id(resultSet.getLong("id"))
            .memberId(resultSet.getLong("memberId"))
            .contents(resultSet.getString("contents"))
            .createdDate(resultSet.getObject("createdDate", LocalDate.class))
            .createdAt(resultSet.getObject("createdAt", LocalDateTime.class))
            .build();

    final static private RowMapper<DailyPostCount> DAILY_POST_COUNT_MAPPER = (ResultSet resultSet, int rowNum) -> new DailyPostCount(
            resultSet.getLong("memberId"),
            resultSet.getObject("createdDate", LocalDate.class),
            resultSet.getLong("count")
    );

    final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Post save(Post post) {
        if (post.getId() == null) {
            return this.insert(post);
        }

        throw new UnsupportedOperationException("POST는 갱신을 지원하지 않습니다.");
    }
    // CREATE
    private Post insert(Post post) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(namedParameterJdbcTemplate.getJdbcTemplate())
                .withTableName(this.TABLE)
                .usingGeneratedKeyColumns("id");

        SqlParameterSource params = new BeanPropertySqlParameterSource(post);

        var id = jdbcInsert
                .executeAndReturnKey(params)
                .longValue();

        return Post.builder()
                .id(id)
                .memberId(post.getMemberId())
                .contents(post.getContents())
                .createdDate(post.getCreatedDate())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public void bulkInsert(List<Post> posts) {
        var sql = String.format("""
                                        INSERT INTO `%s` (memberId, contents, createdDate, createdAt)
                                        VALUES (:memberId, :contents, :createdDate, :createdAt)""",
                                this.TABLE);

        SqlParameterSource[] params = posts.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);

        this.namedParameterJdbcTemplate.batchUpdate(sql, params);

    }

    // READ
    public List<DailyPostCount> groupByCreatedDate(DailyPostCountRequest request) {
        var sql = String.format("""
                                        SELECT createdDate,
                                                memberId,
                                                count(id) AS count
                                        FROM %s
                                        WHERE memberId = :memberId
                                        AND createdDate between :firstDate AND :lastDate
                                        GROUP BY memberId, createdDate""", this.TABLE);

        var params = new BeanPropertySqlParameterSource(request);
        return namedParameterJdbcTemplate.query(sql, params, DAILY_POST_COUNT_MAPPER);
    }

    public Page<Post> findAllByMemberId(Long memberId, Pageable pageable) {
        var params = new MapSqlParameterSource().addValue("memberId", memberId)
                .addValue("size", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());
        var sql = String.format(""" 
                                        SELECT  *
                                        FROM    %s
                                        WHERE   memberId = :memberId
                                        ORDER BY %s
                                        LIMIT   :size
                                        OFFSET  :offset
                                        """, this.TABLE, PageHelper.orderBy(pageable.getSort()));

        var posts = this.namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);

        return new PageImpl<>(posts, pageable, this.getCount(memberId));
    }

    private Long getCount(Long memberId) {
        var params = new MapSqlParameterSource().addValue("memberId", memberId);
        var sql = String.format("""
                                        SELECT COUNT(id)
                                        FROM %s
                                        WHERE memberId = :memberId""", this.TABLE);
        return this.namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
    }

    public List<Post> findAllByMemberIdAndOrderByIdDesc(Long memberId, int size) {
        var params = new MapSqlParameterSource().addValue("memberId", memberId)
                .addValue("size", size);
        var sql = String.format("""
                                        SELECT *
                                        FROM %s
                                        WHERE memberId = :memberId
                                        ORDER BY id desc
                                        LIMIT :size""", this.TABLE);
        return this.namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    public List<Post> findAllByInMemberIdAndOrderByIdDesc(List<Long> memberIds, int size) {

        if(memberIds.isEmpty()){
            return List.of();
        }

        var params = new MapSqlParameterSource().addValue("memberIds", memberIds)
                .addValue("size", size);
        var sql = String.format("""
                                        SELECT *
                                        FROM %s
                                        WHERE memberId in (:memberIds)
                                        ORDER BY id desc
                                        LIMIT :size""", this.TABLE);
        return this.namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    public List<Post> findAllByInId(List<Long> ids){
        if(ids.isEmpty()) return List.of();

        var params = new MapSqlParameterSource()
                .addValue("ids", ids);

        var sql = String.format("""
                                        SELECT *
                                        FROM %s
                                        WHERE id in (:ids)
                                        ORDER BY id desc
                                        """, this.TABLE);
        return this.namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }

    public List<Post> findAllByLessThanIdAndMemberIdAndOrderByIdDesc(Long id, Long memberId, int size) {
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

    public List<Post> findAllByLessThanIdAndInMemberIdAndOrderByIdDesc(Long id, List<Long> memberIds, int size) {
        if(memberIds.isEmpty()){
            return List.of();
        }

        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("memberIds", memberIds)
                .addValue("size", size);
        var sql = String.format("""
                                        SELECT *
                                        FROM %s
                                        WHERE memberId in (:memberIds) and id < :id
                                        ORDER BY id desc
                                        LIMIT :size""", this.TABLE);
        return this.namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER);
    }
}