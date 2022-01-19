USE crf;

INSERT INTO `crf`.`email_templates` (`id`, `type`, `name`, `config_id`, `user`, `email_from`, `email_from_password`, `email_bcc`, `subject`, `template`, `message`, `variables`, `priority`, `operator_id`, `date_created`, `date_edited`) VALUES ('13', '13', 'Customer Support Query', '1', '', 'no-reply@jmgcfinance.com', 'Xk8J)x7%Kk', ' ', 'JMGCFinance - ${queryType}', '${templateBody}', '${queryDetails}\n\n', ' ', '15', '-1', '2020-01-20 12:38:00', '2020-01-20 12:38:00');

ALTER TABLE `crf`.`system_parameters` 
ADD COLUMN `contact_email` VARCHAR(64) NOT NULL AFTER `errors_to_email`;

UPDATE `crf`.`system_parameters` SET `contact_email` = 'contact@jmgcfinance.com' WHERE (`id` = '1');

ALTER TABLE `crf`.`email_log` 
ADD COLUMN `email_reply_to` VARCHAR(256) NULL AFTER `email_to`;

ALTER TABLE `crf`.`email_scheduler` 
ADD COLUMN `email_reply_to` VARCHAR(256) NULL DEFAULT NULL AFTER `email_to`;
