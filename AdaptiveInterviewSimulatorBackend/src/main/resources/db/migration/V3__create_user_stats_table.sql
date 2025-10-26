CREATE TABLE user_stats (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  date DATE NOT NULL,
  avg_score DOUBLE,
  sessions_completed INT DEFAULT 0,
  avg_content_score DOUBLE,
  avg_clarity_score DOUBLE,
  avg_confidence_score DOUBLE
);
