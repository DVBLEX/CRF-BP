USE crf;

ALTER TABLE `crf`.`deposit_accounts` 
ADD COLUMN `fee_withdrawal` DECIMAL(10,2) NOT NULL AFTER `premature_withdrawal_rate_interest`;
