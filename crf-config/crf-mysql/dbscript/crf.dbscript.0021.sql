USE crf;

UPDATE `crf`.`verification_denial_reasons` SET `description` = 'POID Other Denial Reason', `is_poa_related` = '0', `is_photo_related` = '0' WHERE (`id` = '100');
INSERT INTO `crf`.`verification_denial_reasons` (`id`, `description`, `question`, `is_poid_related`, `is_poa_related`, `is_photo_related`, `date_created`) VALUES ('101', 'POA Other Denial Reason', 'Is there any other reason?', '0', '1', '0', '2017-10-02 15:39:10');
INSERT INTO `crf`.`verification_denial_reasons` (`id`, `description`, `question`, `is_poid_related`, `is_poa_related`, `is_photo_related`, `date_created`) VALUES ('102', 'Personal Photo Other Denial Reason', 'Is there any other reason?', '0', '0', '1', '2017-10-02 15:39:10');
