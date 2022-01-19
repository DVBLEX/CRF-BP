USE crf;

ALTER TABLE `crf`.`deposit_products` 
ADD COLUMN `premature_withdrawal_min_period_days` INT(11) NOT NULL COMMENT 'represents the amount of days after the deposit start date in which investors don\'t get any interest (only their initial investment), if they decide to withdraw' AFTER `amount_deposit_max`;

UPDATE crf.deposit_products SET premature_withdrawal_min_period_days = 90 WHERE id > 0;

ALTER TABLE `crf`.`deposit_products` 
ADD COLUMN `premature_withdrawal_rate_interest` DECIMAL(4,2) NOT NULL AFTER `premature_withdrawal_min_days`,
CHANGE COLUMN `premature_withdrawal_min_period_days` `premature_withdrawal_min_days` INT(11) NOT NULL COMMENT 'represents the amount of days after the deposit start date in which investors don\'t get any interest (only their initial investment), if they decide to withdraw' ;

UPDATE `crf`.`deposit_products` SET `premature_withdrawal_rate_interest` = '2.50' WHERE (`id` = '1');
UPDATE `crf`.`deposit_products` SET `premature_withdrawal_rate_interest` = '3.00' WHERE (`id` = '2');
UPDATE `crf`.`deposit_products` SET `premature_withdrawal_rate_interest` = '3.50' WHERE (`id` = '3');

ALTER TABLE `crf`.`deposit_accounts` 
ADD COLUMN `premature_withdrawal_min_days` INT(11) NOT NULL AFTER `bank_transfer_reference`,
ADD COLUMN `premature_withdrawal_rate_interest` DECIMAL(4,2) NOT NULL AFTER `premature_withdrawal_min_days`;

UPDATE crf.deposit_accounts SET premature_withdrawal_min_days = 90, premature_withdrawal_rate_interest = '2.50' WHERE deposit_product_id = 1 AND id > 0;
UPDATE crf.deposit_accounts SET premature_withdrawal_min_days = 90, premature_withdrawal_rate_interest = '3.00' WHERE deposit_product_id = 2 AND id > 0;
UPDATE crf.deposit_accounts SET premature_withdrawal_min_days = 90, premature_withdrawal_rate_interest = '3.50' WHERE deposit_product_id = 3 AND id > 0;

ALTER TABLE `crf`.`deposit_accounts` 
ADD COLUMN `date_withdraw_request` DATETIME NULL AFTER `date_maturity`,
CHANGE COLUMN `date_withdrawal` `date_withdraw_approve` DATETIME NULL DEFAULT NULL ;
