USE crf;

ALTER TABLE `crf`.`customers`
    ADD COLUMN `is_aml_verified` TINYINT(1) NOT NULL AFTER `date_edited`;
