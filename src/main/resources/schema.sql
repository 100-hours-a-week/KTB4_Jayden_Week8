CREATE TABLE `users` (
                         `user_id`	bigint	NOT NULL,
                         `email`	varchar(255)	NOT NULL,
                         `password`	varchar(255)	NOT NULL,
                         `nickname`	varchar(10)	NOT NULL,
                         `profile_image`	varchar(2048)	NULL,
                         `created_at`	timestamp	NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `deleted_at`	timestamp	NULL DEFAULT NULL,
                         `information_updated_at`	timestamp	NULL DEFAULT NULL,
                         `password_updated_at`	timestamp	NULL DEFAULT NULL,

                         CONSTRAINT `PK_USERS` PRIMARY KEY (`user_id`),
                         CONSTRAINT `UK_USERS_EMAIL` UNIQUE (`email`),
                         CONSTRAINT `UK_USERS_NICKNAME` UNIQUE (`nickname`)
);

CREATE TABLE `articles` (
                            `article_id`	bigint	NOT NULL,
                            `user_id`	bigint	NOT NULL,
                            `title`	varchar(26)	NOT NULL,
                            `content`	mediumtext	NOT NULL,
                            `content_images`	json	NULL,
                            `created_at`	timestamp	NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_at`	timestamp	NULL DEFAULT NULL,
                            `deleted_at`	timestamp	NULL DEFAULT NULL,
                            `is_article_hidden`	boolean	NOT NULL	DEFAULT 0,

                            CONSTRAINT `PK_ARTICLES` PRIMARY KEY (`article_id`),

                            CONSTRAINT `FK_USERS_TO_ARTICLES`
                                FOREIGN KEY (`user_id`)
                                    REFERENCES `users` (`user_id`)
);

CREATE TABLE `article_update_history` (
                                          `article_history_id`	bigint	NOT NULL,
                                          `article_id`	bigint	NOT NULL,
                                          `title`	varchar(26)	NOT NULL,
                                          `content`	mediumtext	NOT NULL,
                                          `content_images`	json	NULL,
                                          `created_at`	timestamp	NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                          CONSTRAINT `PK_ARTICLE_UPDATE_HISTORY` PRIMARY KEY (`article_history_id`),

                                          CONSTRAINT `FK_ARTICLES_TO_ARTICLE_UPDATE_HISTORY`
                                              FOREIGN KEY (`article_id`)
                                                  REFERENCES `articles` (`article_id`)
);

CREATE TABLE `comments` (
                            `comment_id`	bigint	NOT NULL,
                            `article_id`	bigint	NOT NULL,
                            `user_id`	bigint	NOT NULL,
                            `parent_comment_id`	bigint	NULL DEFAULT NULL,
                            `comment_text`	text	NOT NULL,
                            `created_at`	timestamp	NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_at`	timestamp	NULL DEFAULT NULL,
                            `deleted_at`	timestamp	NULL DEFAULT NULL,

                            CONSTRAINT `PK_COMMENTS` PRIMARY KEY (`comment_id`),

                            CONSTRAINT `FK_ARTICLES_TO_COMMENTS`
                                FOREIGN KEY (`article_id`)
                                    REFERENCES `articles` (`article_id`),

                            CONSTRAINT `FK_USERS_TO_COMMENTS`
                                FOREIGN KEY (`user_id`)
                                    REFERENCES `users` (`user_id`),

                            CONSTRAINT `FK_COMMENTS_TO_PARENT_COMMENTS`
                                FOREIGN KEY (`parent_comment_id`)
                                    REFERENCES `comments` (`comment_id`)
);

CREATE TABLE `temp_articles` (
                                 `user_id`	bigint	NOT NULL,
                                 `title`	varchar(26)	NULL,
                                 `content`	mediumtext	NOT NULL,
                                 `content_images`	json	NULL,
                                 `saved_at`	timestamp	NOT NULL	DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                 CONSTRAINT `PK_TEMP_ARTICLES` PRIMARY KEY (`user_id`),

                                 CONSTRAINT `FK_USERS_TO_TEMP_ARTICLES`
                                     FOREIGN KEY (`user_id`)
                                         REFERENCES `users` (`user_id`)
);

CREATE TABLE `article_reports` (
                                   `report_id`	bigint	NOT NULL,
                                   `article_id`	bigint	NOT NULL,
                                   `user_id`	bigint	NOT NULL,
                                   `report_type`	varchar(100)	NOT NULL	DEFAULT 'spam',
                                   `reason`	varchar(500)	NOT NULL,
                                   `created_at`	timestamp	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
                                   `status`	varchar(20)	NOT NULL	DEFAULT 'waiting',

                                   CONSTRAINT `PK_ARTICLE_REPORTS` PRIMARY KEY (`report_id`),

                                   CONSTRAINT `FK_ARTICLES_TO_ARTICLE_REPORTS`
                                       FOREIGN KEY (`article_id`)
                                           REFERENCES `articles` (`article_id`),

                                   CONSTRAINT `FK_USERS_TO_ARTICLE_REPORTS`
                                       FOREIGN KEY (`user_id`)
                                           REFERENCES `users` (`user_id`),

                                   CONSTRAINT `UK_ARTICLE_REPORTS_ARTICLE_ID_USER_ID`
                                       UNIQUE (`article_id`, `user_id`)
);

CREATE TABLE `article_likes` (
                                 `article_like_id`	bigint	NOT NULL,
                                 `article_id`	bigint	NOT NULL,
                                 `user_id`	bigint	NOT NULL,
                                 `created_at`	timestamp	NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT `PK_ARTICLE_LIKES` PRIMARY KEY (`article_like_id`),

                                 CONSTRAINT `FK_ARTICLES_TO_ARTICLE_LIKES`
                                     FOREIGN KEY (`article_id`)
                                         REFERENCES `articles` (`article_id`),

                                 CONSTRAINT `FK_USERS_TO_ARTICLE_LIKES`
                                     FOREIGN KEY (`user_id`)
                                         REFERENCES `users` (`user_id`),

                                 CONSTRAINT `UK_ARTICLE_LIKES_ARTICLE_ID_USER_ID`
                                     UNIQUE (`article_id`, `user_id`)
);

CREATE TABLE `article_views` (
                                 `article_view_id`	bigint	NOT NULL,
                                 `article_id`	bigint	NOT NULL,
                                 `user_id`	bigint	NOT NULL,
                                 `updated_at`	timestamp	NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                 CONSTRAINT `PK_ARTICLE_VIEWS` PRIMARY KEY (`article_view_id`),

                                 CONSTRAINT `FK_ARTICLES_TO_ARTICLE_VIEWS`
                                     FOREIGN KEY (`article_id`)
                                         REFERENCES `articles` (`article_id`),

                                 CONSTRAINT `FK_USERS_TO_ARTICLE_VIEWS`
                                     FOREIGN KEY (`user_id`)
                                         REFERENCES `users` (`user_id`),

                                 CONSTRAINT `UK_ARTICLE_VIEWS_ARTICLE_ID_USER_ID`
                                     UNIQUE (`article_id`, `user_id`)
);

CREATE TABLE `article_stats` (
                                 `article_id`	bigint	NOT NULL,
                                 `comment_count`	bigint	NOT NULL	DEFAULT 0,
                                 `article_like_count`	bigint	NOT NULL	DEFAULT 0,
                                 `article_view_count`	bigint	NOT NULL	DEFAULT 0,
                                 `article_report_count`	bigint	NOT NULL	DEFAULT 0,

                                 CONSTRAINT `PK_ARTICLE_STATS` PRIMARY KEY (`article_id`),

                                 CONSTRAINT `FK_ARTICLES_TO_ARTICLE_STATS`
                                     FOREIGN KEY (`article_id`)
                                         REFERENCES `articles` (`article_id`)
);


CREATE SEQUENCE refresh_token_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE refresh_token (
                               id BIGINT NOT NULL DEFAULT NEXT VALUE FOR refresh_token_seq,
                               token VARCHAR(255) NOT NULL,
                               user_id BIGINT NOT NULL,
                               expires_at TIMESTAMP NOT NULL,

                               CONSTRAINT pk_refresh_token PRIMARY KEY (id),
                               CONSTRAINT uk_refresh_token_token UNIQUE (token)
);