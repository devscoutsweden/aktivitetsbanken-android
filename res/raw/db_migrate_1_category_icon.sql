ALTER TABLE "category"
 ADD COLUMN "icon_media_id" INTEGER
 CONSTRAINT "fk_categories_media1" REFERENCES "media" ("id") ON DELETE SET NULL ON UPDATE RESTRICT;

CREATE INDEX "fk_categories_media1_idx" ON "category" ("icon_media_id" ASC);
