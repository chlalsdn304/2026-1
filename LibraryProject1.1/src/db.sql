-- 1. 데이터베이스 생성 및 선택
CREATE DATABASE IF NOT EXISTS library;
USE library;

-- 2. 도서(books) 테이블 생성
CREATE TABLE IF NOT EXISTS books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(100),
    available BOOLEAN DEFAULT TRUE,
    borrower_id VARCHAR(50) DEFAULT NULL
);

-- 3. 사용자(users) 테이블 생성
CREATE TABLE IF NOT EXISTS users (
    userid VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER'
);

-- 4. 테스트용 샘플 데이터 넣기
INSERT INTO books (title, author) VALUES ('자바의 정석', '남궁성');
INSERT INTO users (userid, password, role) VALUES ('admin', '1111', 'ADMIN');
INSERT INTO users (userid, password, role) VALUES ('user', '1111', 'USER');