-- Add password_hash (nullable for now so existing rows don't break)
ALTER TABLE users
  ADD COLUMN IF NOT EXISTS password_hash text;

-- Helpful index for case-insensitive lookups
CREATE INDEX IF NOT EXISTS idx_users_username_lower
  ON users ((lower(username)));
