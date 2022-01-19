USE crf;

ALTER TABLE `crf`.`customer_aml_responses`
    ADD COLUMN `number_of_matches` int(11) NOT NULL AFTER `aml_scan_response`;