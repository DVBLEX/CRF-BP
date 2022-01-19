USE crf;

CREATE TABLE crf.`bank_accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(64) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `bank_name` varchar(64) NOT NULL,
  `bank_account_name` varchar(64) NOT NULL,
  `bank_address` varchar(64) NOT NULL,
  `iban` varchar(34) NOT NULL,
  `bic` varchar(16) NOT NULL,
  `date_created` datetime NOT NULL,
  `date_edited` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8;

ALTER TABLE `crf`.`bank_accounts` 
CHANGE COLUMN `bank_address` `bank_address` VARCHAR(256) NOT NULL ;

ALTER TABLE `crf`.`customers` 
ADD COLUMN `is_bank_account_setup` TINYINT(1) NOT NULL AFTER `date_photo_uploaded`;

