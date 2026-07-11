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
            select new com.example.spring_rest_api.comment.service.response.CommentResponse(
                c.commentId,
                u.userId,
                u.profileImage.filePath,
                c.commentText,
                c.createdAt,
                c.updatedAt,
                c.deletedAt,
                p.commentId
            )
            from Comment c
            left join c.user u
            left join c.parentComment p
            where
                c.article.articleId = :articleId
                and (
                    c.deletedAt is null
                    or (
                        c.parentComment is null
                        and exists (
                            select 1
                            from Comment child
                            where child.parentComment.commentId = c.commentId
                                and child.deletedAt is null
                        )
                    )
                )
            order by coalesce(p.commentId, c.commentId) asc,
                case when c.parentComment is null then 0 else 1 end asc,
                c.commentId asc
            limit :pageSize
            """)
    List<CommentResponse> findAllInfiniteScroll(Long articleId, Long pageSize);



    @Query("""
            select new com.example.spring_rest_api.comment.service.response.CommentResponse(
                c.commentId,
                u.userId,
                u.profileImage.filePath,
                c.commentText,
                c.createdAt,
                c.updatedAt,
                c.deletedAt,
                p.commentId
            )
            from Comment c
            left join c.user u
            left join c.parentComment p
            where
                c.article.articleId = :articleId
                and ((c.commentId > :lastCommentId and p.commentId is null)
                    or (c.commentId > :lastCommentId and p.commentId >= :lastCommentId))
                and (
                    c.deletedAt is null
                    or (
                        c.parentComment is null
                        and exists (
                            select 1
                            from Comment child
                            where child.parentComment.commentId = c.commentId
                                and child.deletedAt is null
                        )
                    )
                )
            order by coalesce(p.commentId, c.commentId) asc,
                case when c.parentComment is null then 0 else 1 end asc,
                c.commentId asc
            limit :pageSize
            """)
    List<CommentResponse> findAllInfiniteScroll(Long articleId, Long pageSize, Long lastCommentId);

    @Query("""
            select new com.example.spring_rest_api.comment.service.response.CommentResponse(
                c.commentId,
                u.userId,
                u.profileImage.filePath,
                c.commentText,
                c.createdAt,
                c.updatedAt,
                c.deletedAt,
                p.commentId
            )
            from Comment c
            left join c.user u
            left join c.parentComment p
            where
                c.article.articleId = :articleId
                and (
                    (c.parentComment is null and c.commentId > :lastParentCommentId)
                    or (p.commentId = :lastParentCommentId and c.commentId > :lastCommentId)
                    or p.commentId > :lastParentCommentId
                )
                and (
                    c.deletedAt is null
                    or (
                        c.parentComment is null
                        and exists (
                            select 1
                            from Comment child
                            where child.parentComment.commentId = c.commentId
                                and child.deletedAt is null
                        )
                    )
                )
            order by coalesce(p.commentId, c.commentId) asc,
                case when c.parentComment is null then 0 else 1 end asc,
                c.commentId asc
            limit :pageSize
            """)
    List<CommentResponse> findAllInfiniteScroll(Long articleId, Long pageSize, Long lastParentCommentId, Long lastCommentId);
}
