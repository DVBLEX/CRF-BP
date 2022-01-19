DROP TABLE IF EXISTS `system_parameters`;
CREATE TABLE `system_parameters`
(
    `id`                                         int          NOT NULL,
    `errors_from_email`                          varchar(255) NOT NULL,
    `errors_from_email_password`                 varchar(255) NOT NULL,
    `errors_to_email`                            varchar(255) NOT NULL,
    `contact_email`                              varchar(64)  NOT NULL,
    `password_forgot_email_limit`                int          NOT NULL,
    `reg_email_code_send_limit`                  int          NOT NULL,
    `reg_email_verification_limit`               int          NOT NULL,
    `reg_email_code_valid_minutes`               int          NOT NULL,
    `reg_email_verification_valid_hours`         int          NOT NULL,
    `reg_sms_code_send_limit`                    int          NOT NULL,
    `reg_sms_verification_limit`                 int          NOT NULL,
    `reg_sms_code_valid_minutes`                 int          NOT NULL,
    `reg_sms_verification_valid_hours`           int          NOT NULL,
    `reg_link_valid_hours`                       int          NOT NULL,
    `login_lock_count_failed`                    int          NOT NULL,
    `login_lock_period`                          int          NOT NULL,
    `login_password_valid_period`                int          NOT NULL,
    `email_mobile_fileupload_link_limit`         int          NOT NULL,
    `email_mobile_fileupload_link_valid_minutes` int          NOT NULL,
    `password_forgot_url_valid_minutes`          int          NOT NULL,
    `initiated_deposit_expiry_days`              int          NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `email_config`;
CREATE TABLE `email_config` 
(
	`id`                           int(11) NOT NULL,
	`smtp_host`                    varchar(64) NOT NULL,
	`smtp_auth`                    varchar(8) NOT NULL,
	`smtp_port`                    varchar(8) NOT NULL,
	`smtp_starttls_enable`         varchar(8) NULL,
	`smtp_ssl_protocols`           varchar(128) NULL,
	`operator_id`                  int(11) NOT NULL,
	`date_created`                 datetime NOT NULL,
	`date_edited`                  datetime NOT NULL,
	 PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `email_templates`;
CREATE TABLE `email_templates`
(
    `id`                  int           NOT NULL,
    `type`                int           NOT NULL,
    `name`                varchar(64)   NOT NULL,
    `config_id`           int           NOT NULL,
    `email_from`          varchar(64)  DEFAULT NULL,
    `email_from_password` varchar(64)  DEFAULT NULL,
    `email_bcc`           varchar(256) DEFAULT NULL,
    `subject`             varchar(128)  NOT NULL,
    `template`            varchar(8192) NOT NULL,
    `message`             varchar(8192) NOT NULL,
    `variables`           varchar(128)  NOT NULL,
    `priority`            int           NOT NULL,
    `operator_id`         int           NOT NULL,
    `date_created`        datetime      NOT NULL,
    `date_edited`         datetime      NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sms_config`;
CREATE TABLE `sms_config`
(
    `id`           int          NOT NULL,
    `url`          varchar(128) NOT NULL,
    `username`     varchar(32)  NOT NULL,
    `password`     varchar(32)  NOT NULL,
    `operator_id`  int          NOT NULL,
    `date_created` datetime     NOT NULL,
    `date_edited`  datetime     NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sms_templates`;
CREATE TABLE `sms_templates`
(
    `id`           int          NOT NULL,
    `type`         int          NOT NULL,
    `name`         varchar(64)  NOT NULL,
    `config_id`    int          NOT NULL,
    `source_addr`  varchar(32)  NOT NULL,
    `message`      varchar(640) NOT NULL,
    `variables`    varchar(128) NOT NULL,
    `priority`     int          NOT NULL,
    `operator_id`  int          NOT NULL,
    `date_created` datetime     NOT NULL,
    `date_edited`  datetime     NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `sms_scheduler`;
CREATE TABLE `sms_scheduler`
(
    `id`             int          NOT NULL AUTO_INCREMENT,
    `is_processed`   int          NOT NULL,
    `type`           int          DEFAULT NULL,
    `config_id`      varchar(11)  NOT NULL,
    `customer_id`    int          NOT NULL,
    `template_id`    int          NOT NULL,
    `priority`       int          NOT NULL,
    `msisdn`         varchar(32)  NOT NULL,
    `source_addr`    varchar(32)  NOT NULL,
    `message`        varchar(320) NOT NULL,
    `channel`        int          NOT NULL,
    `date_created`   datetime     NOT NULL,
    `date_scheduled` datetime     NOT NULL,
    `retry_count`    int          NOT NULL,
    `date_processed` datetime     DEFAULT NULL,
    `transaction_id` int          NOT NULL,
    `response_code`  int          NOT NULL,
    `response_text`  varchar(256) DEFAULT NULL,
    PRIMARY KEY (`id`)
);


DROP TABLE IF EXISTS `deposit_products`;
CREATE TABLE `deposit_products`
(
    `id`                                   int            NOT NULL AUTO_INCREMENT,
    `code`                                 varchar(64)    NOT NULL,
    `name`                                 varchar(32)    NOT NULL,
    `description`                          varchar(256)   NOT NULL,
    `yearly_rate_interest`                 decimal(4, 2)  NOT NULL,
    `twice_yearly_rate_interest`           decimal(4, 2)  NOT NULL,
    `quarterly_rate_interest`              decimal(4, 2)  NOT NULL,
    `term_years`                           decimal(2, 0)  NOT NULL,
    `amount_deposit_min`                   decimal(10, 2) NOT NULL,
    `amount_deposit_max`                   decimal(10, 2) NOT NULL,
    `premature_withdrawal_min_period_days` int            NOT NULL COMMENT 'represents the amount of days after the deposit start date in which investors don''t get any interest (only their initial investment), if they decide to withdraw',
    `premature_withdrawal_min_days`        int            NOT NULL COMMENT 'represents the amount of days after the deposit start date in which investors don''t get any interest (only their initial investment), if they decide to withdraw',
    `premature_withdrawal_rate_interest`   decimal(4, 2)  NOT NULL,
    `fee_withdrawal`                       decimal(10, 2) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `operators`;
CREATE TABLE `operators`
(
    `id`                              int          NOT NULL AUTO_INCREMENT,
    `code`                            varchar(64)  NOT NULL,
    `customer_id`                     int          NOT NULL,
    `first_name`                      varchar(32)  NOT NULL,
    `last_name`                       varchar(32)  NOT NULL,
    `email`                           varchar(64)  NOT NULL,
    `msisdn`                          varchar(16)  NOT NULL,
    `username`                        varchar(64)  NOT NULL,
    `password`                        varchar(128) NOT NULL,
    `role_id`                         int          NOT NULL,
    `is_active`                       boolean      NOT NULL,
    `is_deleted`                      boolean      NOT NULL,
    `is_locked`                       boolean      NOT NULL,
    `login_failure_count`             int          NOT NULL,
    `date_locked`                     datetime    DEFAULT NULL,
    `date_last_login`                 datetime    DEFAULT NULL,
    `date_last_attempt`               datetime    DEFAULT NULL,
    `operator_id`                     int          NOT NULL,
    `count_passwd_forgot_requests`    int          NOT NULL,
    `date_last_passwd_forgot_request` datetime(3) DEFAULT NULL,
    `date_password_forgot_reported`   datetime(3) DEFAULT NULL,
    `date_last_password`              datetime(3)  NOT NULL,
    `date_last_passwd_set_up`         datetime(3) DEFAULT NULL,
    `is_credentials_expired`          boolean      NOT NULL,
    `date_created`                    datetime     NOT NULL,
    `date_edited`                     datetime     NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `customers`;
CREATE TABLE `customers`
(
    `id`                          int         NOT NULL AUTO_INCREMENT,
    `code`                        varchar(64) NOT NULL,
    `type`                        int         NOT NULL,
    `category`                    int         NOT NULL,
    `title`                       varchar(16) NOT NULL,
    `first_name`                  varchar(64) NOT NULL,
    `last_name`                   varchar(64) NOT NULL,
    `date_of_birth`               datetime    NOT NULL,
    `email`                       varchar(64) NOT NULL,
    `msisdn`                      varchar(16) NOT NULL,
    `national_id_number`          varchar(32) NOT NULL,
    `nationality`                 varchar(2)  NOT NULL,
    `residnence_country`          varchar(2)  NOT NULL,
    `address_1`                   varchar(64) NOT NULL,
    `address_2`                   varchar(64) NOT NULL,
    `address_3`                   varchar(64) NOT NULL,
    `address_4`                   varchar(64) NOT NULL,
    `post_code`                   varchar(16) NOT NULL,
    `kyc_option`                  int         NOT NULL,
    `id_1_type`                   int         NOT NULL,
    `id_1_number`                 varchar(64) NOT NULL,
    `id_2_type`                   int         NOT NULL,
    `id_2_number`                 varchar(64) NOT NULL,
    `date_id_1_expiry`            datetime DEFAULT NULL,
    `date_id_2_expiry`            datetime DEFAULT NULL,
    `poa_1_type`                  int         NOT NULL,
    `poa_2_type`                  int         NOT NULL,
    `is_passport_scan_uploaded`   boolean     NOT NULL,
    `date_passport_scan_uploaded` datetime DEFAULT NULL,
    `is_passport_scan_verified`   boolean     NOT NULL,
    `date_passport_scan_verified` datetime DEFAULT NULL,
    `is_passport_scan_denied`     boolean     NOT NULL,
    `date_passport_scan_denied`   datetime DEFAULT NULL,
    `is_photo_uploaded`           boolean     NOT NULL,
    `date_photo_uploaded`         datetime DEFAULT NULL,
    `is_bank_account_setup`       boolean     NOT NULL,
    `is_aml_verified`             boolean     NOT NULL,
    `is_deleted`                  boolean     NOT NULL,
    `date_deleted`                datetime DEFAULT NULL,
    `date_created`                datetime    NOT NULL,
    `date_edited`                 datetime    NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `system_timer_tasks`;
CREATE TABLE `system_timer_tasks`
(
    `id`            int         NOT NULL,
    `name`          varchar(64) NOT NULL,
    `date_last_run` datetime    NOT NULL,
    `type`          varchar(16) NOT NULL,
    `period`        varchar(32) NOT NULL,
    `application`   varchar(32) NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `deposit_accounts`;
CREATE TABLE `deposit_accounts`
(
    `id`                                 int            NOT NULL AUTO_INCREMENT,
    `code`                               varchar(64)    NOT NULL,
    `customer_id`                        int            NOT NULL,
    `deposit_product_id`                 int            NOT NULL,
    `account_number`                     varchar(8)     NOT NULL,
    `amount_deposit`                     decimal(10, 2) NOT NULL,
    `interest_payout_frequency`          int            NOT NULL,
    `rate_interest`                      decimal(4, 2)  NOT NULL,
    `term_years`                         decimal(2, 0)  NOT NULL,
    `status`                             int            NOT NULL,
    `bank_transfer_reference`            varchar(16)    NOT NULL,
    `premature_withdrawal_min_days`      int            NOT NULL,
    `premature_withdrawal_rate_interest` decimal(4, 2)  NOT NULL,
    `fee_withdrawal`                     decimal(10, 2) NOT NULL,
    `amount_deposit_withdrawal`          decimal(10, 2) NOT NULL,
    `amount_interest_earned`             decimal(10, 2) NOT NULL,
    `date_open`                          datetime       NOT NULL,
    `date_start`                         datetime DEFAULT NULL,
    `date_maturity`                      datetime DEFAULT NULL,
    `date_last_interest_payment`         datetime DEFAULT NULL,
    `date_withdraw_request`              datetime DEFAULT NULL,
    `date_withdraw_approve`              datetime DEFAULT NULL,
    `date_created`                       datetime       NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `deposit_account_payments`;
CREATE TABLE `deposit_account_payments`
(
    `id`                        int            NOT NULL AUTO_INCREMENT,
    `code`                      varchar(64)    NOT NULL,
    `deposit_account_id`        int            NOT NULL,
    `account_number`            varchar(8)     NOT NULL,
    `interest_payout_frequency` int            NOT NULL,
    `customer_id`               int            NOT NULL,
    `customer_name`             varchar(128)   NOT NULL,
    `amount`                    decimal(10, 2) NOT NULL,
    `operator_id`               int            NOT NULL,
    `is_processed`              boolean        NOT NULL,
    `date_processed`            datetime DEFAULT NULL,
    `date_period_from`          DATETIME       NULL,
    `date_period_to`            DATETIME       NULL,
    `date_created`              datetime       NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `deposit_statements`;
CREATE TABLE `deposit_statements`
(
    `id`                        int            NOT NULL AUTO_INCREMENT,
    `code`                      varchar(64)    NOT NULL,
    `type`                      int            NOT NULL,
    `description`               varchar(128)   NOT NULL,
    `deposit_account_id`        int            NOT NULL,
    `account_number`            varchar(8)     NOT NULL,
    `customer_id`               int            NOT NULL,
    `amount_transaction`        decimal(10, 2) NOT NULL,
    `amount_balance`            decimal(10, 2) NOT NULL,
    `date_created`              datetime       NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `email_log`;
CREATE TABLE `email_log`
(
    `id`              int          NOT NULL AUTO_INCREMENT,
    `is_processed`    int          NOT NULL,
    `type`            int          NOT NULL,
    `config_id`       int          NOT NULL,
    `customer_id`     int          NOT NULL,
    `template_id`     int          NOT NULL,
    `priority`        int          NOT NULL,
    `email_to`        varchar(256) NOT NULL,
    `email_reply_to`  varchar(256) DEFAULT NULL,
    `email_bcc`       varchar(256) DEFAULT NULL,
    `subject`         varchar(128) NOT NULL,
    `channel`         int          NOT NULL,
    `attachment_path` varchar(256) NOT NULL,
    `date_created`    datetime     NOT NULL,
    `date_scheduled`  datetime     NOT NULL,
    `retry_count`     int          NOT NULL,
    `date_processed`  datetime     DEFAULT NULL,
    `response_code`   varchar(128) NOT NULL,
    `response_text`   varchar(256) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `email_scheduler`;
CREATE TABLE `email_scheduler`
(
    `id`              int           NOT NULL,
    `is_processed`    int           NOT NULL,
    `type`            int           NOT NULL,
    `config_id`       int           NOT NULL,
    `customer_id`     int           NOT NULL,
    `template_id`     int           NOT NULL,
    `priority`        int           NOT NULL,
    `email_to`        varchar(256)  NOT NULL,
    `email_reply_to`  varchar(256) DEFAULT NULL,
    `email_bcc`       varchar(256) DEFAULT NULL,
    `subject`         varchar(128)  NOT NULL,
    `message`         varchar(8192) NOT NULL,
    `channel`         int           NOT NULL,
    `attachment_path` varchar(256)  NOT NULL,
    `date_created`    datetime      NOT NULL,
    `date_scheduled`  datetime      NOT NULL,
    `retry_count`     int           NOT NULL,
    `date_processed`  datetime     DEFAULT NULL,
    `response_code`   varchar(128)  NOT NULL,
    `response_text`   varchar(256) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `operator_activity_log`;
CREATE TABLE `operator_activity_log`
(
    `id`            int           NOT NULL AUTO_INCREMENT,
    `operator_id`   int           NOT NULL,
    `activity_id`   int           NOT NULL,
    `activity_name` varchar(128)  NOT NULL,
    `json`          varchar(8192) NOT NULL,
    `date_created`  datetime      NOT NULL,
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `deposit_account_documents`;
CREATE TABLE `deposit_account_documents`
(
    `id`                         int(11)      NOT NULL AUTO_INCREMENT,
    `code`                       varchar(64)  NOT NULL,
    `type`                       int(11)      NOT NULL,
    `customer_id`                int(11)      NOT NULL,
    `deposit_account_id`         int(11)      NOT NULL,
    `deposit_product_id`         int(11)      NOT NULL,
    `deposit_account_payment_id` int(11)      NOT NULL,
    `account_number`             varchar(8)   NOT NULL,
    `path`                       varchar(128) NOT NULL,
    `date_created`               datetime     NOT NULL,
    PRIMARY KEY (`id`)
);

