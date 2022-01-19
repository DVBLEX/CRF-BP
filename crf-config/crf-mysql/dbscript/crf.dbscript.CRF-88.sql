USE crf;

CREATE TABLE crf.`deposit_statements` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`code` varchar(64) NOT NULL,
`type` int(11) NOT NULL,
`description` varchar(128) NOT NULL,
`deposit_account_id` int(11) NOT NULL,
`account_number` varchar(8) NOT NULL,
`customer_id` int(11) NOT NULL,
`amount_transaction` decimal(10,2) NOT NULL,
`amount_balance` decimal(10,2) NOT NULL,
`date_created` datetime NOT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `code_uk` (`code`),
KEY `deposit_account_id_ik` (`deposit_account_id`),
KEY `account_number_ik` (`account_number`),
KEY `customer_id_ik` (`customer_id`),
KEY `amount_transaction_ik` (`amount_transaction`),
KEY `amount_balance_ik` (`amount_balance`),
KEY `date_created_ik` (`date_created`)
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8;
