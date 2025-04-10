SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE `ai-rag-knowledge` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `ai-rag-knowledge`;

CREATE TABLE tb_user (
    id BIGSERIAL PRIMARY KEY,
    phone VARCHAR(11) NOT NULL,  -- 手机号通常不允许为空
    password VARCHAR(128) NOT NULL,  -- 密码建议存储哈希值
    nick_name VARCHAR(32),        -- 昵称可为空
    icon VARCHAR(255),            -- 头像URL
    create_time TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP,  -- 创建时间自动填充
    update_time TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP   -- 更新时间自动填充
);


CREATE TABLE IF NOT EXISTS public.vector_store (
    id uuid PRIMARY KEY COMMENT '知识库唯一id',
    content text COMMENT '原文',
    metadata jsonb COMMENT '标签',
    embedding vector(768) COMMENT '文本向量表示'
);