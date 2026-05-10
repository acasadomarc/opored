USE opored;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;


CREATE TABLE `roles` (
                         `id` int(11) NOT NULL AUTO_INCREMENT,
                         `name` enum('SUPER_ADMIN','ADMIN','PROFESSOR','MODERATOR','STUDENT') NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;


CREATE TABLE `permissions` (
                               `id` int(11) NOT NULL AUTO_INCREMENT,
                               `name` enum('USER_CREATE','USER_UPDATE','USER_DELETE','STUDENT_READ','STUDENT_CREATE','STUDENT_UPDATE','STUDENT_DELETE','MODERATION_READ','MODERATION_CREATE','MODERATION_UPDATE','MODERATION_DELETE','ADMINISTRATION_READ','ADMINISTRATION_CREATE','ADMINISTRATION_UPDATE','ADMINISTRATION_DELETE','PROFESSOR_READ','PROFESSOR_CREATE','PROFESSOR_UPDATE','PROFESSOR_DELETE','ROOT','USER_READ') NOT NULL,
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `roles_permissions`
--

DROP TABLE IF EXISTS `roles_permissions`;


CREATE TABLE `roles_permissions` (
                                     `role_id` int(11) NOT NULL,
                                     `permission_id` int(11) NOT NULL,
                                     PRIMARY KEY (`role_id`,`permission_id`),
                                     KEY `permission_id` (`permission_id`),
                                     CONSTRAINT `roles_permissions_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
                                     CONSTRAINT `roles_permissions_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;


CREATE TABLE `users` (
                         `id` int(11) NOT NULL AUTO_INCREMENT,
                         `name` varchar(100) NOT NULL,
                         `surname` varchar(100) NOT NULL,
                         `alias` varchar(100) NOT NULL,
                         `email` varchar(100) NOT NULL,
                         `password` varchar(255) NOT NULL,
                         `profile_photo` varchar(255),
                         `registration_date` date NOT NULL,
                         `is_enabled` bit(1) NOT NULL DEFAULT b'1',
                         `account_no_expired` bit(1) NOT NULL DEFAULT b'1',
                         `account_no_locked` bit(1) NOT NULL DEFAULT b'1',
                         `credential_no_expired` bit(1) NOT NULL DEFAULT b'1',
                         `is_deleted` bit(1) DEFAULT b'0',
                         `role` int(11) NOT NULL DEFAULT 5,
                         PRIMARY KEY (`id`),
                         KEY `users_ibfk_1` (`role`),
                         CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `administrators`
--

DROP TABLE IF EXISTS `administrators`;


CREATE TABLE `administrators` (
                                  `id` int(11) NOT NULL,
                                  PRIMARY KEY (`id`),
                                  CONSTRAINT `administrators_ibfk_1` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;


CREATE TABLE `students` (
                            `id` int(11) NOT NULL,
                            PRIMARY KEY (`id`),
                            CONSTRAINT `students_ibfk_1` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `moderators`
--

DROP TABLE IF EXISTS `moderators`;


CREATE TABLE `moderators` (
                              `id` int(11) NOT NULL,
                              PRIMARY KEY (`id`),
                              CONSTRAINT `moderators_ibfk_1` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `professors`
--

DROP TABLE IF EXISTS `professors`;


CREATE TABLE `professors` (
                              `id` int(11) NOT NULL AUTO_INCREMENT,
                              PRIMARY KEY (`id`),
                              CONSTRAINT `professors_ibfk_1` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;


CREATE TABLE `categories` (
                              `id` int(11) NOT NULL AUTO_INCREMENT,
                              `name` varchar(100) NOT NULL,
                              `description` text DEFAULT NULL,
                              `is_deleted` bit(1) NOT NULL DEFAULT b'0',
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `forums`
--

DROP TABLE IF EXISTS `forums`;


CREATE TABLE `forums` (
                          `id` int(11) NOT NULL AUTO_INCREMENT,
                          `name` varchar(100) NOT NULL,
                          `description` text DEFAULT NULL,
                          `is_deleted` bit(1) DEFAULT b'0',
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `bulletin_boards`
--

DROP TABLE IF EXISTS `bulletin_boards`;


CREATE TABLE `bulletin_boards` (
                                   `id` int(11) NOT NULL AUTO_INCREMENT,
                                   `name` varchar(100) NOT NULL,
                                   `description` text DEFAULT NULL,
                                   `is_deleted` bit(1) DEFAULT b'0',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `public_examinations`
--

DROP TABLE IF EXISTS `public_examinations`;


-- opored.public_examinations definition

CREATE TABLE `public_examinations` (
                                       `id` int(11) NOT NULL AUTO_INCREMENT,
                                       `name` varchar(100) NOT NULL,
                                       `description` text DEFAULT NULL,
                                       `is_deleted` bit(1) DEFAULT b'0',
                                       `is_visible` bit(1) DEFAULT b'1',
                                       `category_id` int(11) NOT NULL,
                                       `bulletin_board_id` int(11) DEFAULT NULL,
                                       `forum_id` int(11) DEFAULT NULL,
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `bulletin_board_id` (`bulletin_board_id`),
                                       UNIQUE KEY `forum_id` (`forum_id`),
                                       KEY `category_id` (`category_id`),
                                       CONSTRAINT `public_examinations_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
                                       CONSTRAINT `public_examinations_ibfk_2` FOREIGN KEY (`bulletin_board_id`) REFERENCES `bulletin_boards` (`id`),
                                       CONSTRAINT `public_examinations_ibfk_3` FOREIGN KEY (`forum_id`) REFERENCES `forums` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `student_public_examinations`
--

DROP TABLE IF EXISTS `student_public_examinations`;


CREATE TABLE `student_public_examinations` (
                                               `student_id` int(11) NOT NULL,
                                               `public_examination_id` int(11) NOT NULL,
                                               PRIMARY KEY (`student_id`,`public_examination_id`),
                                               KEY `public_examination_id` (`public_examination_id`),
                                               CONSTRAINT `student_public_examinations_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`),
                                               CONSTRAINT `student_public_examinations_ibfk_2` FOREIGN KEY (`public_examination_id`) REFERENCES `public_examinations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `announcements`
--

DROP TABLE IF EXISTS `announcements`;


CREATE TABLE `announcements` (
                                 `id` int(11) NOT NULL AUTO_INCREMENT,
                                 `title` text NOT NULL,
                                 `content` text NOT NULL,
                                 `related_links` text DEFAULT NULL,
                                 `publication_date` date DEFAULT curdate(),
                                 `is_deleted` bit(1) DEFAULT b'0',
                                 `classification_confidence` float DEFAULT NULL,
                                 `bulletin_board_id` int(11) NOT NULL,
                                 PRIMARY KEY (`id`),
                                 KEY `bulletin_board_id` (`bulletin_board_id`),
                                 CONSTRAINT `announcements_ibfk_1` FOREIGN KEY (`bulletin_board_id`) REFERENCES `bulletin_boards` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `announcements_staging`
--

DROP TABLE IF EXISTS `announcements_staging`;

CREATE TABLE `announcements_staging` (
                                         `title` varchar(767) NOT NULL,
                                         `content` text NOT NULL,
                                         `related_links` text DEFAULT NULL,
                                         `publication_date` date NOT NULL DEFAULT curdate(),
                                         `bulletin_board_id` int(11) NOT NULL,
                                         `classification_confidence` float DEFAULT NULL,
                                         `deployed` binary(1) NOT NULL DEFAULT x'30',
                                         PRIMARY KEY (`title`,`publication_date`),
                                         KEY `bulletin_board_id` (`bulletin_board_id`),
                                         CONSTRAINT `announcements_staging_ibfk_1` FOREIGN KEY (`bulletin_board_id`) REFERENCES `bulletin_boards` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `topics`
--

DROP TABLE IF EXISTS `topics`;


CREATE TABLE `topics` (
                          `id` int(11) NOT NULL AUTO_INCREMENT,
                          `title` varchar(100) NOT NULL,
                          `status` enum('VISIBLE','HIDDEN') NOT NULL,
                          `publication_date` date NOT NULL,
                          `is_deleted` bit(1) DEFAULT b'0',
                          `forum_id` int(11) NOT NULL,
                          `user_id` int(11) NOT NULL,
                          PRIMARY KEY (`id`),
                          KEY `topics_ibfk_1` (`forum_id`),
                          KEY `topics_users_FK` (`user_id`),
                          CONSTRAINT `topics_ibfk_1` FOREIGN KEY (`forum_id`) REFERENCES `forums` (`id`),
                          CONSTRAINT `topics_users_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `follow_topic`
--

DROP TABLE IF EXISTS `follow_topic`;


CREATE TABLE `follow_topic` (
                                `topic_id` int(11) NOT NULL,
                                `student_id` int(11) NOT NULL,
                                PRIMARY KEY (`topic_id`,`student_id`),
                                KEY `student_id` (`student_id`),
                                CONSTRAINT `follow_topic_ibfk_1` FOREIGN KEY (`topic_id`) REFERENCES `topics` (`id`),
                                CONSTRAINT `follow_topic_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;



--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;


CREATE TABLE `messages` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `content` text NOT NULL,
                            `status` enum('VISIBLE','HIDDEN','DELETED') NOT NULL,
                            `publication_date` datetime NOT NULL,
                            `is_deleted` bit(1) DEFAULT b'0',
                            `parent_message_id` int(11) DEFAULT NULL,
                            `topic_id` int(11) NOT NULL,
                            `user_id` int(11) NOT NULL,
                            PRIMARY KEY (`id`),
                            KEY `topic_id` (`topic_id`),
                            KEY `messages_users_FK` (`user_id`),
                            CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`topic_id`) REFERENCES `topics` (`id`),
                            CONSTRAINT `messages_users_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `moderation_message`
--

DROP TABLE IF EXISTS `moderation_message`;


CREATE TABLE `moderation_message` (
                                      `message_id` int(11) NOT NULL,
                                      `moderator_id` int(11) NOT NULL,
                                      `moderation_date` date NOT NULL,
                                      `reason` text NOT NULL,
                                      `is_deleted` bit(1) DEFAULT b'0',
                                      PRIMARY KEY (`message_id`,`moderator_id`),
                                      KEY `moderator_id` (`moderator_id`),
                                      CONSTRAINT `moderation_message_ibfk_1` FOREIGN KEY (`message_id`) REFERENCES `messages` (`id`),
                                      CONSTRAINT `moderation_message_ibfk_2` FOREIGN KEY (`moderator_id`) REFERENCES `moderators` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `moderation_topic`
--

DROP TABLE IF EXISTS `moderation_topic`;


CREATE TABLE `moderation_topic` (
                                    `topic_id` int(11) NOT NULL,
                                    `moderator_id` int(11) NOT NULL,
                                    `moderation_date` date NOT NULL,
                                    `reason` text NOT NULL,
                                    `is_deleted` bit(1) DEFAULT b'0',
                                    PRIMARY KEY (`topic_id`,`moderator_id`),
                                    KEY `moderator_id` (`moderator_id`),
                                    CONSTRAINT `moderation_topic_ibfk_1` FOREIGN KEY (`topic_id`) REFERENCES `topics` (`id`),
                                    CONSTRAINT `moderation_topic_ibfk_2` FOREIGN KEY (`moderator_id`) REFERENCES `moderators` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;


CREATE TABLE `courses` (
                           `id` int(11) NOT NULL AUTO_INCREMENT,
                           `name` varchar(100) NOT NULL,
                           `description` text NOT NULL,
                           `price` float NOT NULL,
                           `discount_percentage` float DEFAULT 0,
                           `is_visible` bit(1) DEFAULT b'0',
                           `is_purchasable` bit(1) DEFAULT b'1',
                           `create_date` date DEFAULT curdate(),
                           `update_date` date DEFAULT curdate(),
                           `is_deleted` bit(1) NOT NULL DEFAULT b'0',
                           `professor_id` int(11) NOT NULL,
                           PRIMARY KEY (`id`),
                           KEY `professor_id` (`professor_id`),
                           CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`professor_id`) REFERENCES `professors` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `purchases`
--

DROP TABLE IF EXISTS `purchases`;


CREATE TABLE `purchases` (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `purchase_date` date DEFAULT curdate(),
                             `price` float NOT NULL,
                             `payment_method` varchar(100) NOT NULL,
                             `course_id` int(11) NOT NULL,
                             `student_id` int(11) NOT NULL,
                             PRIMARY KEY (`id`),
                             KEY `course_id` (`course_id`),
                             KEY `student_id` (`student_id`),
                             CONSTRAINT `purchases_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
                             CONSTRAINT `purchases_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `ratings`
--

DROP TABLE IF EXISTS `ratings`;


CREATE TABLE `ratings` (
                           `id` int(11) NOT NULL AUTO_INCREMENT,
                           `title` varchar(100) NOT NULL,
                           `score` float DEFAULT NULL,
                           `publication_date` date NOT NULL DEFAULT curdate(),
                           `student_id` int(11) NOT NULL,
                           `comment` text NOT NULL,
                           `is_deleted` bit(1) NOT NULL DEFAULT b'0',
                           PRIMARY KEY (`id`),
                           KEY `student_id` (`student_id`),
                           CONSTRAINT `ratings_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `rating_courses`
--

DROP TABLE IF EXISTS `rating_courses`;


CREATE TABLE `rating_courses` (
                                  `id` int(11) NOT NULL,
                                  `course_id` int(11) NOT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `course_id` (`course_id`),
                                  CONSTRAINT `rating_courses_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
                                  CONSTRAINT `rating_courses_ibfk_2` FOREIGN KEY (`id`) REFERENCES `ratings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `rating_professors`
--

DROP TABLE IF EXISTS `rating_professors`;


CREATE TABLE `rating_professors` (
                                     `id` int(11) NOT NULL,
                                     `professor_id` int(11) NOT NULL,
                                     PRIMARY KEY (`id`),
                                     KEY `professor_id` (`professor_id`),
                                     CONSTRAINT `rating_professors_ibfk_1` FOREIGN KEY (`professor_id`) REFERENCES `professors` (`id`),
                                     CONSTRAINT `rating_professors_ibfk_2` FOREIGN KEY (`id`) REFERENCES `ratings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `contents`
--

DROP TABLE IF EXISTS `contents`;


CREATE TABLE `contents` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `title` varchar(100) NOT NULL,
                            `description` text DEFAULT NULL,
                            `content_type` varchar(100) NOT NULL,
                            `is_deleted` bit(1) NOT NULL DEFAULT b'0',
                            `course_id` int(11) NOT NULL,
                            PRIMARY KEY (`id`),
                            KEY `contents_courses_FK` (`course_id`),
                            CONSTRAINT `contents_courses_FK` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- Table structure for table `courses_contents`
--

DROP TABLE IF EXISTS `courses_contents`;


CREATE TABLE `courses_contents` (
                                    `course_id` int(11) NOT NULL,
                                    `content_id` int(11) NOT NULL,
                                    PRIMARY KEY (`course_id`,`content_id`),
                                    KEY `content_id` (`content_id`),
                                    CONSTRAINT `courses_contents_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
                                    CONSTRAINT `courses_contents_ibfk_2` FOREIGN KEY (`content_id`) REFERENCES `contents` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- Table structure for table `documents`
--

DROP TABLE IF EXISTS `documents`;


CREATE TABLE `documents` (
                             `id` int(11) NOT NULL,
                             `num_pages` int(11) NOT NULL,
                             `link` text NOT NULL,
                             PRIMARY KEY (`id`),
                             CONSTRAINT `documents_ibfk_1` FOREIGN KEY (`id`) REFERENCES `contents` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `quizzes`
--

DROP TABLE IF EXISTS `quizzes`;


CREATE TABLE `quizzes` (
                           `id` int(11) NOT NULL,
                           `time_limit` int(11) DEFAULT NULL,
                           `score_to_pass` int(11) DEFAULT NULL,
                           `max_score` int(11) NOT NULL,
                           PRIMARY KEY (`id`),
                           CONSTRAINT `quizzes_ibfk_1` FOREIGN KEY (`id`) REFERENCES `contents` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `videos`
--

DROP TABLE IF EXISTS `videos`;


CREATE TABLE `videos` (
                          `id` int(11) NOT NULL,
                          `link` text NOT NULL,
                          PRIMARY KEY (`id`),
                          CONSTRAINT `videos_ibfk_1` FOREIGN KEY (`id`) REFERENCES `contents` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;


CREATE TABLE `questions` (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `statement` text DEFAULT NULL,
                             `quiz_id` int(11) NOT NULL,
                             `is_deleted` bit(1) NOT NULL DEFAULT b'0',
                             PRIMARY KEY (`id`),
                             KEY `questions_ibfk_1` (`quiz_id`),
                             CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `answers`
--

DROP TABLE IF EXISTS `answers`;


CREATE TABLE `answers` (
                           `id` int(11) NOT NULL AUTO_INCREMENT,
                           `reply` text NOT NULL,
                           `is_correct` bit(1) DEFAULT b'0',
                           `question_id` int(11) NOT NULL,
                           `is_deleted` bit(1) NOT NULL DEFAULT b'0',
                           PRIMARY KEY (`id`),
                           KEY `question_id` (`question_id`),
                           CONSTRAINT `answers_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;


--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;


CREATE TABLE `flyway_schema_history` (
                                         `installed_rank` int(11) NOT NULL,
                                         `version` varchar(50) DEFAULT NULL,
                                         `description` varchar(200) NOT NULL,
                                         `type` varchar(20) NOT NULL,
                                         `script` varchar(1000) NOT NULL,
                                         `checksum` int(11) DEFAULT NULL,
                                         `installed_by` varchar(100) NOT NULL,
                                         `installed_on` timestamp NOT NULL DEFAULT current_timestamp(),
                                         `execution_time` int(11) NOT NULL,
                                         `success` tinyint(1) NOT NULL,
                                         PRIMARY KEY (`installed_rank`),
                                         KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- Table structure for table `announcements_classification_keywords`
--

CREATE TABLE `announcements_classification_keywords` (
                                                         `id` int(11) NOT NULL AUTO_INCREMENT,
                                                         `main_tags` text DEFAULT NULL,
                                                         `secondary_tags` text DEFAULT NULL,
                                                         `exclusion_tags` text DEFAULT NULL,
                                                         `public_examination_id` int(11) DEFAULT NULL,
                                                         PRIMARY KEY (`id`),
                                                         UNIQUE KEY `public_examination_id` (`public_examination_id`),
                                                         CONSTRAINT `announcements_classification_keywords_ibfk_1` FOREIGN KEY (`public_examination_id`) REFERENCES `public_examinations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

--
-- Table structure for table `refresh_tokens`
--

CREATE TABLE `refresh_tokens` (
                                  `id` int(11) NOT NULL AUTO_INCREMENT,
                                  `token` text NOT NULL,
                                  `expiry_date` timestamp NOT NULL,
                                  `revoked` tinyint(1) NOT NULL DEFAULT 0,
                                  `user_id` int(11) NOT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `refresh_tokens_users_FK` (`user_id`),
                                  CONSTRAINT `refresh_tokens_users_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;



--
-- Events
--
DELIMITER ;;
DROP EVENT IF EXISTS `call_announcements_deployment_afternoon`;;

CREATE DEFINER=`root`@`%` EVENT `call_announcements_deployment_afternoon`
    ON SCHEDULE EVERY 1 DAY
        STARTS '2025-09-08 14:35:00'
    ON COMPLETION PRESERVE
    ENABLE
    DO CALL deploy_announcements();;


DROP EVENT IF EXISTS `call_announcements_deployment_morning`;;

CREATE DEFINER=`root`@`%` EVENT `call_announcements_deployment_morning`
    ON SCHEDULE EVERY 1 DAY
        STARTS '2025-09-08 09:35:00'
    ON COMPLETION PRESERVE
    ENABLE
    DO CALL deploy_announcements();;


DROP EVENT IF EXISTS `clean_staging_announcements` ;;

CREATE DEFINER=`root`@`%` EVENT `clean_staging_announcements`
    ON SCHEDULE EVERY 1 DAY
        STARTS '2025-09-08 23:59:00'
    ON COMPLETION PRESERVE
    ENABLE
    DO TRUNCATE announcements_staging;;

DELIMITER ;

--
-- Routines
--

DELIMITER //
CREATE DEFINER=`root`@`%` PROCEDURE `deploy_announcements`()
BEGIN
    INSERT INTO announcements (title, content, related_links, publication_date, bulletin_board_id)
    SELECT s.title, s.content, s.related_links, s.publication_date, s.bulletin_board_id
    FROM announcements_staging s
    WHERE s.deployed = 0;

    -- Marcar como desplegados
    UPDATE announcements_staging
    SET deployed = 1
    WHERE deployed = 0;
END //
DELIMITER ;
