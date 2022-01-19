USE crf;

ALTER TABLE `crf`.`verification_denial_reasons`
    ADD COLUMN `is_poid_related` TINYINT(1) NOT NULL AFTER `question`,
    ADD COLUMN `is_poa_related` TINYINT(1) NOT NULL AFTER `is_poid_related`,
    ADD COLUMN `is_photo_related` TINYINT(1) NOT NULL AFTER `is_poa_related`;

UPDATE `crf`.`verification_denial_reasons` SET `description` = 'Blurred Document Photo', `question` = 'Is document photo blurred?', `is_poid_related` = '1', `is_poa_related` = '1' WHERE (`id` = '2');
DELETE FROM `crf`.`verification_denial_reasons` WHERE (`id` = '3');
UPDATE `crf`.`verification_denial_reasons` SET `id` = '3', `description` = 'Document Expired', `question` = 'Is document expired?', `is_poid_related` = '1', `is_poa_related` = '1' WHERE (`id` = '4');
DELETE FROM `crf`.`verification_denial_reasons` WHERE (`id` = '5');
UPDATE `crf`.`verification_denial_reasons` SET `id` = '4', `description` = 'Document Number Mismatch', `question` = 'Is document number incorrect?', `is_poid_related` = '1' WHERE (`id` = '6');
DELETE FROM `crf`.`verification_denial_reasons` WHERE (`id` = '7');
UPDATE `crf`.`verification_denial_reasons` SET `id` = '5', `description` = 'Personal & Document Photo Mismatch', `question` = 'Is personal & document photo mismatch?', `is_poid_related` = '1' WHERE (`id` = '8');
DELETE FROM `crf`.`verification_denial_reasons` WHERE (`id` = '9');
UPDATE `crf`.`verification_denial_reasons` SET `id` = '6', `is_poid_related` = '1' WHERE (`id` = '10');
UPDATE `crf`.`verification_denial_reasons` SET `is_poid_related` = '1', `is_poa_related` = '1', `is_photo_related` = '1' WHERE (`id` = '100');
UPDATE `crf`.`verification_denial_reasons` SET `is_photo_related` = '1' WHERE (`id` = '1');

ALTER TABLE `crf`.`files`
    ADD COLUMN `entity_type` INT NOT NULL AFTER `role`,
    ADD COLUMN `entity_number` INT NOT NULL AFTER `entity_type`;
