USE crf;

ALTER TABLE `crf`.`email_config` 
DROP COLUMN `smtp_socket_factory_fallback`,
DROP COLUMN `smtp_socket_factory_class`,
DROP COLUMN `smtp_socket_factory_port`;

ALTER TABLE `crf`.`email_config` 
CHANGE COLUMN `smtp_starttls_enable` `smtp_starttls_enable` VARCHAR(8) NOT NULL ;

ALTER TABLE `crf`.`email_config` 
ADD COLUMN `smtp_ssl_protocols` VARCHAR(128) NOT NULL AFTER `smtp_starttls_enable`;

ALTER TABLE `crf`.`email_config` 
CHANGE COLUMN `smtp_starttls_enable` `smtp_starttls_enable` VARCHAR(8) NULL ;

ALTER TABLE `crf`.`email_config` 
CHANGE COLUMN `smtp_ssl_protocols` `smtp_ssl_protocols` VARCHAR(128) NULL ;

ALTER TABLE `crf`.`email_templates` 
DROP COLUMN `user`;

DELETE FROM crf.email_config WHERE id >= -1;

INSERT INTO `crf`.`email_config` (`id`, `smtp_host`, `smtp_auth`, `smtp_port`, `smtp_starttls_enable`, `smtp_ssl_protocols`, `operator_id`, `date_created`, `date_edited`) VALUES ('-1', 'smtp.gmail.com', 'true', '587', 'true', 'TLSv1.2', '-1', '2020-01-20 09:28:19', '2020-01-20 09:28:19');
INSERT INTO `crf`.`email_config` (`id`, `smtp_host`, `smtp_auth`, `smtp_port`, `operator_id`, `date_created`, `date_edited`) VALUES ('1', 'mail.jmgcfinance.com', 'true', '587', '-1', '2019-05-31 09:29:00', '2019-05-31 09:29:00');
