-- V1__init_schema.sql

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255),
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  profile JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE question_bank (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  category VARCHAR(50),
  prompt_text TEXT,
  difficulty TINYINT,
  tags JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sessions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  type VARCHAR(50),
  settings JSON,
  started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  ended_at TIMESTAMP NULL,
  summary JSON,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE session_questions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_id BIGINT NOT NULL,
  question_id BIGINT NULL,
  prompt_text TEXT,
  sequence_index INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE
);

CREATE TABLE answers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_question_id BIGINT NOT NULL,
  answer_text MEDIUMTEXT,
  audio_path VARCHAR(1024),
  duration_ms INT,
  status VARCHAR(50) DEFAULT 'pending',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (session_question_id) REFERENCES session_questions(id) ON DELETE CASCADE
);

CREATE TABLE analysis (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  answer_id BIGINT NOT NULL,
  content_score INT,
  clarity_score INT,
  confidence_score INT,
  filler_count INT,
  pause_metrics JSON,
  ai_feedback TEXT,
  raw_ai_response JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (answer_id) REFERENCES answers(id) ON DELETE CASCADE
);
