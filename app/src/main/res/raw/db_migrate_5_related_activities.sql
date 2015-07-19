CREATE TABLE "activity_relations" (
  "activity_id"         INTEGER NOT NULL,
  "related_activity_id" INTEGER NOT NULL,
  CONSTRAINT "pk_activity_relations" PRIMARY KEY ("activity_id", "related_activity_id")
    ON CONFLICT FAIL,
  CONSTRAINT "fk_activity_relations_activity" FOREIGN KEY ("activity_id") REFERENCES "activity" ("id") ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT "fk_activity_relations_relatedactivity" FOREIGN KEY ("related_activity_id") REFERENCES "activity" ("id") ON DELETE CASCADE ON UPDATE RESTRICT);
