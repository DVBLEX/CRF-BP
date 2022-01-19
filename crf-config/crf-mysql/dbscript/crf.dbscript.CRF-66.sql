USE crf;

ALTER TABLE `crf`.`customers` 
CHANGE COLUMN `is_aml_verified` `is_aml_verified` TINYINT(1) NOT NULL AFTER `is_bank_account_setup`;

ALTER TABLE `crf`.`customer_aml_responses` 
DROP INDEX `customer_id` ,
ADD UNIQUE INDEX `customer_id_uk` (`customer_id` ASC),
ADD INDEX `customer_id_ik` (`customer_id` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;

