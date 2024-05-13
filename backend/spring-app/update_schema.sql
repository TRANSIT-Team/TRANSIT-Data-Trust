alter table public.orders
    add column destination_date_to timestamp;
alter table public.orders
    add column pick_up_date_to timestamp;
alter table public.orders
    add column pick_up_person text;
alter table public.orders
    add column pick_up_timestamp timestamp;
alter table public.orders_aud
    add column destination_date_to timestamp;
alter table public.orders_aud
    add column destination_date_to_mod boolean;
alter table public.orders_aud
    add column pick_up_date_to timestamp;
alter table public.orders_aud
    add column pick_up_date_to_mod boolean;
alter table public.orders_aud
    add column pick_up_person text;
alter table public.orders_aud
    add column pick_up_person_mod boolean;
alter table public.orders_aud
    add column pick_up_timestamp timestamp;
alter table public.orders_aud
    add column pick_up_timestamp_mod boolean;