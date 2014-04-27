-- Creator:       MySQL Workbench 6.1.4/ExportSQLite plugin 2009.12.02
-- Author:        Mikael
-- Caption:       Scout Application Server
-- Project:       Scout Application Server
-- Changed:       2014-04-27 08:14
-- Created:       2014-04-09 17:21
PRAGMA foreign_keys = OFF;

-- Schema: mydb
BEGIN;
CREATE TABLE "user"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "email" VARCHAR(100) NOT NULL,
  "email_verified" BIT,
  "password_hash" BINARY,
  "password_algorithm" CHAR(1),
  "display_name" VARCHAR(100)
);
CREATE TABLE "category"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "uuid" BINARY(16) NOT NULL,
  "group_name" VARCHAR(100),
  "name" VARCHAR(100) NOT NULL,
  "owner_id" INTEGER NOT NULL,
  "status" CHAR(1) NOT NULL,
  CONSTRAINT "uuid_UNIQUE"
    UNIQUE("uuid"),
  CONSTRAINT "idx_group_name"
    UNIQUE("group_name","name"),
  CONSTRAINT "fk_category_user1"
    FOREIGN KEY("owner_id")
    REFERENCES "user"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT
);
CREATE INDEX "category.fk_category_user1_idx" ON "category"("owner_id");
CREATE TABLE "media"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "data" BLOB,
  "uri" VARCHAR(100),
  "status" CHAR(1) NOT NULL,
  "mime_type" VARCHAR(100) NOT NULL,
  "hash" BINARY(32)
);
CREATE TABLE "reference"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "uri" VARCHAR(500) NOT NULL,
  "type" CHAR(1) NOT NULL,
  CONSTRAINT "idx_uri_type"
    UNIQUE("uri","type")
);
CREATE TABLE "activity"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "owner_id" INTEGER NOT NULL,
  "status" CHAR(1) NOT NULL,
  "likes" INTEGER,
  CONSTRAINT "fk_activity_user1"
    FOREIGN KEY("owner_id")
    REFERENCES "user"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT
);
CREATE INDEX "activity.fk_activity_user1_idx" ON "activity"("owner_id");
CREATE TABLE "activity_data"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "activity_id" INTEGER NOT NULL,
  "status" CHAR(1) NOT NULL,
  "name" VARCHAR(100) NOT NULL,
  "datetime_published" DATETIME,
  "datetime_created" DATETIME NOT NULL,
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
  "featured" BOOLEAN,
  "author_id" INTEGER,
  "source_uri" VARCHAR(200),
  CONSTRAINT "name_UNIQUE"
    UNIQUE("name"),
  CONSTRAINT "fk_activity_data_activity"
    FOREIGN KEY("activity_id")
    REFERENCES "activity"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT "fk_activity_data_user1"
    FOREIGN KEY("author_id")
    REFERENCES "user"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT
);
CREATE INDEX "activity_data.fk_activity_data_activity_idx" ON "activity_data"("activity_id");
CREATE INDEX "activity_data.fk_activity_data_user1_idx" ON "activity_data"("author_id");
CREATE TABLE "comment"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "activity_id" INTEGER NOT NULL,
  "status" CHAR(1) NOT NULL,
  "owner_id" INTEGER NOT NULL,
  CONSTRAINT "fk_comment_activity1"
    FOREIGN KEY("activity_id")
    REFERENCES "activity"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT "fk_comment_user1"
    FOREIGN KEY("owner_id")
    REFERENCES "user"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT
);
CREATE INDEX "comment.fk_comment_activity1_idx" ON "comment"("activity_id");
CREATE INDEX "comment.fk_comment_user1_idx" ON "comment"("owner_id");
CREATE TABLE "comment_data"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "datetime_created" DATETIME NOT NULL,
  "status" CHAR(1) NOT NULL,
  "text" VARCHAR(100),
  "source_uri" VARCHAR(200),
  CONSTRAINT "fk_comment_data_comment1"
    FOREIGN KEY("id")
    REFERENCES "comment"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT
);
CREATE INDEX "comment_data.fk_comment_data_comment1_idx" ON "comment_data"("id");
CREATE TABLE "favourite_activity"(
  "activity_id" INTEGER NOT NULL,
  "user_id" INTEGER NOT NULL,
  PRIMARY KEY("activity_id","user_id"),
  CONSTRAINT "fk_activity_has_user_activity1"
    FOREIGN KEY("activity_id")
    REFERENCES "activity"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT "fk_activity_has_user_user1"
    FOREIGN KEY("user_id")
    REFERENCES "user"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT
);
CREATE INDEX "favourite_activity.fk_activity_has_user_user1_idx" ON "favourite_activity"("user_id");
CREATE INDEX "favourite_activity.fk_activity_has_user_activity1_idx" ON "favourite_activity"("activity_id");
CREATE TABLE "rating"(
  "id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  "activity_id" INTEGER NOT NULL,
  "datetime_created" DATETIME NOT NULL,
  "rating" INTEGER NOT NULL,
  "source_uri" VARCHAR(200),
  "user_id" INTEGER,
  CONSTRAINT "user_id_UNIQUE"
    UNIQUE("user_id"),
  CONSTRAINT "fk_rating_activity1"
    FOREIGN KEY("activity_id")
    REFERENCES "activity"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT "fk_rating_user1"
    FOREIGN KEY("user_id")
    REFERENCES "user"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT
);
CREATE INDEX "rating.fk_rating_activity1_idx" ON "rating"("activity_id");
CREATE INDEX "rating.fk_rating_user1_idx" ON "rating"("user_id");
CREATE TABLE "comment_data_media"(
  "comment_data_id" INTEGER NOT NULL,
  "media_id" INTEGER NOT NULL,
  PRIMARY KEY("comment_data_id","media_id"),
  CONSTRAINT "fk_comment_data_has_media_comment_data1"
    FOREIGN KEY("comment_data_id")
    REFERENCES "comment_data"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT "fk_comment_data_has_media_media1"
    FOREIGN KEY("media_id")
    REFERENCES "media"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT
);
CREATE INDEX "comment_data_media.fk_comment_data_has_media_media1_idx" ON "comment_data_media"("media_id");
CREATE INDEX "comment_data_media.fk_comment_data_has_media_comment_data1_idx" ON "comment_data_media"("comment_data_id");
CREATE TABLE "activity_data_media"(
  "activity_data_id" INTEGER NOT NULL,
  "media_id" INTEGER NOT NULL,
  "featured" BIT NOT NULL,
  PRIMARY KEY("activity_data_id","media_id"),
  CONSTRAINT "fk_activity_data_has_media_activity_data1"
    FOREIGN KEY("activity_data_id")
    REFERENCES "activity_data"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT "fk_activity_data_has_media_media1"
    FOREIGN KEY("media_id")
    REFERENCES "media"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT
);
CREATE INDEX "activity_data_media.fk_activity_data_has_media_media1_idx" ON "activity_data_media"("media_id");
CREATE INDEX "activity_data_media.fk_activity_data_has_media_activity_data1_idx" ON "activity_data_media"("activity_data_id");
CREATE TABLE "activity_data_reference"(
  "activity_data_id" INTEGER NOT NULL,
  "reference_id" INTEGER NOT NULL,
  PRIMARY KEY("activity_data_id","reference_id"),
  CONSTRAINT "fk_activity_data_has_reference_activity_data1"
    FOREIGN KEY("activity_data_id")
    REFERENCES "activity_data"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT "fk_activity_data_has_reference_reference1"
    FOREIGN KEY("reference_id")
    REFERENCES "reference"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT
);
CREATE INDEX "activity_data_reference.fk_activity_data_has_reference_reference1_idx" ON "activity_data_reference"("reference_id");
CREATE INDEX "activity_data_reference.fk_activity_data_has_reference_activity_data1_idx" ON "activity_data_reference"("activity_data_id");
CREATE TABLE "activity_data_category"(
  "activity_data_id" INTEGER NOT NULL,
  "category_id" INTEGER NOT NULL,
  PRIMARY KEY("activity_data_id","category_id"),
  CONSTRAINT "fk_activity_data_has_category_activity_data1"
    FOREIGN KEY("activity_data_id")
    REFERENCES "activity_data"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT "fk_activity_data_has_category_category1"
    FOREIGN KEY("category_id")
    REFERENCES "category"("id")
    ON DELETE CASCADE
    ON UPDATE RESTRICT
);
CREATE INDEX "activity_data_category.fk_activity_data_has_category_category1_idx" ON "activity_data_category"("category_id");
CREATE INDEX "activity_data_category.fk_activity_data_has_category_activity_data1_idx" ON "activity_data_category"("activity_data_id");
COMMIT;
