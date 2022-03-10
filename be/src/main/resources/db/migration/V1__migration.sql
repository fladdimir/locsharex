CREATE TABLE app_user (
	id uuid NOT NULL,
	issuer_name varchar(255) NOT NULL,
	name varchar(255) NOT NULL,
	lat float8 NULL,
	lng float8 NULL,
	sub varchar(255) NOT NULL,
	CONSTRAINT app_user_pkey PRIMARY KEY (id),
	CONSTRAINT ukca699byqpy39i3fkohu5ya69m UNIQUE (name),
	CONSTRAINT ukr3uqgc15a4dtmplfc4bdrdvjx UNIQUE (sub, issuer_name)
);

CREATE TABLE app_user_app_user (
	shared_by_id uuid NOT NULL,
	shared_with_id uuid NOT NULL,
	CONSTRAINT app_user_app_user_pkey PRIMARY KEY (shared_by_id, shared_with_id)
);

-- public.app_user_app_user foreign keys
ALTER TABLE public.app_user_app_user ADD CONSTRAINT fkg0pj44kgrtn2cn4dt7jy8kjy4 FOREIGN KEY (shared_by_id) REFERENCES app_user(id);
ALTER TABLE public.app_user_app_user ADD CONSTRAINT fkijij8w81ka2rko7534g7u21aw FOREIGN KEY (shared_with_id) REFERENCES app_user(id);
