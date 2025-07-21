package com.example.blog.repository;

import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Comment> commentRowMapper = (rs, rowNum) -> {
        Comment c = new Comment();
        c.setId(rs.getLong("id"));
        c.setText(rs.getString("text"));
        c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        c.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        Post post = new Post();
        post.setId(rs.getLong("post_id"));
        c.setPost(post);
        return c;
    };

    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT * FROM comments WHERE post_id = ?";
        return jdbcTemplate.query(sql, commentRowMapper, postId);
    }

    public Optional<Comment> findById(Long id) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        List<Comment> comments = jdbcTemplate.query(sql, commentRowMapper, id);
        return comments.stream().findFirst();
    }

    public Comment save(Comment comment) {
        String sql = """
            INSERT INTO comments (text, post_id, created_at, updated_at)
            VALUES (?, ?, ?, ?)
            RETURNING id
        """;
        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                comment.getText(),
                comment.getPost().getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt());
        comment.setId(id);
        return comment;
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM comments WHERE id = ?", id);
    }

    public void update(Comment comment) {
        String sql = "UPDATE comments SET text = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql, comment.getText(), comment.getUpdatedAt(), comment.getId());
    }
}
