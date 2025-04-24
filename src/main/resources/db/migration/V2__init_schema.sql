DROP TABLE IF EXISTS `permission`;

CREATE TABLE `permission` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `permission` VALUES (1,'add and delete Reviewers'),(3,'add and delete Translators'),(4,'import and export files'),(5,'add, modify and delete terms'),(6,'add and delete other Admins');

DROP TABLE IF EXISTS `role_permission`;

CREATE TABLE `role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(10) unsigned NOT NULL,
  `permission` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `role_permission_ibfk_1` (`role_id`),
  KEY `role_permission_ibfk_2` (`permission`),
  CONSTRAINT `role_permission_ibfk_2` FOREIGN KEY (`permission`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `role_permission_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `role_permission` VALUES (1,1,1),(2,1,3),(3,1,4),(4,1,5),(5,1,6),(6,3,3),(7,2,4);