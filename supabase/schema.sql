-- Chronix: sincronización de datos; las alarmas se ejecutan localmente.
create table if not exists public.chronix_records (
  user_id uuid not null default auth.uid(),
  record_id text not null,
  record_type text not null,
  payload jsonb not null default '{}'::jsonb,
  client_updated_at timestamptz,
  server_updated_at timestamptz not null default now(),
  deleted_at timestamptz,
  primary key (user_id, record_id)
);
create index if not exists chronix_records_user_type_idx on public.chronix_records(user_id, record_type);
alter table public.chronix_records enable row level security;
drop policy if exists "chronix select own" on public.chronix_records;
create policy "chronix select own" on public.chronix_records for select to authenticated using ((select auth.uid()) = user_id);
drop policy if exists "chronix insert own" on public.chronix_records;
create policy "chronix insert own" on public.chronix_records for insert to authenticated with check ((select auth.uid()) = user_id);
drop policy if exists "chronix update own" on public.chronix_records;
create policy "chronix update own" on public.chronix_records for update to authenticated using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
drop policy if exists "chronix delete own" on public.chronix_records;
create policy "chronix delete own" on public.chronix_records for delete to authenticated using ((select auth.uid()) = user_id);
revoke all on table public.chronix_records from anon;
grant select,insert,update,delete on table public.chronix_records to authenticated;
