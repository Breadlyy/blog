package com.example.blog.repository;

import com.example.blog.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TagRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Tag> tagRowMapper = (rs, rowNum) -> {
        Tag tag = new Tag();
        tag.setId(rs.getLong("id"));
        tag.setName(rs.getString("name"));
        return tag;
    };

    public Optional<Tag> findByName(String name) {
        String sql = "SELECT * FROM tags WHERE name = ?";
        List<Tag> tags = jdbcTemplate.query(sql, tagRowMapper, name);
        return tags.stream().findFirst();
    }

    public Optional<Tag> findById(Long id) {
        String sql = "SELECT * FROM tags WHERE id = ?";
        List<Tag> tags = jdbcTemplate.query(sql, tagRowMapper, id);
        return tags.stream().findFirst();
    }

    public List<Tag> findAll() {
        String sql = "SELECT * FROM tags";
        return jdbcTemplate.query(sql, tagRowMapper);
    }

    public Tag save(Tag tag) {
        String sql = """
            INSERT INTO tags (name)
            VALUES (?)
            RETURNING id
        """;
        Long id = jdbcTemplate.queryForObject(sql, Long.class, tag.getName());
        tag.setId(id);
        return tag;
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM tags WHERE id = ?", id);
    }
}
