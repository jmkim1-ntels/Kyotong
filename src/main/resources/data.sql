CREATE TABLE post (
    id SERIAL PRIMARY KEY,          -- 기본 키로 사용되는 자동 증가하는 ID
    title VARCHAR(255) NOT NULL,    -- 게시글 제목
    content TEXT,                   -- 게시글 내용
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- 생성일자, 기본값은 현재 시간
);