
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cauldron_recipe_material_tbl` (
  `cauldron_recipe_material_id` bigint NOT NULL AUTO_INCREMENT,
  `slot_number` int NOT NULL,
  `cauldron_recipe_id` bigint NOT NULL,
  `item_id` bigint NOT NULL,
  PRIMARY KEY (`cauldron_recipe_material_id`),
  UNIQUE KEY `UK90butcnlhfrlr0klvwtrkksej` (`cauldron_recipe_id`,`slot_number`),
  KEY `FK9t91ish0bxvf778t1rc8yhw2` (`item_id`),
  CONSTRAINT `FK9t91ish0bxvf778t1rc8yhw2` FOREIGN KEY (`item_id`) REFERENCES `item_tbl` (`item_id`),
  CONSTRAINT `FKoqggurehmq7rb42ewe78vxom2` FOREIGN KEY (`cauldron_recipe_id`) REFERENCES `cauldron_recipe_tbl` (`cauldron_recipe_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cauldron_recipe_tbl` (
  `cauldron_recipe_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `recipe_id` varchar(36) NOT NULL,
  `status` enum('ACTIVE','DELETED') NOT NULL,
  `result_item_id` bigint NOT NULL,
  PRIMARY KEY (`cauldron_recipe_id`),
  UNIQUE KEY `UKgdf4l1ifpc6syi3ownyr2xfp9` (`recipe_id`),
  KEY `FK499ogbdqb2bnlrxjv1y8j1qt6` (`result_item_id`),
  CONSTRAINT `FK499ogbdqb2bnlrxjv1y8j1qt6` FOREIGN KEY (`result_item_id`) REFERENCES `item_tbl` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_submission_tbl` (
  `item_submission_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `approved_at` datetime(6) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `image_url` varchar(2048) NOT NULL,
  `name` varchar(15) NOT NULL,
  `status` enum('PENDING','APPROVED','COMBINED','REJECTED','CANCELED') NOT NULL,
  `submission_id` varchar(36) NOT NULL,
  `submitter_id` bigint NOT NULL,
  `combined_at` datetime(6) DEFAULT NULL,
  `reserved_name` varchar(15) GENERATED ALWAYS AS ((case when (`status` in (_utf8mb4'PENDING',_utf8mb4'APPROVED',_utf8mb4'COMBINED')) then `name` else NULL end)) STORED,
  PRIMARY KEY (`item_submission_id`),
  UNIQUE KEY `UKqw25bocr3cejkit4i9rgpgtir` (`submission_id`),
  UNIQUE KEY `UKmaiwgpr3yico1vojc8mcckmka` (`reserved_name`),
  KEY `FK6l4fln94bbqj3ar3hx6q6v8ov` (`submitter_id`),
  CONSTRAINT `FK6l4fln94bbqj3ar3hx6q6v8ov` FOREIGN KEY (`submitter_id`) REFERENCES `user_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_tbl` (
  `item_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `approved_at` datetime(6) NOT NULL,
  `description` varchar(1000) NOT NULL,
  `image_url` varchar(2048) NOT NULL,
  `public_item_id` varchar(36) NOT NULL,
  `name` varchar(30) NOT NULL,
  `creator_id` bigint NOT NULL,
  `status` enum('COMBINATION','GACHA') NOT NULL DEFAULT 'GACHA',
  PRIMARY KEY (`item_id`),
  UNIQUE KEY `UKrbjsw9xh8r3h5kqvy3ruqm675` (`public_item_id`),
  KEY `FKsnhgrjcxg2sxb4y3hy0bwb7xt` (`creator_id`),
  CONSTRAINT `FKsnhgrjcxg2sxb4y3hy0bwb7xt` FOREIGN KEY (`creator_id`) REFERENCES `user_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trade_comment_item_tbl` (
  `trade_comment_item_id` bigint NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `item_id` bigint NOT NULL,
  `trade_comment_id` bigint NOT NULL,
  PRIMARY KEY (`trade_comment_item_id`),
  KEY `FK7ss20hrll6rexa793r97m1388` (`item_id`),
  KEY `FKhpchi4m3dfiflnexy9uj96c9a` (`trade_comment_id`),
  CONSTRAINT `FK7ss20hrll6rexa793r97m1388` FOREIGN KEY (`item_id`) REFERENCES `item_tbl` (`item_id`),
  CONSTRAINT `FKhpchi4m3dfiflnexy9uj96c9a` FOREIGN KEY (`trade_comment_id`) REFERENCES `trade_comment_tbl` (`trade_comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trade_comment_tbl` (
  `trade_comment_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `public_trade_comment_id` varchar(36) NOT NULL,
  `status` enum('ACCEPTED','PENDING') NOT NULL,
  `author_id` bigint NOT NULL,
  `trade_post_id` bigint NOT NULL,
  PRIMARY KEY (`trade_comment_id`),
  UNIQUE KEY `UKpd2i8394ol4pmpq4t0kit7g7v` (`public_trade_comment_id`),
  KEY `FKrdulrwho1peiems24uggiu9s7` (`author_id`),
  KEY `FKmrqhqi2fh4g613pi7lal4qy35` (`trade_post_id`),
  CONSTRAINT `FKmrqhqi2fh4g613pi7lal4qy35` FOREIGN KEY (`trade_post_id`) REFERENCES `trade_post_tbl` (`trade_post_id`),
  CONSTRAINT `FKrdulrwho1peiems24uggiu9s7` FOREIGN KEY (`author_id`) REFERENCES `user_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trade_post_item_tbl` (
  `trade_post_item_id` bigint NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `item_id` bigint NOT NULL,
  `trade_post_id` bigint NOT NULL,
  PRIMARY KEY (`trade_post_item_id`),
  KEY `FKh3cg24i56pd53vm4cmeewt979` (`item_id`),
  KEY `FK16eakaiwa0mokvw0he7tb845x` (`trade_post_id`),
  CONSTRAINT `FK16eakaiwa0mokvw0he7tb845x` FOREIGN KEY (`trade_post_id`) REFERENCES `trade_post_tbl` (`trade_post_id`),
  CONSTRAINT `FKh3cg24i56pd53vm4cmeewt979` FOREIGN KEY (`item_id`) REFERENCES `item_tbl` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trade_post_tbl` (
  `trade_post_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `completed_at` datetime(6) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `status` enum('COMPLETED','DELETED','OPEN') NOT NULL,
  `title` varchar(100) NOT NULL,
  `public_trade_post_id` varchar(36) NOT NULL,
  `author_id` bigint NOT NULL,
  PRIMARY KEY (`trade_post_id`),
  UNIQUE KEY `UK1q24s52gxhlram90e8ql0ge6i` (`public_trade_post_id`),
  KEY `FK90r3rskj9mcbsunyl93p0ctdk` (`author_id`),
  CONSTRAINT `FK90r3rskj9mcbsunyl93p0ctdk` FOREIGN KEY (`author_id`) REFERENCES `user_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_item_tbl` (
  `user_item_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `quantity` int NOT NULL,
  `reserved_quantity` int NOT NULL,
  `item_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`user_item_id`),
  UNIQUE KEY `UKoo6mxf8ga0nognmbdthvea0gu` (`user_id`,`item_id`),
  KEY `FK88n4xe2lkdettxpor40csiyfh` (`item_id`),
  CONSTRAINT `FK88n4xe2lkdettxpor40csiyfh` FOREIGN KEY (`item_id`) REFERENCES `item_tbl` (`item_id`),
  CONSTRAINT `FKetvfhk4gb7ems1rgwj5m7m3t` FOREIGN KEY (`user_id`) REFERENCES `user_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_tbl` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `currency` int NOT NULL,
  `email` varchar(255) NOT NULL,
  `login_id` varchar(20) NOT NULL,
  `nickname` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','USER') NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UKi4ygcc30htflmb5xe5mjcydid` (`email`),
  UNIQUE KEY `UKjdk15l4i4s8y5b2g1e1gxm6r6` (`login_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

