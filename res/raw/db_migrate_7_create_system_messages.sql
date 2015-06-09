CREATE TABLE IF NOT EXISTS "system_messages" ("id"                 INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                                              "server_id"          INTEGER,
                                              "server_revision_id" INTEGER,
                                              "valid_from"         NUMERIC,
                                              "valid_to"           NUMERIC,
                                              "key"                TEXT,
                                              "value"              TEXT);