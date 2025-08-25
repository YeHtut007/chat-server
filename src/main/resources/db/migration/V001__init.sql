-- Users & Conversations
create table if not exists users (
  id uuid primary key,
  username text not null unique,
  display_name text not null
);

create table if not exists conversations (
  id uuid primary key,
  type text not null,                 -- 'DM' or 'GROUP'
  title text
);

create table if not exists conversation_member (
  conversation_id uuid not null references conversations(id) on delete cascade,
  user_id uuid not null references users(id) on delete cascade,
  role text not null default 'member',
  joined_at timestamptz not null default now(),
  last_read_at timestamptz not null default 'epoch',
  primary key (conversation_id, user_id)
);

-- Messages (keep simple: store sender username)
create table if not exists messages (
  id bigserial primary key,
  conversation_id uuid not null references conversations(id) on delete cascade,
  sender_username text not null,
  content text not null,
  type text not null default 'text',
  sent_at timestamptz not null default now()
);

create index if not exists idx_messages_conv_time on messages(conversation_id, sent_at desc);
create index if not exists idx_conv_member_user on conversation_member(user_id);
