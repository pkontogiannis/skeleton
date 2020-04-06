CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA extensions;

CREATE TABLE skeleton.user (
-- TODO by Petros Kontogiannis: change the SERIAL to INT GENERATED ALWAYS AS IDENTITY
--    user_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY
    user_id SERIAL PRIMARY KEY
  , user_uuid UUID UNIQUE NOT NULL DEFAULT extensions.uuid_generate_v4()
  , email TEXT UNIQUE NOT NULL
  , password TEXT NOT NULL
  , first_name TEXT NOT NULL
  , last_name TEXT NOT NULL
  , role TEXT NOT NULL
);