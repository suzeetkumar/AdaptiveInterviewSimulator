-- V5__add_audio_path_to_answers.sql
-- Adds audio_path to answers table only if it does not exist
-- Compatible with MySQL 5.7, 8.x and Flyway

SET @exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE table_schema = DATABASE()
      AND table_name = 'answers'
      AND column_name = 'audio_path'
);

SET @sql = IF(
    @exists = 0,
    'ALTER TABLE answers ADD COLUMN audio_path VARCHAR(1024) NULL',
    'SELECT 1'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
