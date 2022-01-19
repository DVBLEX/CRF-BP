ALTER TABLE `crf`.`customers` 
ADD COLUMN `national_id_number` VARCHAR(32) NOT NULL AFTER `msisdn`;
