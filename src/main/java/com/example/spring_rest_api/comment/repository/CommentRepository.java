package com.example.spring_rest_api.comment.repository;

import com.example.spring_rest_api.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
                select c.comment_id, c.user_id, c.comment_text, c.parent_comment_id, c.created_at, c.deleted_at, c.updated_at
                    from comment c
                    where c.article_id = :articleId
                    order by c.parent_comment_id asc, c.comment_id asc
                    limit :pageSize
            """)
    List<Comment> findAllInfiniteScroll(Long articleId, Long pageSize);

    @Query("""
                select c.comment_id, c.user_id, c.comment_text, c.parent_comment_id, c.created_at, c.deleted_at, c.updated_at
                    from comment c
                    where c.article_id = :articleId
                        and (c.parent_comment_id > :lastParentCommentId or
                            (c.parent_comment_id = :lastParentCommentId and c.comment_id > :lastCommentId))
                    order by c.parent_comment_id asc, c.comment_id asc
                    limit :pageSize
            """)
    List<Comment> findAllInfiniteScroll(Long articleId, Long pageSize, Long lastParentCommentId, Long lastCommentId);
}
