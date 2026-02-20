create table if not exists optimistic_documents (
    id bigserial primary key,
    title text not null,
    content text not null,
    version bigint not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
