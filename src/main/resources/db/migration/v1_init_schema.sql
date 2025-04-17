DROP TABLE IF EXISTS `glossary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `glossary` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `term` varchar(255) NOT NULL DEFAULT '',
  `initial_translation` varchar(255) NOT NULL DEFAULT '',
  `context` varchar(255) NOT NULL DEFAULT '',
  `translatable` varchar(255) NOT NULL DEFAULT '',
  `comment` varchar(255) DEFAULT NULL,
  `status` varchar(255) NOT NULL DEFAULT '',
  `create_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `organization_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `organization_id` (`organization_id`),
  CONSTRAINT `glossary_ibfk_1` FOREIGN KEY (`organization_id`) REFERENCES `organizations` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `glossary`
--

LOCK TABLES `glossary` WRITE;
/*!40000 ALTER TABLE `glossary` DISABLE KEYS */;
INSERT INTO `glossary` VALUES (1,'Welcome-message','hello {user}','greetings','yes','','Pending','2025-04-17 10:11:01',2),(2,'dsfgsdgghfhj','zdfsgfxsgfdh','gcvhjgfuhkghj','yes','cgvcghhjgfhj','Pending','2025-04-17 10:08:04',2),(3,'fdfghfhjgjhgjkgk','jhjkhjkljlh','swrstrdgfxgfxfx','no','zxddfxgfxdg','Approved','2025-04-17 10:08:36',2),(4,'dsfgsdgghfhj','zdfsgfxsgfdh','gcvhjgfuhkghj','yes','cgvcghhjgfhj','NotApproved','2025-04-17 10:08:59',2),(5,'Welcome-message','hellow {user}','greetings','yes','','Pending','2025-04-17 10:09:09',2),(6,'cghhjgjkghjk','cgfhjghghu','fufhgjhgj','yes','vcvcnbvbn','Approved','2025-04-17 10:13:44',2),(7,'Welcome-message','hello{user}','greetings','yes','fdydghgj','NotApproved','2025-04-17 10:13:58',2),(8,'Welcome-message','hello {user}','greetings','yes','','Pending','2025-04-17 10:18:34',2);
/*!40000 ALTER TABLE `glossary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `images`
--

DROP TABLE IF EXISTS `images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `images` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `language_id` int(10) NOT NULL,
  `image_key` varchar(255) NOT NULL,
  `image_path` text NOT NULL,
  `project_id` int(10) unsigned NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `language_id` (`language_id`),
  KEY `project_id` (`project_id`),
  CONSTRAINT `images_ibfk_1` FOREIGN KEY (`language_id`) REFERENCES `languages` (`id`),
  CONSTRAINT `images_ibfk_2` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `images`
--

LOCK TABLES `images` WRITE;
/*!40000 ALTER TABLE `images` DISABLE KEYS */;
INSERT INTO `images` VALUES (1,1,'banner','\\\\DC1\\Devellopement\\Ariane\\image-storage\\43\\banner-en.PNG',43,'2025-04-15 10:18:01'),(2,1,'exampleImageKey','\\\\DC1\\Devellopement\\Ariane\\image-storage\\34\\exampleImageKey-en.PNG',34,'2025-04-15 11:56:00'),(4,3,'NewUpload-key','\\\\DC1\\Devellopement\\Ariane\\image-storage\\43\\NewUpload-key-fr.PNG',43,'2025-04-16 16:01:13'),(5,2,'banner','\\\\DC1\\Devellopement\\Ariane\\image-storage\\43\\banner-es.PNG',43,'2025-04-16 16:34:05'),(6,2,'New-banner','\\\\DC1\\Devellopement\\Ariane\\image-storage\\45\\New-banner-es.PNG',45,'2025-04-17 10:29:42');
/*!40000 ALTER TABLE `images` ENABLE KEYS */;
UNLOCK TABLES;