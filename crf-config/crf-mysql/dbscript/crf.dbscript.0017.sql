USE crf;

ALTER TABLE `crf`.`customers`
    ADD COLUMN `kyc_option` INT NOT NULL AFTER `post_code`,
    ADD COLUMN `id_2_type` INT NOT NULL AFTER `id_1_number`,
    ADD COLUMN `id_2_number` VARCHAR(64) NOT NULL AFTER `id_2_type`,
    ADD COLUMN `date_id_2_expiry` DATETIME NULL DEFAULT NULL AFTER `date_id_1_expiry`,
    ADD COLUMN `poa_1_type` INT NOT NULL AFTER `date_id_2_expiry`,
    ADD COLUMN `poa_2_type` INT NOT NULL AFTER `poa_1_type`,
    CHANGE COLUMN `id_type` `id_1_type` INT NOT NULL ,
    CHANGE COLUMN `id_number` `id_1_number` VARCHAR(64) NOT NULL ,
    CHANGE COLUMN `date_id_expiry` `date_id_1_expiry` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `crf`.`deposit_products` 
CHANGE COLUMN `rate_interest` `yearly_rate_interest` DECIMAL(4,2) NOT NULL ;

ALTER TABLE `crf`.`deposit_products` 
ADD COLUMN `quarterly_rate_interest` DECIMAL(4,2) NOT NULL AFTER `yearly_rate_interest`;

UPDATE crf.deposit_products SET quarterly_rate_interest = yearly_rate_interest - 0.5 WHERE id > 0;

ALTER TABLE `crf`.`deposit_accounts` 
ADD COLUMN `interest_payout_frequency` INT(11) NOT NULL AFTER `amount_deposit`;

UPDATE crf.deposit_accounts SET interest_payout_frequency = 2 WHERE id > 0;

CREATE TABLE crf.`deposit_account_payments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) NOT NULL,
  `deposit_account_id` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `operator_id` int(11) NOT NULL,
  `date_processed` DATETIME NOT NULL,
  `date_created` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_uk` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8;

ALTER TABLE `crf`.`deposit_account_payments` 
CHANGE COLUMN `date_processed` `date_processed` DATETIME NULL ;

ALTER TABLE `crf`.`deposit_accounts` 
ADD COLUMN `date_last_interest_payment` DATETIME NULL AFTER `date_maturity`;

UPDATE crf.deposit_products SET premature_withdrawal_min_days = 60 WHERE id > 0;
UPDATE crf.deposit_accounts SET premature_withdrawal_min_days = 60 WHERE id > 0;

ALTER TABLE `crf`.`deposit_products` 
ADD COLUMN `twice_yearly_rate_interest` DECIMAL(4,2) NOT NULL AFTER `yearly_rate_interest`;

UPDATE crf.deposit_products SET twice_yearly_rate_interest = yearly_rate_interest WHERE id > 0;
