USE crf;

TRUNCATE `crf`.`verification_denial_reasons`;

INSERT INTO `crf`.`verification_denial_reasons` (`id`,`description`,`question`,`is_poid_related`,`is_poa_related`,`is_photo_related`,`date_created`) VALUES (1,'Blurred Personal Photo','Is personal photo blurred?',0,0,1,'2017-10-02 15:39:10');
INSERT INTO `crf`.`verification_denial_reasons` (`id`,`description`,`question`,`is_poid_related`,`is_poa_related`,`is_photo_related`,`date_created`) VALUES (2,'Blurred POID Photo(s)','Is POID document photo(s) blurred?',1,0,0,'2017-10-02 15:39:10');
INSERT INTO `crf`.`verification_denial_reasons` (`id`,`description`,`question`,`is_poid_related`,`is_poa_related`,`is_photo_related`,`date_created`) VALUES (3,'Blurred POA Photo(s)','Is POA document photo(s) blurred?',0,1,0,'2017-10-02 15:39:10');
INSERT INTO `crf`.`verification_denial_reasons` (`id`,`description`,`question`,`is_poid_related`,`is_poa_related`,`is_photo_related`,`date_created`) VALUES (4,'POID Document(s) Expired','Is POID document(s) expired?',1,0,0,'2017-10-02 15:39:10');
INSERT INTO `crf`.`verification_denial_reasons` (`id`,`description`,`question`,`is_poid_related`,`is_poa_related`,`is_photo_related`,`date_created`) VALUES (5,'POID Document(s) Number Mismatch','Is POID document(s) number incorrect?',1,0,0,'2017-10-02 15:39:10');
INSERT INTO `crf`.`verification_denial_reasons` (`id`,`description`,`question`,`is_poid_related`,`is_poa_related`,`is_photo_related`,`date_created`) VALUES (6,'Personal & Document(s) Photo Mismatch','Is personal & document(s) photo mismatch?',0,0,1,'2017-10-02 15:39:10');
INSERT INTO `crf`.`verification_denial_reasons` (`id`,`description`,`question`,`is_poid_related`,`is_poa_related`,`is_photo_related`,`date_created`) VALUES (7,'Date of Birth Mismatch','Is date of birth incorrect?',1,0,0,'2017-10-02 15:39:10');
INSERT INTO `crf`.`verification_denial_reasons` (`id`,`description`,`question`,`is_poid_related`,`is_poa_related`,`is_photo_related`,`date_created`) VALUES (100,'Other','Is there any other reason?',1,1,1,'2017-10-02 15:39:10');
