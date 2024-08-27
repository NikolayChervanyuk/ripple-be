CREATE TABLE app_user
(
    id                           UUID                        NOT NULL,
    version                      INTEGER,
    creation_date                TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified_date           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    email                        VARCHAR(320)                NOT NULL,
    username                     VARCHAR(40)                 NOT NULL,
    full_name                    VARCHAR(100),
    password                     CHAR(60)                    NOT NULL,
    bio                          VARCHAR(250),
    role                         VARCHAR(255)                NOT NULL,
    followers                    BIGINT                      NOT NULL,
    following                    BIGINT                      NOT NULL,
    posts_count                  BIGINT                      NOT NULL,
    is_active                    BOOLEAN                     NOT NULL,
    last_active                  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_issued_token_revocation TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_appuser PRIMARY KEY (id)
);

CREATE TABLE chat
(
    id                 UUID                        NOT NULL,
    version            INTEGER,
    creation_date      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    chat_name          VARCHAR(255),
    CONSTRAINT pk_chat PRIMARY KEY (id)
);

CREATE TABLE chat_user
(
    id                 UUID                        NOT NULL,
    version            INTEGER,
    creation_date      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    chat_id            UUID                        NOT NULL,
    user_id            UUID                        NOT NULL,
    CONSTRAINT pk_chatuser PRIMARY KEY (id)
);

CREATE TABLE comment_like
(
    id                 UUID                        NOT NULL,
    version            INTEGER,
    creation_date      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    author_id          UUID                        NOT NULL,
    parent_comment_id  UUID                        NOT NULL,
    CONSTRAINT pk_commentlike PRIMARY KEY (id)
);

CREATE TABLE comment_reply
(
    id                 UUID                        NOT NULL,
    comment            VARCHAR(4096),
    author_id          UUID                        NOT NULL,
    likes_count        BIGINT,
    version            INTEGER,
    creation_date      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    parent_comment_id  UUID                        NOT NULL,
    CONSTRAINT pk_commentreply PRIMARY KEY (id)
);

CREATE TABLE post
(
    id                 UUID                        NOT NULL,
    version            INTEGER,
    creation_date      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    author_id          UUID                        NOT NULL,
    post_image_dir     VARCHAR(512)                NOT NULL,
    caption            VARCHAR(2048),
    likes_count        BIGINT                      NOT NULL,
    comments_count     BIGINT                      NOT NULL,
    CONSTRAINT pk_post PRIMARY KEY (id)
);

CREATE TABLE post_comment
(
    id                 UUID                        NOT NULL,
    comment            VARCHAR(4096),
    author_id          UUID                        NOT NULL,
    likes_count        BIGINT,
    version            INTEGER,
    creation_date      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    parent_post_id     UUID                        NOT NULL,
    replies_count      BIGINT                      NOT NULL,
    CONSTRAINT pk_postcomment PRIMARY KEY (id)
);

CREATE TABLE post_like
(
    id                 UUID                        NOT NULL,
    version            INTEGER,
    creation_date      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    author_id          UUID                        NOT NULL,
    parent_post_id     UUID                        NOT NULL,
    CONSTRAINT pk_postlike PRIMARY KEY (id)
);

CREATE TABLE reply_like
(
    id                 UUID                        NOT NULL,
    version            INTEGER,
    creation_date      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    author_id          UUID                        NOT NULL,
    parent_reply_id    UUID                        NOT NULL,
    CONSTRAINT pk_replylike PRIMARY KEY (id)
);

CREATE TABLE user_following
(
    id                 UUID                        NOT NULL,
    version            INTEGER,
    creation_date      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id            UUID                        NOT NULL,
    following_id       UUID                        NOT NULL,
    CONSTRAINT pk_userfollowing PRIMARY KEY (id)
);

ALTER TABLE app_user
    ADD CONSTRAINT uc_appuser_email UNIQUE (email);

ALTER TABLE app_user
    ADD CONSTRAINT uc_appuser_username UNIQUE (username);

CREATE INDEX idx_38146b0aa61d05377b7270058 ON comment_like (author_id);

CREATE INDEX idx_42fc08f186877bb112ba43808 ON post_comment (parent_post_id);

CREATE INDEX idx_4bfca7e59d4261f384ec35216 ON chat_user (chat_id, user_id);

CREATE INDEX idx_86a18bf7348ed9654003d4549 ON reply_like (author_id);

CREATE INDEX idx_8891bbe2fb9ac9417b537f15a ON comment_reply (parent_comment_id);

CREATE INDEX idx_94c0bb8c3f9123c66522ef6fc ON user_following (user_id, following_id);

CREATE INDEX idx_9fb988b3a53690c9d415ca57c ON post (author_id);

CREATE INDEX idx_afb22d6cd4d20eb557088d80b ON post_like (author_id);