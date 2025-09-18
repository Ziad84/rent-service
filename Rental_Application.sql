-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: rental_application
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `appartments`
--

DROP TABLE IF EXISTS `appartments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appartments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `owner_id` int DEFAULT NULL,
  `client_id` int DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `monthly_rent` int DEFAULT NULL,
  `rooms_number` int DEFAULT NULL,
  `rented_at` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_apartments_owner` (`owner_id`),
  KEY `fk_apartments_client` (`client_id`),
  CONSTRAINT `fk_apartments_client` FOREIGN KEY (`client_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_apartments_owner` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appartments`
--

LOCK TABLES `appartments` WRITE;
/*!40000 ALTER TABLE `appartments` DISABLE KEYS */;
INSERT INTO `appartments` VALUES (2,17,NULL,'new Apartment in Riyadh',1500,3,'2025-08-31 12:07:51','2025-08-31 10:11:10','2025-09-04 17:00:34',1),(4,18,NULL,'Appartment in Tabuk',1000,4,NULL,'2025-09-04 13:10:34','2025-09-04 13:21:17',1),(6,29,28,'Apartment in Makkah',3500,3,'2025-09-05 16:13:11','2025-09-05 14:24:03','2025-09-05 16:13:11',0),(7,29,36,'Apartment in Khober',2500,4,'2025-09-08 11:13:40','2025-09-05 14:33:46','2025-09-08 11:13:40',0),(8,31,NULL,'Appartment in Jeddah',2000,2,'2025-09-07 11:50:00','2025-09-07 11:48:20','2025-09-07 11:52:30',1),(9,38,NULL,'Appartment in Riyadh',4000,3,NULL,'2025-09-08 12:50:38','2025-09-08 12:50:38',0),(10,38,NULL,'Appartment in Riyadh',4000,3,NULL,'2025-09-08 12:50:46','2025-09-08 12:50:46',0),(11,38,NULL,'Appartment in Riyadh',4000,3,NULL,'2025-09-08 12:50:47','2025-09-08 12:50:47',0),(12,38,NULL,'Appartment in Riyadh',4000,3,NULL,'2025-09-08 12:51:43','2025-09-08 12:51:43',0),(13,38,40,'Appartment in Jeddah',2000,3,'2025-09-11 10:46:31','2025-09-08 12:52:55','2025-09-11 10:46:31',0),(14,49,NULL,'Apartment in Tabuk',1500,4,NULL,'2025-09-14 11:12:18','2025-09-14 11:12:51',0);
/*!40000 ALTER TABLE `appartments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'Admin','2025-08-25 19:26:40','2025-08-27 10:06:46'),(2,'Owner','2025-08-25 19:26:40','2025-08-25 19:26:40'),(3,'Client','2025-08-25 19:26:40','2025-08-25 19:26:40');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `role_id` int NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `created_by` varchar(255) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_users_role` (`role_id`),
  CONSTRAINT `fk_users_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (2,'Owner User','owner@example.com','$2a$10$7qk1...hashedPassword...','1234567891',2,NULL,NULL,0,NULL,NULL),(3,'Cleint 1','saif6@gmail.com','$2a$10$7qk1...hashedPassword...','5234567892',3,NULL,'2025-08-30 13:42:05',0,NULL,NULL),(5,'Admin User','admin@example.com','$2a$10$kfYKpl536JoCNegdsZXpRe62zj8Hefep6OBM6anlDb3CmYLui5/I6','1234567890',1,'2025-08-24 21:45:38','2025-08-30 14:11:09',1,NULL,NULL),(6,'Admin2','admin2@example.com','$2a$10$rHsJNXBBzboLSoXQ4aZBWup89g83lyh7zzp3U0WSFqaFNu5pX3lA6','987654321',1,'2025-08-26 10:18:34','2025-08-26 10:18:34',0,NULL,NULL),(7,'Mohammeaad Abdulallah','sA9@gmail.com','$2a$10$M4qb5yv5Rehh8IzVvD0.D..8hUd.mRZDIQJsLv/7ANizRs7k1WQue','1234567892',3,'2025-08-26 12:56:22','2025-08-29 19:25:08',1,NULL,NULL),(10,'Salem mohammed','Salem1@example.com','$2a$10$HRJqZ4SLdaYXR.rY.JYqRumaBtAXjMmQNpaOwEauhGKwiKGYnc1lS','1234567890',3,'2025-08-26 13:51:15','2025-08-27 13:25:48',1,NULL,NULL),(11,'Salem','salem@gmail.com','$2a$10$Ts8xNU9vB/tEeZPyc/XiEutYAQahmzTg8IMvwwTX4xTNAljIHYXHG','123456788',3,'2025-08-26 13:59:27','2025-08-28 11:18:01',1,NULL,NULL),(12,'Saif mohammed','saif@gmail.com','$2a$10$7TFhki3PPWngV182fpcKheCbIxkQUQDACONukQ8QIu15aK/BPhz.K','0559746457',2,'2025-08-27 15:05:39','2025-08-28 11:10:36',1,NULL,NULL),(14,'Cleint 3','saif25@gmail.com','$2a$10$94Dtxo4wGCJ50BvwwMnUOOAeSL1zzfkIPalN16TbdB1TOXZAMFvXy','5234567844',3,'2025-08-29 19:35:04','2025-09-04 13:05:44',1,NULL,NULL),(15,'mohammed Abdulallah','MA@gmail.com','$2a$10$jauwCbv3De7odk1ZZ5n.jubSZ0r/JPdzuJTX3fEZXt86GEFOzVXk6','0559746457',2,'2025-08-30 14:17:25','2025-09-07 11:32:36',1,NULL,'System'),(17,'Owner 1','ziyad2@gmail.com','$2a$10$TxSkgRdwYmmG3uZul.suH.CF0FRu2xJ8m8MPFOy2IGGvQNvwlD7bu','5234567832',2,'2025-08-30 17:31:34','2025-09-04 17:00:34',1,NULL,'System'),(18,'Ahmed','saif22@gmail.com','$2a$10$E4w48v4192Pm0ilzZy7RS.0ckD0luL1.c6046RYRqUGcdT4T.15Dm','0564325875',2,'2025-09-03 15:18:06','2025-09-04 13:21:17',1,NULL,NULL),(19,'Ahmed','saif2@gmail.com','$2a$10$BnWgo30DNcg5ajR1llbCM.9uvK65W7Tb4uk367qrKIEAacZOCEhGC','0564325875',1,'2025-09-03 15:18:34','2025-09-03 15:18:34',0,NULL,NULL),(20,'Ahmed','saif7@gmail.com','$2a$10$jx5HQSkF7/PNSiuo7EGXEuweGjzPivqg9GWzbSfvlw.tgCuKG6BeC','0564325874',3,'2025-09-03 16:01:06','2025-09-03 16:01:06',0,NULL,NULL),(22,'Ziyad','Ziyad@gmail.com','$2a$10$fGYnuuYjBB5Ugm2TiUPbceHm1mkkUrQwJFArWClFAibLc8PTGWTyK','5234567829',3,'2025-09-03 16:15:02','2025-09-04 16:21:04',1,NULL,'System'),(23,'Cleint 3','saif711@gmail.com','$2a$10$wSZeKSuj4NqEQewTvYA3QO08FvDveGDkQmr8S4Qg9s6AZkoJ1cQnK','5234567822',3,'2025-09-04 11:14:41','2025-09-04 13:09:13',1,NULL,NULL),(25,'Ziyad','Ziyad1@gmail.com','$2a$10$NuPhhzlyPKuSxRIz/Pw5ouXhk4wQEKLFmJ6Bw5OAs2hYt42./LNHW','5234567811',3,'2025-09-04 16:51:34','2025-09-04 16:55:57',1,'System','System'),(26,'Ziad','ziad100@gmail.com','$2a$10$4upFGOtfxFLJ2VvxzaQPLu3cehPT9O//woJuqYlu2LQzqYxzh4Ija','0564325872',3,'2025-09-04 17:36:19','2025-09-04 17:36:19',0,'System','System'),(27,'Ziad','ziad10@gmail.com','$2a$10$i6884EQ9gAyda1XI9O1HSOV0bygkCM84ajRW0YkIGZYhrMfArcDKS','0564325870',3,'2025-09-05 13:00:16','2025-09-05 13:00:16',0,'System','System'),(28,'Ziad','ziad2@gmail.com','$2a$10$Rr.D8P.T4RHlqWtTtvzEc.LXGcByU/0rBXXqk/nIS.FfbKFiCmWeG','0564325871',3,'2025-09-05 13:09:04','2025-09-05 13:09:04',0,'System','System'),(29,'Ziad','ziad3@gmail.com','$2a$10$nokl9kLNZLWb5tQUdkyFKulx8X9.Qn2Amr4iy4NQmjKeD3OVug.kK','0564325854',2,'2025-09-05 13:19:11','2025-09-05 13:19:11',0,'System','System'),(30,'Ziad T','ziad.albalwi1@gmail.com','$2a$10$9j3IDZHXuw0QESHGoswLheDirY7EKZzNDXE/JFNCsn4zp04xIuGDe','0559746495',3,'2025-09-05 19:25:26','2025-09-05 19:25:26',0,'System','System'),(31,'Ziad','ziad4@gmail.com','$2a$10$mG.AlljZfGdO05qOTMdeVewKSsfP7xBY.yK.N5nKygRZId6tKa1sK','0564325851',2,'2025-09-07 11:47:10','2025-09-07 11:52:30',1,'System','System'),(32,'Ziad','ziad5@gmail.com','$2a$10$OfgwQmJwMjwuF.39WaskpOIjZJUicj/JzgUdEXe2Cg6//WxL1Q0N.','0564325850',3,'2025-09-07 13:48:17','2025-09-07 13:48:17',0,'System','System'),(33,'Ziyad','Ziyad6@gmail.com','$2a$10$N4fY9vR44fV49unH52UoqOnWN2j4GFKBydHGD01y0T0w7cgLMEP6.','5234567812',3,'2025-09-07 13:57:05','2025-09-07 14:37:59',0,'System','System'),(34,'Ziyad','Ziyad9@gmail.com','$2a$10$O5RGO/ypf1nelD4/ld2A7eNENKbaucwGIpWGxcIbRKw7KvU4JQjZe','5234567817',3,'2025-09-07 14:38:20','2025-09-07 15:29:05',1,'System','System'),(35,'Ziyad','Ziyad10@gmail.com','$2a$10$bxv1wdQIfe7kFJU2Jd7JY.Ia5hQ3UNdJgUFSYvxqc7dLjqhXDPld6','5234567827',3,'2025-09-07 14:40:20','2025-09-07 17:51:23',0,'System','System'),(36,'Ziyad','Ziyad11@gmail.com','$2a$10$w/27gwem1MBqRfN05QMuFOQWM8skuq/a83i5txLot0J9Xs8DlOHV6','5234567857',3,'2025-09-08 11:10:16','2025-09-08 11:11:23',0,'System','System'),(37,'Ziyad','ziad13@gmail.com','$2a$10$ZN3QSU8Hjzr.3g1zNw8NMO4DwD4nQ3NaN8H38ySCLulNOURYu/h5e','5234567357',3,'2025-09-08 11:19:12','2025-09-08 11:20:46',0,'System','System'),(38,'Ziyad','ziyad20@gmail.com','$2a$10$nNGQpRoCZMmzXw9FJ704puNmebWXHkGXUD3o3sCUQgxUxc1CspvcS','5234562832',2,'2025-09-08 12:49:55','2025-09-09 13:46:33',0,'System','System'),(39,'Ziad','ziad15@gmail.com','$2a$10$o7hmrI0g8iKhA1PJsuVwu.lzasDc9/zRNlNsyF3dYF.Rx4APnx1iO','0564325261',2,'2025-09-08 14:56:45','2025-09-08 14:56:45',0,'System','System'),(40,'Ziyad','ziyad18@gmail.com','$2a$10$r6.4ct869MXmMR138.ACZeI0fMPZqdM4JidB5pIkA7.OfFouRFWXm','5234564127',3,'2025-09-09 13:47:04','2025-09-09 13:47:32',0,'System','System'),(41,'Ziad','ziad18@gmail.com','$2a$10$ddctmGAwz1ryfKUvPWQehOAvnjmR69Z9FmFGn.SZzoxhC8E5gyZgC','0564325161',3,'2025-09-12 16:03:10','2025-09-12 16:03:10',0,'System','System'),(42,'Ziad','ziad19@gmail.com','$2a$10$.6lR6KH9B/mcGZX1KrfW8.KIlNsANkn4ROGKosGtE7TknyatWSjPK','0564322361',3,'2025-09-12 16:03:54','2025-09-14 11:18:09',1,'System','System'),(43,'Ziad','ziad20gmail.com','$2a$10$3jQx8uRUN9h/FSVs0s9tRex1gx63znuMyiXC4AsIK2KB.d22R0TmO','0564322661',3,'2025-09-12 16:10:36','2025-09-12 16:10:36',0,'System','System'),(44,'Ziad','ziad21gmail.com','$2a$10$r99jnVyx64tE84guIvDyJO7n.kehj1ezK3HI6478863/i12pvVHtK','0564541661',3,'2025-09-12 16:14:04','2025-09-12 16:14:04',0,'System','System'),(45,'Ziyad','ziyad12@gmail.com','$2a$10$DzmwikXjJ3VOU280JhBXL.nKc/F09pEmdaUY41.trbQ4EA/n/pB3O','5234564167',3,'2025-09-12 18:50:55','2025-09-13 13:31:19',0,'System','System'),(46,'Ziad','ziad23@gmail.com','$2a$10$FZ19aOfIfRrAIfa0h6wJeuxUvJjfFi7mT3yxUiWy.lGz/51rY7vDK','0569141661',2,'2025-09-14 08:10:52','2025-09-14 08:10:52',0,'System','System'),(47,'Ziyad','ziyad5@gmail.com','$2a$10$QLt5nDyNcK2bRS69Faz5iONhDxiHoBT9SL6RQinfjyF6nw./076Ee','0534564167',3,'2025-09-14 11:07:25','2025-09-14 11:08:55',1,'System','System'),(48,'Ziyad','ziyad3@gmail.com','$2a$10$qCrbENCIQm/3TvekzeKveuKUFt0r/oRKNScRQVnXsidD4P1mKdxSq','0512424167',3,'2025-09-14 11:09:00','2025-09-16 10:29:38',0,'System','System'),(49,'Ziyad','Ziyad15@gmail.com','$2a$10$n2C52Zdf8UfHrmcc1CuJlOdXgD43ixqSnPqgC0BPrmpypGxS5ciRC','5234262832',2,'2025-09-14 11:09:41','2025-09-14 11:11:14',0,'System','System');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-18 17:31:11
