CREATE TABLE public.users (
      id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
      first_name varchar NOT NULL,
      last_name varchar NOT NULL,
      user_name varchar NOT NULL
);
CREATE INDEX users_id_idx ON public.users (id);