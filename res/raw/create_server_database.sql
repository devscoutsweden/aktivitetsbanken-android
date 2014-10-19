CREATE TABLE IF NOT EXISTS "user" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "server_id" INTEGER,
     "server_revision_id" INTEGER,
     "api_key" TEXT,
     "display_name" TEXT);

CREATE UNIQUE INDEX "user_server_id_UNIQUE" ON "user" ("server_id" ASC);

CREATE TABLE IF NOT EXISTS "activity" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "server_id" INTEGER,
     "server_revision_id" INTEGER,
     "is_publishable" INTEGER NOT NULL,
     "owner_id" INTEGER,
     "name" TEXT NOT NULL,
     "datetime_published" NUMERIC,
     "datetime_created" NUMERIC NOT NULL,
     "descr_material" TEXT,
     "descr_introduction" TEXT,
     "descr_prepare" TEXT,
     "descr_activity" TEXT NOT NULL,
     "descr_safety" TEXT,
     "descr_notes" TEXT,
     "age_min" INTEGER,
     "age_max" INTEGER,
     "participants_min" INTEGER,
     "participants_max" INTEGER,
     "time_min" INTEGER,
     "time_max" INTEGER,
     "featured" INTEGER,
     "favourite_count" INTEGER,
     CONSTRAINT "fk_activity_user1" FOREIGN KEY ("owner_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_activity_user1_idx" ON "activity" ("owner_id" ASC);

CREATE UNIQUE INDEX "activity_server_id_UNIQUE" ON "activity" ("server_id" ASC);

CREATE UNIQUE INDEX "activity_server_revision_id_UNIQUE" ON "activity" ("server_revision_id" ASC);

CREATE TABLE IF NOT EXISTS "comment" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "server_id" INTEGER,
     "server_revision_id" INTEGER,
     "is_publishable" INTEGER NOT NULL,
     "activity_id" INTEGER NOT NULL,
     "owner_id" INTEGER,
     "text" TEXT,
     "datetime_created" NUMERIC,
     CONSTRAINT "fk_comment_activity1" FOREIGN KEY ("activity_id") REFERENCES "activity" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_comment_user1" FOREIGN KEY ("owner_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_comment_activity1_idx" ON "comment" ("activity_id" ASC);

CREATE INDEX "fk_comment_user1_idx" ON "comment" ("owner_id" ASC);

CREATE UNIQUE INDEX "comment_server_id_UNIQUE" ON "comment" ("server_id" ASC);

CREATE TABLE IF NOT EXISTS "category" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "server_id" INTEGER,
     "server_revision_id" INTEGER,
     "uuid" NONE NOT NULL,
     "group_name" TEXT,
     "name" TEXT NOT NULL,
     "owner_id" INTEGER,
     "status" TEXT NOT NULL,
     CONSTRAINT "fk_category_user1" FOREIGN KEY ("owner_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_category_user1_idx" ON "category" ("owner_id" ASC);

CREATE UNIQUE INDEX "category_uuid_UNIQUE" ON "category" ("uuid" ASC);

CREATE UNIQUE INDEX "category_group_name_UNIQUE" ON "category" ("group_name" ASC,
     "name" ASC);

CREATE UNIQUE INDEX "category_server_id_UNIQUE" ON "category" ("server_id" ASC);

CREATE TABLE IF NOT EXISTS "media" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "server_id" INTEGER,
     "server_revision_id" INTEGER,
     "is_publishable" INTEGER NOT NULL,
     "uri" TEXT,
     "mime_type" TEXT NOT NULL);

CREATE UNIQUE INDEX "media_server_id_UNIQUE" ON "media" ("server_id" ASC);

CREATE TABLE IF NOT EXISTS "reference" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "server_id" INTEGER,
     "server_revision_id" INTEGER,
     "uri" TEXT NOT NULL,
     "description" TEXT);

CREATE UNIQUE INDEX "reference_server_id_UNIQUE" ON "reference" ("server_id" ASC);

CREATE UNIQUE INDEX "uri_UNIQUE" ON "reference" ("uri" ASC);

CREATE TABLE IF NOT EXISTS "favourite_activity" ( "activity_id" INTEGER NOT NULL,
     "user_id" INTEGER NOT NULL,
     PRIMARY KEY ("activity_id",
     "user_id"),
     CONSTRAINT "fk_activity_has_user_activity1" FOREIGN KEY ("activity_id") REFERENCES "activity" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_activity_has_user_user1" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_activity_has_user_user1_idx" ON "favourite_activity" ("user_id" ASC);

CREATE INDEX "fk_activity_has_user_activity1_idx" ON "favourite_activity" ("activity_id" ASC);

CREATE TABLE IF NOT EXISTS "rating" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "server_id" INTEGER,
     "activity_id" INTEGER NOT NULL,
     "datetime_created" NUMERIC NOT NULL,
     "rating" INTEGER NOT NULL,
     "source_uri" TEXT,
     "user_id" INTEGER,
     CONSTRAINT "fk_rating_activity1" FOREIGN KEY ("activity_id") REFERENCES "activity" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_rating_user1" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_rating_activity1_idx" ON "rating" ("activity_id" ASC);

CREATE INDEX "fk_rating_user1_idx" ON "rating" ("user_id" ASC);

CREATE UNIQUE INDEX "rating_user_id_UNIQUE" ON "rating" ("user_id" ASC);

CREATE UNIQUE INDEX "rating_server_id_UNIQUE" ON "rating" ("server_id" ASC);

CREATE TABLE IF NOT EXISTS "comment_media_items" ( "media_id" INTEGER NOT NULL,
     "comment_id" INTEGER NOT NULL,
     PRIMARY KEY ("media_id",
     "comment_id"),
     CONSTRAINT "fk_comment_data_has_media_media1" FOREIGN KEY ("media_id") REFERENCES "media" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_comment_data_media_comment1" FOREIGN KEY ("comment_id") REFERENCES "comment" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_comment_data_has_media_media1_idx" ON "comment_media_items" ("media_id" ASC);

CREATE INDEX "fk_comment_data_media_comment1_idx" ON "comment_media_items" ("comment_id" ASC);

CREATE TABLE IF NOT EXISTS "activity_media_items" ( "media_id" INTEGER NOT NULL,
     "activity_id" INTEGER NOT NULL,
     "featured" NUMERIC NOT NULL,
     PRIMARY KEY ("media_id",
     "activity_id"),
     CONSTRAINT "fk_activity_data_has_media_media1" FOREIGN KEY ("media_id") REFERENCES "media" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_activity_data_media_activity1" FOREIGN KEY ("activity_id") REFERENCES "activity" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_activity_data_has_media_media1_idx" ON "activity_media_items" ("media_id" ASC);

CREATE INDEX "fk_activity_data_media_activity1_idx" ON "activity_media_items" ("activity_id" ASC);

CREATE TABLE IF NOT EXISTS "history" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "user_id" INTEGER NOT NULL,
     "type" TEXT NOT NULL,
     "data" NONE,
     CONSTRAINT "fk_history_user1" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_history_user1_idx" ON "history" ("user_id" ASC);

CREATE TABLE IF NOT EXISTS "activity_categories" ( "activity_id" INTEGER NOT NULL,
     "category_id" INTEGER NOT NULL,
     PRIMARY KEY ("activity_id",
     "category_id"),
     CONSTRAINT "fk_activity_has_category_activity1" FOREIGN KEY ("activity_id") REFERENCES "activity" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_activity_has_category_category1" FOREIGN KEY ("category_id") REFERENCES "category" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_activity_has_category_category1_idx" ON "activity_categories" ("category_id" ASC);

CREATE INDEX "fk_activity_has_category_activity1_idx" ON "activity_categories" ("activity_id" ASC);

CREATE TABLE IF NOT EXISTS "activity_references" ( "activity_id" INTEGER NOT NULL,
     "reference_id" INTEGER NOT NULL,
     PRIMARY KEY ("activity_id",
     "reference_id"),
     CONSTRAINT "fk_activity_has_reference_activity1" FOREIGN KEY ("activity_id") REFERENCES "activity" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_activity_has_reference_reference1" FOREIGN KEY ("reference_id") REFERENCES "reference" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_activity_has_reference_reference1_idx" ON "activity_references" ("reference_id" ASC);

CREATE INDEX "fk_activity_has_reference_activity1_idx" ON "activity_references" ("activity_id" ASC);

