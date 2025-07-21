package com.example.blog.repository;

import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Post> postRowMapper = (rs, rowNum) -> {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setText(rs.getString("text"));
        post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        post.setImage(rs.getBytes("image"));
        return post;
    };

    public Optional<Post> findById(Long id) {
        String sql = "SELECT * FROM posts WHERE id = ?";
        List<Post> posts = jdbcTemplate.query(sql, postRowMapper, id);
        return posts.stream().findFirst();
    }

    public List<Post> findAll() {
        String sql = "SELECT * FROM posts";
        return jdbcTemplate.query(sql, postRowMapper);
    }

    public Page<Post> findAll(Pageable pageable) {
        String sql = "SELECT * FROM posts ORDER BY id LIMIT ? OFFSET ?";
        List<Post> posts = jdbcTemplate.query(sql, postRowMapper,
                pageable.getPageSize(), pageable.getOffset());

        Long total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM posts", Long.class);
        return new PageImpl<>(posts, pageable, total);
    }

    public Optional<Post> findByIdWithCommentsAndTags(Long id) {
        String postSql = "SELECT * FROM posts WHERE id = ?";
        List<Post> posts = jdbcTemplate.query(postSql, postRowMapper, id);

        if (posts.isEmpty()) return Optional.empty();

        Post post = posts.get(0);

        // fetch comments
        String commentSql = "SELECT * FROM comments WHERE post_id = ?";
        List<Comment> comments = jdbcTemplate.query(commentSql, (rs, rowNum) -> {
            Comment c = new Comment();
            c.setId(rs.getLong("id"));
            c.setText(rs.getString("text"));
            c.setPost(post); // устанавливаем объект Post
            c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            c.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return c;
        }, id);

        // fetch tags
        String tagSql = """
                SELECT t.* FROM tags t
                JOIN post_tags pt ON pt.tag_id = t.id
                WHERE pt.post_id = ?
                """;
        List<Tag> tags = jdbcTemplate.query(tagSql, (rs, rowNum) -> {
            Tag t = new Tag();
            t.setId(rs.getLong("id"));
            t.setName(rs.getString("name"));
            return t;
        }, id);

        post.setComments(comments);
        post.setTags(new HashSet<>(tags));

        return Optional.of(post);
    }

    public Page<Post> findByTagStartingWith(String prefix, Pageable pageable) {
        String sql = """
                SELECT DISTINCT p.* FROM posts p
                JOIN post_tags pt ON p.id = pt.post_id
                JOIN tags t ON pt.tag_id = t.id
                WHERE LOWER(t.name) LIKE LOWER(?)
                ORDER BY p.id
                LIMIT ? OFFSET ?
                """;

        String countSql = """
                SELECT COUNT(DISTINCT p.id) FROM post p
                JOIN post_tags pt ON p.id = pt.post_id
                JOIN tags t ON pt.tag_id = t.id
                WHERE LOWER(t.name) LIKE LOWER(?)
                """;

        List<Post> posts = jdbcTemplate.query(sql, postRowMapper,
                prefix.toLowerCase() + "%", pageable.getPageSize(), pageable.getOffset());

        Long total = jdbcTemplate.queryForObject(countSql, Long.class, prefix.toLowerCase() + "%");

        return new PageImpl<>(posts, pageable, total);
    }

    public Post save(Post post) {
        String insertSql = """
            INSERT INTO posts (title, text, image, likes, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id
            """;

        Long newId = jdbcTemplate.queryForObject(insertSql, Long.class,
                post.getTitle(),
                post.getText(),
                post.getImage(),
                post.getLikes(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );

        post.setId(newId);

        // вставка связей с тегами
        if (post.getTags() != null) {
            for (Tag tag : post.getTags()) {
                jdbcTemplate.update("INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)",
                        post.getId(), tag.getId());
            }
        }

        return post;
    }

    public void deleteById(Long id) {
        String deleteTagsSql = "DELETE FROM post_tags WHERE post_id = ?";
        jdbcTemplate.update(deleteTagsSql, id);

        String deleteCommentsSql = "DELETE FROM comments WHERE post_id = ?";
        jdbcTemplate.update(deleteCommentsSql, id);

        String deletePostSql = "DELETE FROM posts WHERE id = ?";
        jdbcTemplate.update(deletePostSql, id);
    }

    public Post update(Post post) {
        String sql = "UPDATE posts SET title = ?, text = ?, image = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                post.getTitle(),
                post.getText(),
                post.getImage(),
                post.getUpdatedAt(),
                post.getId());

        updatePostTags(post.getId(), post.getTags());

        return post;
    }
    private void updatePostTags(Long postId, Set<Tag> tags) {

        // Добавляем новые
        for (Tag tag : tags) {
            jdbcTemplate.update("INSERT INTO post_tags (post_id, tag_id) VALUES (?, ?)", postId, tag.getId());
        }
    }
}
