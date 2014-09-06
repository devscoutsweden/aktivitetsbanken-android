CREATE TABLE IF NOT EXISTS "user" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "email" TEXT,
     "email_verified" INTEGER,
     "password_hash" NONE,
     "password_algorithm" TEXT,
     "display_name" TEXT);

CREATE TABLE IF NOT EXISTS "activity" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "owner_id" INTEGER NOT NULL,
     "status" TEXT NOT NULL,
     "likes" INTEGER,
     "source_uri" TEXT,
     CONSTRAINT "fk_activity_user1" FOREIGN KEY ("owner_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_activity_user1_idx" ON "activity" ("owner_id" ASC);

CREATE TABLE IF NOT EXISTS "activity_data" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "activity_id" INTEGER NOT NULL,
     "status" TEXT NOT NULL,
     "name" TEXT NOT NULL,
     "datetime_published" NUMERIC,
     "datetime_created" NUMERIC NOT NULL,
     "descr_material" NUMERIC,
     "descr_introduction" NUMERIC,
     "descr_prepare" NUMERIC,
     "descr_activity" NUMERIC NOT NULL,
     "descr_safety" NUMERIC,
     "descr_notes" NUMERIC,
     "age_min" INTEGER,
     "age_max" INTEGER,
     "participants_min" INTEGER,
     "participants_max" INTEGER,
     "time_min" INTEGER,
     "time_max" INTEGER,
     "featured" INTEGER,
     "author_id" INTEGER,
     "source_uri" TEXT,
     CONSTRAINT "fk_activity_data_activity" FOREIGN KEY ("activity_id") REFERENCES "activity" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_activity_data_user1" FOREIGN KEY ("author_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_activity_data_activity_idx" ON "activity_data" ("activity_id" ASC);

CREATE INDEX "fk_activity_data_user1_idx" ON "activity_data" ("author_id" ASC);

CREATE UNIQUE INDEX "name_UNIQUE" ON "activity_data" ("name" ASC);

CREATE TABLE IF NOT EXISTS "comment" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "activity_id" INTEGER NOT NULL,
     "status" TEXT NOT NULL,
     "owner_id" INTEGER NOT NULL,
     CONSTRAINT "fk_comment_activity1" FOREIGN KEY ("activity_id") REFERENCES "activity" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_comment_user1" FOREIGN KEY ("owner_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_comment_activity1_idx" ON "comment" ("activity_id" ASC);

CREATE INDEX "fk_comment_user1_idx" ON "comment" ("owner_id" ASC);

CREATE TABLE IF NOT EXISTS "category" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "uuid" NONE NOT NULL,
     "group_name" TEXT,
     "name" TEXT NOT NULL,
     "owner_id" INTEGER NOT NULL,
     "status" TEXT NOT NULL,
     CONSTRAINT "fk_category_user1" FOREIGN KEY ("owner_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_category_user1_idx" ON "category" ("owner_id" ASC);

CREATE UNIQUE INDEX "uuid_UNIQUE" ON "category" ("uuid" ASC);

CREATE UNIQUE INDEX "idx_group_name" ON "category" ("group_name" ASC,
     "name" ASC);

CREATE TABLE IF NOT EXISTS "comment_data" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "datetime_created" NUMERIC NOT NULL,
     "status" TEXT NOT NULL,
     "text" TEXT,
     "source_uri" TEXT,
     CONSTRAINT "fk_comment_data_comment1" FOREIGN KEY ("id") REFERENCES "comment" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_comment_data_comment1_idx" ON "comment_data" ("id" ASC);

CREATE TABLE IF NOT EXISTS "media" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "data" NONE,
     "uri" TEXT,
     "status" TEXT NOT NULL,
     "mime_type" TEXT NOT NULL,
     "hash" NONE);

CREATE TABLE IF NOT EXISTS "reference" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "uri" TEXT NOT NULL,
     "type" TEXT NOT NULL);

CREATE UNIQUE INDEX "idx_uri_type" ON "reference" ("uri" ASC,
     "type" ASC);

CREATE TABLE IF NOT EXISTS "favourite_activity" ( "activity_id" INTEGER NOT NULL,
     "user_id" INTEGER NOT NULL,
     PRIMARY KEY ("activity_id",
     "user_id"),
     CONSTRAINT "fk_activity_has_user_activity1" FOREIGN KEY ("activity_id") REFERENCES "activity" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_activity_has_user_user1" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_activity_has_user_user1_idx" ON "favourite_activity" ("user_id" ASC);

CREATE INDEX "fk_activity_has_user_activity1_idx" ON "favourite_activity" ("activity_id" ASC);

CREATE TABLE IF NOT EXISTS "rating" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "activity_id" INTEGER NOT NULL,
     "datetime_created" NUMERIC NOT NULL,
     "rating" INTEGER NOT NULL,
     "source_uri" TEXT,
     "user_id" INTEGER,
     CONSTRAINT "fk_rating_activity1" FOREIGN KEY ("activity_id") REFERENCES "activity" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_rating_user1" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_rating_activity1_idx" ON "rating" ("activity_id" ASC);

CREATE INDEX "fk_rating_user1_idx" ON "rating" ("user_id" ASC);

CREATE UNIQUE INDEX "user_id_UNIQUE" ON "rating" ("user_id" ASC);

CREATE TABLE IF NOT EXISTS "comment_data_media" ( "comment_data_id" INTEGER NOT NULL,
     "media_id" INTEGER NOT NULL,
     PRIMARY KEY ("comment_data_id",
     "media_id"),
     CONSTRAINT "fk_comment_data_has_media_comment_data1" FOREIGN KEY ("comment_data_id") REFERENCES "comment_data" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_comment_data_has_media_media1" FOREIGN KEY ("media_id") REFERENCES "media" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_comment_data_has_media_media1_idx" ON "comment_data_media" ("media_id" ASC);

CREATE INDEX "fk_comment_data_has_media_comment_data1_idx" ON "comment_data_media" ("comment_data_id" ASC);

CREATE TABLE IF NOT EXISTS "activity_data_media" ( "activity_data_id" INTEGER NOT NULL,
     "media_id" INTEGER NOT NULL,
     "featured" NUMERIC NOT NULL,
     PRIMARY KEY ("activity_data_id",
     "media_id"),
     CONSTRAINT "fk_activity_data_has_media_activity_data1" FOREIGN KEY ("activity_data_id") REFERENCES "activity_data" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_activity_data_has_media_media1" FOREIGN KEY ("media_id") REFERENCES "media" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_activity_data_has_media_media1_idx" ON "activity_data_media" ("media_id" ASC);

CREATE INDEX "fk_activity_data_has_media_activity_data1_idx" ON "activity_data_media" ("activity_data_id" ASC);

CREATE TABLE IF NOT EXISTS "activity_data_reference" ( "activity_data_id" INTEGER NOT NULL,
     "reference_id" INTEGER NOT NULL,
     PRIMARY KEY ("activity_data_id",
     "reference_id"),
     CONSTRAINT "fk_activity_data_has_reference_activity_data1" FOREIGN KEY ("activity_data_id") REFERENCES "activity_data" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_activity_data_has_reference_reference1" FOREIGN KEY ("reference_id") REFERENCES "reference" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_activity_data_has_reference_reference1_idx" ON "activity_data_reference" ("reference_id" ASC);

CREATE INDEX "fk_activity_data_has_reference_activity_data1_idx" ON "activity_data_reference" ("activity_data_id" ASC);

CREATE TABLE IF NOT EXISTS "activity_data_category" ( "activity_data_id" INTEGER NOT NULL,
     "category_id" INTEGER NOT NULL,
     PRIMARY KEY ("activity_data_id",
     "category_id"),
     CONSTRAINT "fk_activity_data_has_category_activity_data1" FOREIGN KEY ("activity_data_id") REFERENCES "activity_data" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
     CONSTRAINT "fk_activity_data_has_category_category1" FOREIGN KEY ("category_id") REFERENCES "category" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_activity_data_has_category_category1_idx" ON "activity_data_category" ("category_id" ASC);

CREATE INDEX "fk_activity_data_has_category_activity_data1_idx" ON "activity_data_category" ("activity_data_id" ASC);

CREATE TABLE IF NOT EXISTS "history" ( "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
     "user_id" INTEGER NOT NULL,
     "type" TEXT NOT NULL,
     "data" NONE,
     CONSTRAINT "fk_history_user1" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_history_user1_idx" ON "history" ("user_id" ASC);

