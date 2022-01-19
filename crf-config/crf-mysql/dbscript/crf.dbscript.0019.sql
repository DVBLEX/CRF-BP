USE crf;

UPDATE `crf`.`verification_denial_reasons` SET `id` = '6', `description` = 'Personal & Document(s) Photo Mismatch', `question` = 'Is personal & document(s) photo mismatch?', `is_poid_related` = '0', `is_photo_related` = '1' WHERE (`id` = '6');
UPDATE `crf`.`verification_denial_reasons` SET `id` = '5', `description` = 'POID Document(s) Number Mismatch', `question` = 'Is POID document(s) number incorrect?' WHERE (`id` = '5');
UPDATE `crf`.`verification_denial_reasons` SET `id` = '3', `description` = 'Blurred POA Photo(s)', `question` = 'Is POA document photo(s) blurred?', `is_poid_related` = '0', `is_poa_related` = '1' WHERE (`id` = '3');
UPDATE `crf`.`verification_denial_reasons` SET `id` = '4', `description` = 'POID Document(s) Expired', `question` = 'Is POID document(s) expired?' WHERE (`id` = '4');
UPDATE `crf`.`verification_denial_reasons` SET `description` = 'Blurred POID Photo(s)', `question` = 'Is POID document photo(s) blurred?', `is_poa_related` = '0' WHERE (`id` = '2');
UPDATE `crf`.`verification_denial_reasons` SET `id` = '7', `description` = 'Date of Birth Mismatch', `question` = 'Is date of birth incorrect?', `is_poa_related` = '0', `is_photo_related` = '0' WHERE (`id` = '100');
INSERT INTO `crf`.`verification_denial_reasons` (`id`, `description`, `question`, `is_poid_related`, `is_poa_related`, `is_photo_related`, `date_created`) VALUES ('100', 'Other', 'Is there any other reason?', '1', '1', '1', '2017-10-02 15:39:10');
