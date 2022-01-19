USE crf;

ALTER TABLE `crf`.`operator_activity_log` 
DROP COLUMN `ref_id`,
ADD COLUMN `json` VARCHAR(8192) NOT NULL AFTER `activity_name`,
CHANGE COLUMN `activity_id` `activity_name` VARCHAR(128) NOT NULL ,
CHANGE COLUMN `date_activity` `date_created` DATETIME NOT NULL ;

DELETE FROM crf.operator_activity_log WHERE id > 0;
ALTER TABLE crf.operator_activity_log AUTO_INCREMENT 1001;

ALTER TABLE `crf`.`operator_activity_log` 
ADD COLUMN `activity_id` INT(11) NOT NULL AFTER `operator_id`;

