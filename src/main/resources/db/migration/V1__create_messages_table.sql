create table if not exists messages (
    id bigserial primary key,
    content text not null,
    created_at timestamptz not null default now()
);
