-- V008__friends_and_requests.sql
-- Friends & Requests schema for Telegram-style flow

-- 1) Friend requests
create table if not exists friend_request (
  id uuid primary key,
  from_user uuid not null references users(id) on delete cascade,
  to_user   uuid not null references users(id) on delete cascade,
  status    text not null default 'PENDING',  -- PENDING / ACCEPTED / DECLINED / CANCELED
  created_at   timestamptz not null default now(),
  responded_at timestamptz,

  -- sanity checks
  constraint chk_fr_not_self check (from_user <> to_user),
  constraint chk_fr_status   check (status in ('PENDING','ACCEPTED','DECLINED','CANCELED'))
);

-- Fast lookups
create index if not exists idx_friend_request_to_user on friend_request(to_user);
create index if not exists idx_friend_request_from_user on friend_request(from_user);

-- Prevent duplicate *pending* requests in the same direction
create unique index if not exists uq_fr_pending_direct
  on friend_request(from_user, to_user)
  where status = 'PENDING';

-- Also prevent two users having two opposite pending requests at the same time.
-- Use an unordered pair uniqueness via LEAST/GREATEST (works with UUID in Postgres).
create unique index if not exists uq_fr_pending_unordered_pair
  on friend_request(least(from_user, to_user), greatest(from_user, to_user))
  where status = 'PENDING';

-- 2) Friendships (store as two directed rows: user_id -> friend_id)
create table if not exists friendship (
  user_id   uuid not null references users(id) on delete cascade,
  friend_id uuid not null references users(id) on delete cascade,
  since timestamptz not null default now(),
  primary key (user_id, friend_id),
  constraint chk_friend_not_self check (user_id <> friend_id)
);

create index if not exists idx_friendship_user on friendship(user_id);
