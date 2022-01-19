USE crf;

ALTER TABLE `crf`.`bank_accounts`
ADD INDEX `customer_id_ik` (`customer_id` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;

ALTER TABLE `crf`.`blocked_email_domains`
ADD INDEX `name_ik` (`name` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;

ALTER TABLE `crf`.`customers`
ADD INDEX `type_ik` (`type` ASC),
ADD INDEX `category_ik` (`category` ASC),
ADD INDEX `email_ik` (`email` ASC),
ADD INDEX `msisdn_ik` (`msisdn` ASC),
ADD INDEX `kyc_option_ik` (`kyc_option` ASC),
ADD INDEX `is_passport_scan_uploaded_ik` (`is_passport_scan_uploaded` ASC),
ADD INDEX `is_passport_scan_verified_ik` (`is_passport_scan_verified` ASC),
ADD INDEX `is_passport_scan_denied_ik` (`is_passport_scan_denied` ASC),
ADD INDEX `is_photo_uploaded_ik` (`is_photo_uploaded` ASC),
ADD INDEX `is_deleted_ik` (`is_deleted` ASC),
ADD INDEX `date_deleted_ik` (`date_deleted` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;

ALTER TABLE `crf`.`customer_verification_denials`
ADD INDEX `customer_id_ik` (`customer_id` ASC),
ADD INDEX `reason_id_ik` (`reason_id` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;

ALTER TABLE `crf`.`deposit_account_documents`
DROP INDEX `code_ik` ,
ADD UNIQUE INDEX `code_uk` (`code` ASC),
ADD INDEX `type_ik` (`type` ASC),
ADD INDEX `account_number_ik` (`account_number` ASC);
;

ALTER TABLE `crf`.`deposit_account_payments`
ADD INDEX `deposit_account_id_ik` (`deposit_account_id` ASC),
ADD INDEX `account_number_ik` (`account_number` ASC),
ADD INDEX `customer_id_ik` (`customer_id` ASC),
ADD INDEX `amount_ik` (`amount` ASC),
ADD INDEX `operator_id_ik` (`operator_id` ASC),
ADD INDEX `is_processed_ik` (`is_processed` ASC),
ADD INDEX `date_processed_ik` (`date_processed` ASC),
ADD INDEX `date_period_from_ik` (`date_period_from` ASC),
ADD INDEX `date_period_to_ik` (`date_period_to` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;

ALTER TABLE `crf`.`deposit_accounts`
ADD INDEX `deposit_product_id_ik` (`deposit_product_id` ASC),
ADD INDEX `account_number_ik` (`account_number` ASC),
ADD INDEX `amount_deposit_ik` (`amount_deposit` ASC),
ADD INDEX `status_ik` (`status` ASC),
ADD INDEX `fee_withdrawal_ik` (`fee_withdrawal` ASC),
ADD INDEX `amount_deposit_withdrawal_ik` (`amount_deposit_withdrawal` ASC),
ADD INDEX `amount_interest_earned_ik` (`amount_interest_earned` ASC),
ADD INDEX `date_open_ik` (`date_open` ASC),
ADD INDEX `date_start_ik` (`date_start` ASC),
ADD INDEX `date_maturity_ik` (`date_maturity` ASC),
ADD INDEX `date_last_interest_payment_ik` (`date_last_interest_payment` ASC),
ADD INDEX `date_withdraw_request_ik` (`date_withdraw_request` ASC),
ADD INDEX `date_withdraw_approve_ik` (`date_withdraw_approve` ASC);
;

ALTER TABLE `crf`.`email_code_requests`
ADD INDEX `date_verified_ik` (`date_verified` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;

ALTER TABLE `crf`.`email_config`
ADD INDEX `operator_id_ik` (`operator_id` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;

ALTER TABLE `crf`.`email_log`
DROP INDEX `account_id_ik` ,
ADD INDEX `customer_id_ik` (`customer_id` ASC),
ADD INDEX `retry_count_ik` (`retry_count` ASC),
ADD INDEX `config_id_ik` (`config_id` ASC);
;

ALTER TABLE `crf`.`email_scheduler`
DROP INDEX `account_id_ik` ,
ADD INDEX `customer_id_ik` (`customer_id` ASC),
ADD INDEX `retry_count_ik` (`retry_count` ASC),
ADD INDEX `date_processed_ik` (`date_processed` ASC),
ADD INDEX `config_id_ik` (`config_id` ASC);
;

ALTER TABLE `crf`.`email_templates`
ADD INDEX `config_id_ik` (`config_id` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;

ALTER TABLE `crf`.`email_whitelist`
ADD INDEX `date_created_ik` (`date_created` ASC);
;

ALTER TABLE `crf`.`files`
ADD UNIQUE INDEX `code_uk` (`code` ASC),
ADD INDEX `type_ik` (`type` ASC),
ADD INDEX `mime_type_ik` (`mime_type` ASC),
ADD INDEX `role_ik` (`role` ASC),
ADD INDEX `entity_type_ik` (`entity_type` ASC),
ADD INDEX `customer_id_ik` (`customer_id` ASC),
ADD INDEX `operator_id_ik` (`operator_id` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;

ALTER TABLE `crf`.`mobile_upload_requests`
ADD INDEX `customer_id_ik` (`customer_id` ASC),
ADD INDEX `token1_ik` (`token1` ASC),
ADD INDEX `token2_ik` (`token2` ASC),
ADD INDEX `is_valid_ik` (`is_valid` ASC),
ADD INDEX `is_completed_ik` (`is_completed` ASC),
ADD INDEX `date_completed_ik` (`date_completed` ASC),
ADD INDEX `date_last_request_ik` (`date_last_request` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;

ALTER TABLE `crf`.`operator_activity_log`
ADD INDEX `operator_id_ik` (`operator_id` ASC),
ADD INDEX `activity_id_ik` (`activity_id` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;

ALTER TABLE `crf`.`operators`
ADD INDEX `is_credentials_expired_ik` (`is_credentials_expired` ASC);
;

ALTER TABLE `crf`.`registration_requests`
ADD INDEX `type_ik` (`type` ASC),
ADD INDEX `email_ik` (`email` ASC),
ADD INDEX `token1_ik` (`token1` ASC),
ADD INDEX `token2_ik` (`token2` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC),
ADD INDEX `category_ik` (`category` ASC);
;

ALTER TABLE `crf`.`sms_code_requests`
ADD INDEX `token_ik` (`token` ASC);
;

ALTER TABLE `crf`.`sms_config`
ADD INDEX `operator_id_ik` (`operator_id` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;

ALTER TABLE `crf`.`sms_log`
DROP INDEX `account_id_ik` ,
ADD INDEX `customer_id_ik` (`customer_id` ASC),
ADD INDEX `is_processed_ik` (`is_processed` ASC),
ADD INDEX `type_ik` (`type` ASC),
ADD INDEX `config_id_ik` (`config_id` ASC);
;

ALTER TABLE `crf`.`sms_scheduler`
DROP INDEX `account_id_ik` ,
ADD INDEX `customer_id_ik` (`customer_id` ASC),
ADD INDEX `type_ik` (`type` ASC),
ADD INDEX `config_id_ik` (`config_id` ASC);
;

ALTER TABLE `crf`.`sms_templates`
ADD INDEX `type_ik` (`type` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;

ALTER TABLE `crf`.`verification_denial_reasons`
ADD INDEX `is_poid_related_ik` (`is_poid_related` ASC),
ADD INDEX `is_poa_related_ik` (`is_poa_related` ASC),
ADD INDEX `is_photo_related_ik` (`is_photo_related` ASC),
ADD INDEX `date_created_ik` (`date_created` ASC);
;
