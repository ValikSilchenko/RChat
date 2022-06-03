CREATE SEQUENCE IF NOT EXISTS public.users_id_seq;

CREATE TABLE IF NOT EXISTS public.Users (
                ID INTEGER NOT NULL DEFAULT nextval('public.users_id_seq'),
                Username VARCHAR(20) NOT NULL,
                Email VARCHAR(60) NOT NULL,
                Phone VARCHAR(10) NOT NULL,
                Password VARCHAR(60) NOT NULL,
                CONSTRAINT users_pk PRIMARY KEY (ID)
);


ALTER SEQUENCE public.users_id_seq OWNED BY public.Users.ID;

CREATE SEQUENCE IF NOT EXISTS public.personal_messages_msg_id_seq;

CREATE TABLE IF NOT EXISTS public.Personal_Messages (
                Msg_ID INTEGER NOT NULL DEFAULT nextval('public.personal_messages_msg_id_seq'),
                Sender_ID INTEGER NOT NULL,
                recipient_id INTEGER NOT NULL,
                Time TIME NOT NULL,
                Date DATE NOT NULL,
                Message_Text VARCHAR NOT NULL,
                CONSTRAINT personal_messages_pk PRIMARY KEY (Msg_ID)
);


ALTER SEQUENCE public.personal_messages_msg_id_seq OWNED BY public.Personal_Messages.Msg_ID;

CREATE SEQUENCE IF NOT EXISTS public.channels_id_seq;

CREATE TABLE IF NOT EXISTS public.Channels (
                ID INTEGER NOT NULL DEFAULT nextval('public.channels_id_seq'),
                Owner_ID INTEGER NOT NULL,
                Channel_Name VARCHAR(20) NOT NULL,
                CONSTRAINT channels_pk PRIMARY KEY (ID)
);


ALTER SEQUENCE public.channels_id_seq OWNED BY public.Channels.ID;

CREATE SEQUENCE IF NOT EXISTS public.channel_messages_msg_id_seq;

CREATE TABLE IF NOT EXISTS public.Channel_Messages (
                Msg_ID INTEGER NOT NULL DEFAULT nextval('public.channel_messages_msg_id_seq'),
                Channel_ID INTEGER NOT NULL,
                Sender_ID INTEGER NOT NULL,
                Time TIME NOT NULL,
                Date DATE NOT NULL,
                Message_Text VARCHAR NOT NULL,
                CONSTRAINT channel_messages_pk PRIMARY KEY (Msg_ID)
);


ALTER SEQUENCE public.channel_messages_msg_id_seq OWNED BY public.Channel_Messages.Msg_ID;

CREATE TABLE IF NOT EXISTS public.Members (
                Channel_ID INTEGER NOT NULL,
                User_ID INTEGER NOT NULL,
                CONSTRAINT members_pk PRIMARY KEY (Channel_ID, User_ID)
);


ALTER TABLE public.Members ADD CONSTRAINT users_members_fk
FOREIGN KEY (User_ID)
REFERENCES public.Users (ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.Channels ADD CONSTRAINT channels_users_fk
FOREIGN KEY (Owner_ID)
REFERENCES public.Users (ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.Channel_Messages ADD CONSTRAINT users_channel_messages_fk
FOREIGN KEY (Sender_ID)
REFERENCES public.Users (ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.Personal_Messages ADD CONSTRAINT users_personal_messages_fk
FOREIGN KEY (Sender_ID)
REFERENCES public.Users (ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.Personal_Messages ADD CONSTRAINT users_personal_messages_fk1
FOREIGN KEY (recipient_id)
REFERENCES public.Users (ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.Members ADD CONSTRAINT channels_members_fk
FOREIGN KEY (Channel_ID)
REFERENCES public.Channels (ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.Channel_Messages ADD CONSTRAINT channels_channel_messages_fk
FOREIGN KEY (Channel_ID)
REFERENCES public.Channels (ID)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;
