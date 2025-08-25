-- Convert messages.id from UUID to BIGINT (identity) while preserving rows

-- 1) Drop existing PK constraint (unknown name-safe)
DO $$
DECLARE
  pk_name text;
BEGIN
  SELECT conname INTO pk_name
  FROM pg_constraint
  WHERE conrelid = 'public.messages'::regclass
    AND contype = 'p';
  IF pk_name IS NOT NULL THEN
    EXECUTE format('ALTER TABLE public.messages DROP CONSTRAINT %I', pk_name);
  END IF;
END $$;

-- 2) Add new BIGINT column with sequence/default
--    (BIGSERIAL in-place without rewriting the table definition)
CREATE SEQUENCE IF NOT EXISTS messages_id_seq;

ALTER TABLE public.messages
  ADD COLUMN id_bigint BIGINT;

ALTER TABLE public.messages
  ALTER COLUMN id_bigint SET DEFAULT nextval('messages_id_seq');

-- 3) Backfill existing rows
UPDATE public.messages
SET id_bigint = nextval('messages_id_seq')
WHERE id_bigint IS NULL;

-- 4) Make it NOT NULL
ALTER TABLE public.messages
  ALTER COLUMN id_bigint SET NOT NULL;

-- 5) Drop the old UUID id column, rename bigint column to id
ALTER TABLE public.messages
  DROP COLUMN id;

ALTER TABLE public.messages
  RENAME COLUMN id_bigint TO id;

-- 6) Recreate primary key and own the sequence
ALTER TABLE public.messages
  ADD CONSTRAINT messages_pkey PRIMARY KEY (id);

ALTER SEQUENCE messages_id_seq OWNED BY public.messages.id;
