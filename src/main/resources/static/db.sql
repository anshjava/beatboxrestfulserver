CREATE TABLE `kamuzta`.`beatbox` (
                                     `id` INT NOT NULL AUTO_INCREMENT,
                                     `time` DATETIME NULL,
                                     `name` VARCHAR(70) NULL,
                                     `message` VARCHAR(70) NULL,
                                     `melody` VARCHAR(1550) NULL,
                                     PRIMARY KEY (`id`),
                                     UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8
    COLLATE = utf8_unicode_ci;