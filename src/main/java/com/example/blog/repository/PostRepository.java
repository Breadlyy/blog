package com.example.blog.repository;

import com.example.blog.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
    SELECT DISTINCT p FROM Post p
    LEFT JOIN FETCH p.comments
    LEFT JOIN FETCH p.tags
    WHERE p.id = :id
    """)
    Optional<Post> findById(Long id);
    List<Post> findAll();
    @Query("""
    SELECT DISTINCT p FROM Post p
    LEFT JOIN FETCH p.tags
    LEFT JOIN FETCH p.comments
""")
    Page<Post> findAll(Pageable page);
    @Query("""
    SELECT DISTINCT p FROM Post p
    LEFT JOIN FETCH p.comments
    LEFT JOIN FETCH p.tags
    WHERE p.id = :id
    """)
    Optional<Post> findByIdWithCommentsAndTags(@Param("id") Long id);
}
