USE crf;

ALTER TABLE `crf`.`accounts` 
DROP COLUMN `type`,
ADD COLUMN `deposit_product_id` INT(11) NOT NULL AFTER `customer_id`,
ADD COLUMN `amount_deposit` DECIMAL(10,2) NOT NULL AFTER `deposit_product_id`,
ADD COLUMN `interest_rate` DECIMAL(4,2) NOT NULL AFTER `amount_deposit`,
ADD COLUMN `term_deposit_years` DECIMAL(2) NOT NULL AFTER `interest_rate`,
DROP INDEX `type_ik` ;


ALTER TABLE `crf`.`accounts` 
RENAME TO  `crf`.`deposit_accounts` ;

ALTER TABLE `crf`.`deposit_accounts` 
CHANGE COLUMN `interest_rate` `rate_interest` DECIMAL(4,2) NOT NULL ,
CHANGE COLUMN `term_deposit_years` `term_years` DECIMAL(2,0) NOT NULL ;

ALTER TABLE `crf`.`deposit_accounts` 
ADD COLUMN `status` INT(11) NOT NULL AFTER `term_years`,
ADD COLUMN `bank_transfer_reference` VARCHAR(16) NOT NULL AFTER `status`,
ADD COLUMN `date_open` DATETIME NOT NULL AFTER `bank_transfer_reference`,
ADD COLUMN `date_start` DATETIME NULL AFTER `date_open`,
ADD COLUMN `date_maturity` DATETIME NULL AFTER `date_start`;

ALTER TABLE `crf`.`deposit_accounts` 
ADD COLUMN `date_withdrawal` DATETIME NULL AFTER `date_maturity`;

CREATE TABLE crf.`deposit_products` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) NOT NULL,
  `name` varchar(32) NOT NULL,
  `description` varchar(256) NOT NULL,
  `rate_interest` decimal(4,2) NOT NULL,
  `term_years` decimal(2,0) NOT NULL,
  `amount_deposit_min` decimal(10,2) NOT NULL,
  `amount_deposit_max` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_uk` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `crf`.`deposit_products` (`id`, `code`, `name`, `description`, `rate_interest`, `term_years`, `amount_deposit_min`, `amount_deposit_max`) VALUES ('1', 'e315331b75faafb4399c043ff1b6887fc6ccc82175e050ce5b58916112c9ce21', 'Deposit Product 1', 'Here is the description of the product...', '5', '1', '100000', '1000000');
INSERT INTO `crf`.`deposit_products` (`id`, `code`, `name`, `description`, `rate_interest`, `term_years`, `amount_deposit_min`, `amount_deposit_max`) VALUES ('2', '0b5643532998638a67e521b95890561efd672d3ba633e415b0e9a4b5e832af18', 'Deposit Product 2', 'Here is the description of the product...', '6.00', '2', '100000.00', '1000000.00');
INSERT INTO `crf`.`deposit_products` (`id`, `code`, `name`, `description`, `rate_interest`, `term_years`, `amount_deposit_min`, `amount_deposit_max`) VALUES ('3', '9s5643532998638a67e521b95890561efd672d3ba633e415b0e9a4b5e832af92', 'Deposit Product 3', 'Here is the description of the product...', '7.00', '5', '100000.00', '1000000.00');


