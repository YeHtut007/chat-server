-- Add 'type' column if it's missing and enforce NOT NULL with default 'text'
ALTER TABLE public.messages
  ADD COLUMN IF NOT EXISTS type text;

UPDATE public.messages
SET type = 'text'
WHERE type IS NULL;

ALTER TABLE public.messages
  ALTER COLUMN type SET NOT NULL,
  ALTER COLUMN type SET DEFAULT 'text';
