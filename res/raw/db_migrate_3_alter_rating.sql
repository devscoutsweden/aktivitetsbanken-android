-- The existing rating table was never used prior to this SQL script being developed, and therefore we can drop it without first backing it up.
DROP TABLE "rating";

CREATE TABLE "rating" (
--   "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "activity_id" INTEGER NOT NULL,
  "user_id"     INTEGER NOT NULL,
  "status"      INTEGER NOT NULL,
  "rating"      INTEGER NOT NULL,
  CONSTRAINT "pk_rating" PRIMARY KEY ("activity_id", "user_id") ON CONFLICT FAIL,
  CONSTRAINT "fk_rating_activity1" FOREIGN KEY ("activity_id") REFERENCES "activity" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT "fk_rating_user1" FOREIGN KEY ("user_id") REFERENCES "user" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);

CREATE INDEX "fk_rating_activity1_idx" ON "rating" ("activity_id" ASC);

CREATE INDEX "fk_rating_user1_idx" ON "rating" ("user_id" ASC);
