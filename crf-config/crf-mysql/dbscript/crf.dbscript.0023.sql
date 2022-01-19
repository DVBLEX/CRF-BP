USE crf;

ALTER TABLE `crf`.`files`
    ADD COLUMN `mime_type` VARCHAR(127) NOT NULL AFTER `type`;

UPDATE `crf`.`files` f
SET f.mime_type = 'image/jpeg'
WHERE f.`type` = 'JPG'
   OR f.`type` = 'JPEG';

UPDATE `crf`.`files` f
SET f.mime_type = 'image/png'
WHERE f.`type` = 'PNG';

UPDATE `crf`.`files` f
SET f.mime_type = 'application/pdf'
WHERE f.`type` = 'PDF';
