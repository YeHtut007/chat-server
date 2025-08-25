-- Add missing sender_username column if it doesn't exist
ALTER TABLE messages
  ADD COLUMN IF NOT EXISTS sender_username text;

-- Backfill nulls to a safe value, then enforce NOT NULL
UPDATE messages SET sender_username = 'system' WHERE sender_username IS NULL;
ALTER TABLE messages ALTER COLUMN sender_username SET NOT NULL;
