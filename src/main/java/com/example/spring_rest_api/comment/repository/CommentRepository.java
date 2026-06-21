package com.example.spring_rest_api.comment.repository;

import com.example.spring_rest_api.comment.entity.Comment;
import com.example.spring_rest_api.comment.service.response.CommentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
                select new com.example.spring_rest_api.comment.service.response.CommentResponse(c.commentId, c.user.userId, c.user.profileImage, c.commentText, c.createdAt, c.updatedAt, c.deletedAt, c.parentCommentId)
                    from Comment c
                    left join Comment cp
                        on c.commentId = cp.parentCommentId
                    where c.article.articleId = :articleId
                        and (
                            c.deletedAt is null
                            or (c.deletedAt is not null and cp.commentId is not null and cp.deletedAt is null)
                        )
                    order by c.parentCommentId asc, c.commentId asc
                    limit :pageSize
            """)
    List<CommentResponse> findAllInfiniteScroll(Long articleId, Long pageSize);

    @Query("""
                select new com.example.spring_rest_api.comment.service.response.CommentResponse(c.commentId, c.user.userId, c.user.profileImage, c.commentText, c.createdAt, c.updatedAt, c.deletedAt, c.parentCommentId)
                    from Comment c
                    left join Comment cp
                        on c.commentId = cp.parentCommentId
                    where c.article.articleId = :articleId
                        and (
                            c.deletedAt is null
                            or (c.deletedAt is not null and cp.commentId is not null and cp.deletedAt is null)
                        )
                        and (
                            c.parentCommentId > :lastParentCommentId or
                            (c.parentCommentId = :lastParentCommentId and c.commentId > :lastCommentId)
                        )
                    order by c.parentCommentId asc, c.commentId asc
                    limit :pageSize
            """)
    List<CommentResponse> findAllInfiniteScroll(Long articleId, Long pageSize, Long lastParentCommentId, Long lastCommentId);
}
