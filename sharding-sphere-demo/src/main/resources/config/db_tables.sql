
--
-- Table structure for table `user_request_N`
--

DROP TABLE IF EXISTS `user_request_0`;

CREATE TABLE `user_request_0` (
  `user_id` bigint unsigned NOT NULL,
  `request_id` bigint unsigned NOT NULL,
  `request_title` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `request_type` tinyint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`request_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `user_request_1`;

CREATE TABLE `user_request_1` (
  `user_id` bigint unsigned NOT NULL,
  `request_id` bigint unsigned NOT NULL,
  `request_title` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `request_type` tinyint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`request_id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
