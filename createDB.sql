CREATE SEQUENCE public.users_id_seq;

CREATE TABLE public.Users (
                ID INTEGER NOT NULL DEFAULT nextval('public.users_id_seq'),
                Username VARCHAR(20) NOT NULL,
                Email VARCHAR(25) NOT NULL,
                Phone VARCHAR(10) NOT NULL,
                Password VARCHAR(20) NOT NULL,
                CONSTRAINT users_pk PRIMARY KEY (ID)
);


ALTER SEQUENCE public.users_id_seq OWNED BY public.Users.ID;

CREATE SEQUENCE public.personal_messages_msg_id_seq;

CREATE TABLE public.Personal_Messages (
                Msg_ID INTEGER NOT NULL DEFAULT nextval('public.personal_messages_msg_id_seq'),
                Sender_ID INTEGER NOT NULL,
                Time TIME NOT NULL,
                Date DATE NOT NULL,
                Message_Text VARCHAR NOT NULL,
                CONSTRAINT personal_messages_pk PRIMARY KEY (Msg_ID)
);


ALTER SEQUENCE public.personal_messages_msg_id_seq OWNED BY public.Personal_Messages.Msg_ID;

CREATE TABLE public.Personal_Recipient (
                User_ID INTEGER NOT NULL,
                Msg_ID INTEGER NOT NULL,
                CONSTRAINT personal_recipient_pk PRIMARY KEY (User_ID, Msg_ID)
);


CREATE SEQUENCE public.channels_id_seq;

CREATE TABLE public.Channels (
                ID INTEGER NOT NULL DEFAULT nextval('public.channels_id_seq'),
                Owner_ID INTEGER NOT NULL,
                Channel_Name VARCHAR(20) NOT NULL,
                CONSTRAINT channels_pk PRIMARY KEY (ID)
);


ALTER SEQUENCE public.channels_id_seq OWNED BY public.Channels.ID;

CREATE SEQUENCE public.channel_messages_msg_id_seq;

CREATE TABLE public.Channel_Messages (
                Msg_ID INTEGER NOT NULL DEFAULT nextval('public.channel_messages_msg_id_seq'),
                Channel_ID INTEGER NOT NULL,
                Sender_ID INTEGER NOT NULL,
                Time TIME NOT NULL,
                Date DATE NOT NULL,
                Message_Text VARCHAR NOT NULL,
                CONSTRAINT channel_messages_pk PRIMARY KEY (Msg_ID)
);


ALTER SEQUENCE public.channel_messages_msg_id_seq OWNED BY public.Channel_Messages.Msg_ID;

CREATE TABLE public.Members (
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

ALTER TABLE public.Personal_Recipient ADD CONSTRAINT users_personal_recipient_fk
FOREIGN KEY (User_ID)
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

ALTER TABLE public.Personal_Recipient ADD CONSTRAINT personal_messages_personal_recipient_fk
FOREIGN KEY (Msg_ID)
REFERENCES public.Personal_Messages (Msg_ID)
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
