-- Table: schm_sp.user_auth_token

-- DROP TABLE IF EXISTS schm_sp.user_auth_token;

CREATE TABLE IF NOT EXISTS schm_sp.user_auth_token
(
    user_id bigint,
    c_date timestamp with time zone,
    atk character varying COLLATE pg_catalog."default" NOT NULL,
    last_check_time timestamp with time zone,
    sign_no character varying COLLATE pg_catalog."default",
    state_id integer,
    user_identifier character varying COLLATE pg_catalog."default",
    email_id character varying COLLATE pg_catalog."default",
    tenant_id character varying COLLATE pg_catalog."default",
    active boolean,
    CONSTRAINT user_auth_token_pkey PRIMARY KEY (atk)
)


-- Table: schm_sp.user_auth_token_history

-- DROP TABLE IF EXISTS schm_sp.user_auth_token_history;

CREATE TABLE IF NOT EXISTS schm_sp.user_auth_token_history
(
    user_id bigint,
    c_date timestamp with time zone,
    atk character varying COLLATE pg_catalog."default" NOT NULL,
    last_check_time timestamp with time zone,
    sign_no character varying COLLATE pg_catalog."default",
    state_id integer,
    user_identifier character varying COLLATE pg_catalog."default",
    email_id character varying COLLATE pg_catalog."default",
    tenant_id character varying COLLATE pg_catalog."default",
    active boolean,
    CONSTRAINT user_auth_token_history_pkey PRIMARY KEY (atk)
)
