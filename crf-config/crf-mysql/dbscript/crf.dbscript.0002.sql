USE crf;

ALTER TABLE `crf`.`files` 
ADD COLUMN `customer_id` INT(11) NOT NULL AFTER `role`;

DROP TABLE crf.customers_files_map;
