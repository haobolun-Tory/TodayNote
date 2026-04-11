-- 初始化用户表
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码(BCrypt)',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '逻辑删除时间(NULL为未删除)',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `folder` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `owner_id` BIGINT NOT NULL COMMENT '作者ID',
    `name` VARCHAR(100) NOT NULL COMMENT '文件夹名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '文件夹描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '逻辑删除时间',
    PRIMARY KEY (`id`),
    KEY `idx_folder_owner` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作者文件夹表';

CREATE TABLE `article` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `author_id` BIGINT NOT NULL COMMENT '作者ID',
    `folder_id` BIGINT DEFAULT NULL COMMENT '文件夹ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
    `content_md` MEDIUMTEXT NOT NULL COMMENT 'Markdown 正文',
    `tags` VARCHAR(255) DEFAULT NULL COMMENT '标签，逗号分隔',
    `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态',
    `primary_interest_code` VARCHAR(50) DEFAULT NULL COMMENT '主兴趣编码',
    `view_count` BIGINT NOT NULL DEFAULT 0 COMMENT '阅读量',
    `like_count` BIGINT NOT NULL DEFAULT 0 COMMENT '点赞量',
    `comment_count` BIGINT NOT NULL DEFAULT 0 COMMENT '评论量',
    `published_at` DATETIME DEFAULT NULL COMMENT '发布时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '逻辑删除时间',
    PRIMARY KEY (`id`),
    KEY `idx_article_author` (`author_id`, `updated_at`),
    KEY `idx_article_status_published` (`status`, `published_at`),
    KEY `idx_article_interest` (`primary_interest_code`, `published_at`),
    KEY `idx_article_folder` (`folder_id`, `updated_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

CREATE TABLE `article_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
    `content` VARCHAR(1000) NOT NULL COMMENT '评论内容',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` DATETIME DEFAULT NULL COMMENT '逻辑删除时间',
    PRIMARY KEY (`id`),
    KEY `idx_comment_article` (`article_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章评论表';

CREATE TABLE `article_like` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_like` (`article_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章点赞表';

-- 预置一个测试用户 (密码: 123456 的 BCrypt hash，仅供测试)
-- INSERT INTO `users` (`username`, `password`, `nickname`) VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7qa8qX57.', '管理员');
