-- V4__add_audio_tone_columns.sql
-- Add audio_path and pause_metrics only when missing (works on older MySQL)

-- add audio_path to answers if not present
SET @exists = (SELECT COUNT(*)
               FROM information_schema.COLUMNS
               WHERE table_schema = DATABASE()
                 AND table_name = 'answers'
                 AND column_name = 'audio_path');

SET @sql = IF(@exists = 0,
              'ALTER TABLE answers ADD COLUMN audio_path VARCHAR(255) NULL',
              'SELECT 1');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- add pause_metrics to analysis if not present
SET @exists = (SELECT COUNT(*)
               FROM information_schema.COLUMNS
               WHERE table_schema = DATABASE()
                 AND table_name = 'analysis'
                 AND column_name = 'pause_metrics');

SET @sql = IF(@exists = 0,
              'ALTER TABLE analysis ADD COLUMN pause_metrics JSON NULL',
              'SELECT 1');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
