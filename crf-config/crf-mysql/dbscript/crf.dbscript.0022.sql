USE crf;

ALTER TABLE `crf`.`deposit_account_payments` 
ADD COLUMN `is_processed` TINYINT(1) NOT NULL AFTER `operator_id`;

ALTER TABLE `crf`.`deposit_account_payments` 
ADD COLUMN `account_number` VARCHAR(8) NOT NULL AFTER `deposit_account_id`,
ADD COLUMN `interest_payout_frequency` INT(11) NOT NULL AFTER `account_number`,
ADD COLUMN `customer_name` VARCHAR(128) NOT NULL AFTER `customer_id`;
