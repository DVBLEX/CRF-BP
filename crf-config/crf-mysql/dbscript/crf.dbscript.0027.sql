USE crf;

CREATE TABLE crf.`customer_aml_responses` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `customer_id` int(11) NOT NULL,
 `aml_scan_response`  text NOT NULL,
 `date_created` datetime  NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;