-- MySQL Script generated by MySQL Workbench
-- 08/16/14 10:23:28
-- Model: Scout Application Server    Version: 1.0
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(100) NULL,
  `email_verified` TINYINT(1) NULL,
  `password_hash` BINARY NULL,
  `password_algorithm` CHAR(1) NULL,
  `display_name` VARCHAR(100) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`activity`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`activity` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `owner_id` INT NOT NULL,
  `status` CHAR(1) NOT NULL,
  `likes` INT NULL,
  `source_uri` VARCHAR(200) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_activity_user1`
    FOREIGN KEY (`owner_id`)
    REFERENCES `mydb`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_activity_user1_idx` ON `mydb`.`activity` (`owner_id` ASC);


-- -----------------------------------------------------
-- Table `mydb`.`activity_data`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`activity_data` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `activity_id` INT NOT NULL,
  `status` CHAR(1) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `datetime_published` DATETIME NULL,
  `datetime_created` DATETIME NOT NULL,
  `descr_material` TEXT NULL,
  `descr_introduction` TEXT NULL,
  `descr_prepare` TEXT NULL,
  `descr_activity` TEXT NOT NULL,
  `descr_safety` TEXT NULL,
  `descr_notes` TEXT NULL,
  `age_min` TINYINT NULL,
  `age_max` TINYINT NULL,
  `participants_min` TINYINT NULL,
  `participants_max` TINYINT NULL,
  `time_min` TINYINT NULL,
  `time_max` TINYINT NULL,
  `featured` TINYINT(1) NULL,
  `author_id` INT NULL,
  `source_uri` VARCHAR(200) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_activity_data_activity`
    FOREIGN KEY (`activity_id`)
    REFERENCES `mydb`.`activity` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_activity_data_user1`
    FOREIGN KEY (`author_id`)
    REFERENCES `mydb`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_activity_data_activity_idx` ON `mydb`.`activity_data` (`activity_id` ASC);

CREATE INDEX `fk_activity_data_user1_idx` ON `mydb`.`activity_data` (`author_id` ASC);

CREATE UNIQUE INDEX `name_UNIQUE` ON `mydb`.`activity_data` (`name` ASC);


-- -----------------------------------------------------
-- Table `mydb`.`comment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`comment` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `activity_id` INT NOT NULL,
  `status` CHAR(1) NOT NULL,
  `owner_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_comment_activity1`
    FOREIGN KEY (`activity_id`)
    REFERENCES `mydb`.`activity` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_user1`
    FOREIGN KEY (`owner_id`)
    REFERENCES `mydb`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_comment_activity1_idx` ON `mydb`.`comment` (`activity_id` ASC);

CREATE INDEX `fk_comment_user1_idx` ON `mydb`.`comment` (`owner_id` ASC);


-- -----------------------------------------------------
-- Table `mydb`.`category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`category` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `uuid` BINARY(16) NOT NULL,
  `group_name` VARCHAR(100) NULL,
  `name` VARCHAR(100) NOT NULL,
  `owner_id` INT NOT NULL,
  `status` CHAR(1) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_category_user1`
    FOREIGN KEY (`owner_id`)
    REFERENCES `mydb`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_category_user1_idx` ON `mydb`.`category` (`owner_id` ASC);

CREATE UNIQUE INDEX `uuid_UNIQUE` ON `mydb`.`category` (`uuid` ASC);

CREATE UNIQUE INDEX `idx_group_name` ON `mydb`.`category` (`group_name` ASC, `name` ASC);


-- -----------------------------------------------------
-- Table `mydb`.`comment_data`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`comment_data` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `datetime_created` DATETIME NOT NULL,
  `status` CHAR(1) NOT NULL,
  `text` VARCHAR(100) NULL,
  `source_uri` VARCHAR(200) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_comment_data_comment1`
    FOREIGN KEY (`id`)
    REFERENCES `mydb`.`comment` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_comment_data_comment1_idx` ON `mydb`.`comment_data` (`id` ASC);


-- -----------------------------------------------------
-- Table `mydb`.`media`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`media` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `data` BLOB NULL,
  `uri` VARCHAR(100) NULL,
  `status` CHAR(1) NOT NULL,
  `mime_type` VARCHAR(100) NOT NULL,
  `hash` BINARY(32) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`reference`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`reference` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `uri` VARCHAR(500) NOT NULL,
  `type` CHAR(1) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `idx_uri_type` ON `mydb`.`reference` (`uri` ASC, `type` ASC);


-- -----------------------------------------------------
-- Table `mydb`.`favourite_activity`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`favourite_activity` (
  `activity_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`activity_id`, `user_id`),
  CONSTRAINT `fk_activity_has_user_activity1`
    FOREIGN KEY (`activity_id`)
    REFERENCES `mydb`.`activity` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_activity_has_user_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `mydb`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_activity_has_user_user1_idx` ON `mydb`.`favourite_activity` (`user_id` ASC);

CREATE INDEX `fk_activity_has_user_activity1_idx` ON `mydb`.`favourite_activity` (`activity_id` ASC);


-- -----------------------------------------------------
-- Table `mydb`.`rating`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`rating` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `activity_id` INT NOT NULL,
  `datetime_created` DATETIME NOT NULL,
  `rating` TINYINT NOT NULL,
  `source_uri` VARCHAR(200) NULL,
  `user_id` INT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_rating_activity1`
    FOREIGN KEY (`activity_id`)
    REFERENCES `mydb`.`activity` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_rating_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `mydb`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_rating_activity1_idx` ON `mydb`.`rating` (`activity_id` ASC);

CREATE INDEX `fk_rating_user1_idx` ON `mydb`.`rating` (`user_id` ASC);

CREATE UNIQUE INDEX `user_id_UNIQUE` ON `mydb`.`rating` (`user_id` ASC);


-- -----------------------------------------------------
-- Table `mydb`.`comment_data_media`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`comment_data_media` (
  `comment_data_id` INT NOT NULL,
  `media_id` INT NOT NULL,
  PRIMARY KEY (`comment_data_id`, `media_id`),
  CONSTRAINT `fk_comment_data_has_media_comment_data1`
    FOREIGN KEY (`comment_data_id`)
    REFERENCES `mydb`.`comment_data` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_data_has_media_media1`
    FOREIGN KEY (`media_id`)
    REFERENCES `mydb`.`media` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_comment_data_has_media_media1_idx` ON `mydb`.`comment_data_media` (`media_id` ASC);

CREATE INDEX `fk_comment_data_has_media_comment_data1_idx` ON `mydb`.`comment_data_media` (`comment_data_id` ASC);


-- -----------------------------------------------------
-- Table `mydb`.`activity_data_media`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`activity_data_media` (
  `activity_data_id` INT NOT NULL,
  `media_id` INT NOT NULL,
  `featured` BIT NOT NULL,
  PRIMARY KEY (`activity_data_id`, `media_id`),
  CONSTRAINT `fk_activity_data_has_media_activity_data1`
    FOREIGN KEY (`activity_data_id`)
    REFERENCES `mydb`.`activity_data` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_activity_data_has_media_media1`
    FOREIGN KEY (`media_id`)
    REFERENCES `mydb`.`media` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_activity_data_has_media_media1_idx` ON `mydb`.`activity_data_media` (`media_id` ASC);

CREATE INDEX `fk_activity_data_has_media_activity_data1_idx` ON `mydb`.`activity_data_media` (`activity_data_id` ASC);


-- -----------------------------------------------------
-- Table `mydb`.`activity_data_reference`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`activity_data_reference` (
  `activity_data_id` INT NOT NULL,
  `reference_id` INT NOT NULL,
  PRIMARY KEY (`activity_data_id`, `reference_id`),
  CONSTRAINT `fk_activity_data_has_reference_activity_data1`
    FOREIGN KEY (`activity_data_id`)
    REFERENCES `mydb`.`activity_data` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_activity_data_has_reference_reference1`
    FOREIGN KEY (`reference_id`)
    REFERENCES `mydb`.`reference` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_activity_data_has_reference_reference1_idx` ON `mydb`.`activity_data_reference` (`reference_id` ASC);

CREATE INDEX `fk_activity_data_has_reference_activity_data1_idx` ON `mydb`.`activity_data_reference` (`activity_data_id` ASC);


-- -----------------------------------------------------
-- Table `mydb`.`activity_data_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`activity_data_category` (
  `activity_data_id` INT NOT NULL,
  `category_id` INT NOT NULL,
  PRIMARY KEY (`activity_data_id`, `category_id`),
  CONSTRAINT `fk_activity_data_has_category_activity_data1`
    FOREIGN KEY (`activity_data_id`)
    REFERENCES `mydb`.`activity_data` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_activity_data_has_category_category1`
    FOREIGN KEY (`category_id`)
    REFERENCES `mydb`.`category` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_activity_data_has_category_category1_idx` ON `mydb`.`activity_data_category` (`category_id` ASC);

CREATE INDEX `fk_activity_data_has_category_activity_data1_idx` ON `mydb`.`activity_data_category` (`activity_data_id` ASC);


-- -----------------------------------------------------
-- Table `mydb`.`history`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`history` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `type` CHAR(1) NOT NULL,
  `data` BLOB NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_history_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `mydb`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_history_user1_idx` ON `mydb`.`history` (`user_id` ASC);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
