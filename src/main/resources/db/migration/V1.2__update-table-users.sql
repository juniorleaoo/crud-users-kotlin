ALTER TABLE public.users
ALTER COLUMN nick DROP NOT NULL;

ALTER TABLE public.users
DROP CONSTRAINT users_nick_key;

ALTER TABLE public.users
    ADD UNIQUE (name);


