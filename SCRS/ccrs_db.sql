-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: ccrs_db
-- ------------------------------------------------------
-- Server version	8.0.41

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

--
-- Current Database: `ccrs_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `ccrs_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `ccrs_db`;

--
-- Table structure for table `course_prerequisites`
--

DROP TABLE IF EXISTS `course_prerequisites`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_prerequisites` (
  `course_id` bigint NOT NULL,
  `prerequisite_id` bigint NOT NULL,
  PRIMARY KEY (`course_id`,`prerequisite_id`),
  KEY `FK2w3n61668a1jqt1y4w7we9pn0` (`prerequisite_id`),
  CONSTRAINT `FK2w3n61668a1jqt1y4w7we9pn0` FOREIGN KEY (`prerequisite_id`) REFERENCES `courses` (`id`),
  CONSTRAINT `FKhh4f1avebuvlv54m3j3l3pp36` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_prerequisites`
--

LOCK TABLES `course_prerequisites` WRITE;
/*!40000 ALTER TABLE `course_prerequisites` DISABLE KEYS */;
/*!40000 ALTER TABLE `course_prerequisites` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `capacity` int NOT NULL,
  `code` varchar(50) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `credits` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `remaining_seats` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `department` varchar(100) NOT NULL,
  `level` varchar(20) NOT NULL,
  `fee` int NOT NULL,
  `duration_years` int DEFAULT NULL,
  `program_level` varchar(20) NOT NULL,
  `required_qualification` varchar(255) NOT NULL,
  `duration_semesters` int NOT NULL,
  `program_name` varchar(100) NOT NULL,
  `teaching_schema_id` bigint DEFAULT NULL,
  `batch_year` int NOT NULL,
  `required_document_types` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK61og8rbqdd2y28rx2et5fdnxd` (`code`),
  KEY `idx_course_code` (`code`),
  KEY `FKs7a8pebockawleiqkkk4q1m08` (`teaching_schema_id`),
  CONSTRAINT `FKs7a8pebockawleiqkkk4q1m08` FOREIGN KEY (`teaching_schema_id`) REFERENCES `teaching_schemas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=368 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES (350,120,'B-2026-002','2026-02-23 03:57:48.174931',3,'BBA',120,'2026-02-24 06:02:20.870623',2,'Management','UG',120000,NULL,'UG','12th pass (Commerce/Any stream) with minimum 50%',6,'BBA',4,2026,NULL),(351,120,'B-2026-003','2026-02-23 03:57:48.202707',3,'BCA',119,'2026-02-24 06:02:35.548275',3,'Computer Applications','UG',130000,NULL,'UG','12th pass with Mathematics/Computer Science (50%+)',6,'BCA',5,2026,NULL),(352,90,'B-2026-004','2026-02-23 03:57:48.203495',3,'BHM',90,'2026-02-24 06:02:47.455135',2,'Hospitality','UG',110000,NULL,'UG','12th pass (any stream) with minimum 45%',6,'BHM',6,2026,NULL),(353,180,'B-2026-005','2026-02-23 03:57:48.213265',4,'BTECH',180,'2026-02-24 06:02:59.627002',2,'Engineering','UG',220000,NULL,'UG','12th pass (PCM) with minimum 60%',8,'BTECH',7,2026,NULL),(354,180,'B-2026-006','2026-02-23 03:57:48.216598',4,'BTECH',180,'2026-02-24 06:03:30.273754',2,'Engineering','UG',210000,NULL,'UG','12th pass (PCM) with minimum 60%',8,'BTECH',8,2026,NULL),(355,120,'M-2026-001','2026-02-23 03:57:48.223543',3,'MBA',120,'2026-02-24 06:03:52.063840',2,'Management','PG',250000,NULL,'PG','Graduation in any discipline with minimum 50%',4,'MBA',9,2026,NULL),(356,120,'M-2026-002','2026-02-23 03:57:48.230467',3,'MCA',120,'2026-02-24 06:04:05.940255',2,'Computer Applications','PG',180000,NULL,'PG','Graduation with Mathematics/CS/IT (50%+)',4,'MCA',10,2026,NULL),(357,60,'M-2026-003','2026-02-23 03:57:48.237418',3,'MTECH',60,'2026-02-24 06:04:18.312910',2,'Engineering','PG',260000,NULL,'PG','B.Tech/BE in relevant branch (60%+)',4,'MTECH',11,2026,NULL),(367,180,'B-2026-001','2026-02-24 04:57:13.779155',5,'BCOM',180,'2026-02-24 05:49:08.309607',2,'Commerce','UG',250000,NULL,'UG','12th pass with minimum 45%',6,'BCOM',3,2026,'SSC_MARKSHEET,HSC_MARKSHEET,SCHOOL_LEAVING_CERTIFICATE,ID_PROOF,PASSPORT_PHOTO');
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `departments`
--

DROP TABLE IF EXISTS `departments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `departments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `name` varchar(100) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_department_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `departments`
--

LOCK TABLES `departments` WRITE;
/*!40000 ALTER TABLE `departments` DISABLE KEYS */;
INSERT INTO `departments` VALUES (1,_binary '','2026-02-18 14:42:12.297156','Computer Applications','2026-02-18 14:42:12.297156'),(2,_binary '','2026-02-18 14:42:12.331869','Engineering','2026-02-18 14:42:12.331869'),(3,_binary '','2026-02-18 14:42:12.341543','Management','2026-02-18 14:42:12.341543'),(4,_binary '','2026-02-18 14:42:12.345765','Hospitality','2026-02-18 14:42:12.345765'),(5,_binary '','2026-02-18 14:42:12.352690','Commerce','2026-02-18 14:42:12.352690'),(6,_binary '','2026-02-18 14:42:12.361318','Arts','2026-02-18 14:42:12.361318'),(7,_binary '','2026-02-18 14:42:12.366593','Science','2026-02-18 14:42:12.366593');
/*!40000 ALTER TABLE `departments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enrollment_documents`
--

DROP TABLE IF EXISTS `enrollment_documents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enrollment_documents` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content_type` varchar(100) DEFAULT NULL,
  `document_type` enum('ADDRESS_PROOF','BACHELOR_SEMESTER_MARKSHEET','CASTE_CERTIFICATE','DEGREE_CERTIFICATE','HSC_MARKSHEET','ID_PROOF','INCOME_CERTIFICATE','MARKSHEET','OTHER','PASSPORT_PHOTO','SCHOOL_LEAVING_CERTIFICATE','SSC_MARKSHEET','TRANSFER_CERTIFICATE') NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_path` varchar(500) NOT NULL,
  `uploaded_at` datetime(6) NOT NULL,
  `enrollment_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_enrollment_document_enrollment` (`enrollment_id`),
  CONSTRAINT `FKoy0nl02hw5nu1kfc7p0ug9y8e` FOREIGN KEY (`enrollment_id`) REFERENCES `enrollments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollment_documents`
--

LOCK TABLES `enrollment_documents` WRITE;
/*!40000 ALTER TABLE `enrollment_documents` DISABLE KEYS */;
INSERT INTO `enrollment_documents` VALUES (10,'application/pdf','SSC_MARKSHEET','EnactOn Technologies Private Limited_Project Manager_Gujrat (1) (1).pdf','uploads\\documents\\7f64772e-6f8b-4a1b-8a3f-083148f7b232_EnactOn Technologies Private Limited_Project Manager_Gujrat (1) (1).pdf','2026-02-23 04:05:07.985238',6),(11,'application/pdf','HSC_MARKSHEET','EnactOn Technologies Private Limited_Project Manager_Gujrat_application.pdf','uploads\\documents\\cdaff0c2-dd8d-461b-a6cc-85cb11e1fb9e_EnactOn Technologies Private Limited_Project Manager_Gujrat_application.pdf','2026-02-23 04:05:07.988277',6),(12,'application/pdf','SCHOOL_LEAVING_CERTIFICATE','Swarck Infolabs (1).pdf','uploads\\documents\\4353dcfe-e33b-4143-9f54-132ec29576aa_Swarck Infolabs (1).pdf','2026-02-23 04:05:07.988277',6);
/*!40000 ALTER TABLE `enrollment_documents` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enrollments`
--

DROP TABLE IF EXISTS `enrollments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `enrollments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `finalized_at` datetime(6) DEFAULT NULL,
  `registered_at` datetime(6) NOT NULL,
  `status` enum('APPROVED','CANCELLED','COMPLETED','ENROLLED','PENDING','WAITLISTED') NOT NULL,
  `course_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `comments` varchar(500) DEFAULT NULL,
  `marksheet_path` varchar(255) DEFAULT NULL,
  `past_education_marks` double DEFAULT NULL,
  `personal_info` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKi0g6mfijtuh199nj653nva6j5` (`student_id`,`course_id`),
  KEY `idx_enrollment_student` (`student_id`),
  KEY `idx_enrollment_course` (`course_id`),
  CONSTRAINT `FK2lha5vwilci2yi3vu5akusx4a` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKho8mcicp4196ebpltdn9wl6co` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enrollments`
--

LOCK TABLES `enrollments` WRITE;
/*!40000 ALTER TABLE `enrollments` DISABLE KEYS */;
INSERT INTO `enrollments` VALUES (6,NULL,'2026-02-23 04:05:07.958407','ENROLLED',351,1,'','uploads\\documents\\7f64772e-6f8b-4a1b-8a3f-083148f7b232_EnactOn Technologies Private Limited_Project Manager_Gujrat (1) (1).pdf',68.43,'{\"fullName\": \"kathan jigeshkumar bhavsar\", \"dob\": \"2004-01-07\", \"highestQualification\": \"12th commerce\", \"boardUniversity\": \"Gujarat board\", \"passingYear\": \"2024\"}');
/*!40000 ALTER TABLE `enrollments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `faculty_course_assignments`
--

DROP TABLE IF EXISTS `faculty_course_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `faculty_course_assignments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assigned_at` datetime(6) NOT NULL,
  `course_id` bigint NOT NULL,
  `faculty_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKmiu640016m6f2bywbpdy3v6uv` (`faculty_id`,`course_id`),
  KEY `FKce2wjykxaxra9mi9jft3u99ss` (`course_id`),
  CONSTRAINT `FKav536sitrq5kyfora96il7s7l` FOREIGN KEY (`faculty_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKce2wjykxaxra9mi9jft3u99ss` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `faculty_course_assignments`
--

LOCK TABLES `faculty_course_assignments` WRITE;
/*!40000 ALTER TABLE `faculty_course_assignments` DISABLE KEYS */;
/*!40000 ALTER TABLE `faculty_course_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `faculty_subject_assignments`
--

DROP TABLE IF EXISTS `faculty_subject_assignments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `faculty_subject_assignments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assigned_at` datetime(6) NOT NULL,
  `faculty_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_faculty_subject` (`faculty_id`,`subject_id`),
  KEY `FK2k8o2mijo595s2t7hdnw02out` (`subject_id`),
  CONSTRAINT `FK2k8o2mijo595s2t7hdnw02out` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`),
  CONSTRAINT `FKrdkm57qnr11m328wh3g70i0lk` FOREIGN KEY (`faculty_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `faculty_subject_assignments`
--

LOCK TABLES `faculty_subject_assignments` WRITE;
/*!40000 ALTER TABLE `faculty_subject_assignments` DISABLE KEYS */;
INSERT INTO `faculty_subject_assignments` VALUES (1,'2026-02-23 03:58:48.211874',4,2),(2,'2026-02-23 03:59:12.291160',4,5),(3,'2026-02-23 04:25:45.502049',4,11),(4,'2026-02-23 04:53:08.273844',4,12);
/*!40000 ALTER TABLE `faculty_subject_assignments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_structure_audit_logs`
--

DROP TABLE IF EXISTS `fee_structure_audit_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fee_structure_audit_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(20) NOT NULL,
  `change_summary` varchar(1000) NOT NULL,
  `changed_at` datetime(6) NOT NULL,
  `changed_by` varchar(255) NOT NULL,
  `fee_structure_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsxneou24lyjrwrjiyr1rtg6q0` (`fee_structure_id`),
  CONSTRAINT `FKsxneou24lyjrwrjiyr1rtg6q0` FOREIGN KEY (`fee_structure_id`) REFERENCES `fee_structures` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fee_structure_audit_logs`
--

LOCK TABLES `fee_structure_audit_logs` WRITE;
/*!40000 ALTER TABLE `fee_structure_audit_logs` DISABLE KEYS */;
INSERT INTO `fee_structure_audit_logs` VALUES (1,'UPDATE','name=Standard Semester Plan, perCredit=1800.00, lab=2500.00, differential=1500.00, latePenalty=750.00, active=true','2026-02-20 15:57:36.044372','staff@college.edu',1);
/*!40000 ALTER TABLE `fee_structure_audit_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fee_structures`
--

DROP TABLE IF EXISTS `fee_structures`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fee_structures` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `cost_per_credit` decimal(16,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `differential_fee` decimal(16,2) NOT NULL,
  `effective_from` date NOT NULL,
  `lab_fee` decimal(16,2) NOT NULL,
  `late_penalty` decimal(16,2) NOT NULL,
  `name` varchar(100) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fee_structures`
--

LOCK TABLES `fee_structures` WRITE;
/*!40000 ALTER TABLE `fee_structures` DISABLE KEYS */;
INSERT INTO `fee_structures` VALUES (1,_binary '',1800.00,'2026-02-20 06:43:09.247907',1500.00,'2026-02-20',2500.00,750.00,'Standard Semester Plan','2026-02-20 06:43:09.247907');
/*!40000 ALTER TABLE `fee_structures` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice_items`
--

DROP TABLE IF EXISTS `invoice_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoice_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(16,2) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `course_id` bigint DEFAULT NULL,
  `invoice_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_invoice_item_invoice` (`invoice_id`),
  KEY `FKo1lj2m5kg0814j44lgchc42i7` (`course_id`),
  CONSTRAINT `FK46ae0lhu1oqs7cv91fn6y9n7w` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`id`),
  CONSTRAINT `FKo1lj2m5kg0814j44lgchc42i7` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice_items`
--

LOCK TABLES `invoice_items` WRITE;
/*!40000 ALTER TABLE `invoice_items` DISABLE KEYS */;
INSERT INTO `invoice_items` VALUES (10,31066.67,'Semester 1 fee - BCA-2026-001',351,6),(11,31066.67,'Semester 2 fee - BCA-2026-001',351,7),(12,31066.67,'Semester 3 fee - BCA-2026-001',351,8);
/*!40000 ALTER TABLE `invoice_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoices`
--

DROP TABLE IF EXISTS `invoices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoices` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `due_date` datetime(6) DEFAULT NULL,
  `invoice_number` varchar(100) NOT NULL,
  `issued_at` datetime(6) NOT NULL,
  `status` enum('CANCELLED','DUE','PAID','PARTIAL') NOT NULL,
  `total_amount` decimal(16,2) NOT NULL,
  `student_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKl1x55mfsay7co0r3m9ynvipd5` (`invoice_number`),
  KEY `idx_invoice_number` (`invoice_number`),
  KEY `FKakkfiaesng8bjgonpf07bbp4p` (`student_id`),
  CONSTRAINT `FKakkfiaesng8bjgonpf07bbp4p` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices`
--

LOCK TABLES `invoices` WRITE;
/*!40000 ALTER TABLE `invoices` DISABLE KEYS */;
INSERT INTO `invoices` VALUES (6,'2026-03-05 04:05:47.929828','SEM-1-1-20260223093547','2026-02-23 04:05:47.929828','PAID',31066.67,1),(7,'2026-03-05 04:40:19.547627','SEM-2-1-20260223101019','2026-02-23 04:40:19.549625','PAID',31066.67,1),(8,'2026-03-05 04:41:42.906741','SEM-3-1-20260223101142','2026-02-23 04:41:42.906741','DUE',31066.67,1);
/*!40000 ALTER TABLE `invoices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `otp_verifications`
--

DROP TABLE IF EXISTS `otp_verifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `otp_verifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `expires_at` datetime(6) NOT NULL,
  `identifier` varchar(255) NOT NULL,
  `otp` varchar(6) NOT NULL,
  `otp_type` enum('EMAIL_VERIFICATION','FORGOT_PASSWORD','MOBILE_VERIFICATION') NOT NULL,
  `used` bit(1) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_otp_identifier` (`identifier`),
  KEY `idx_otp_expires` (`expires_at`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `otp_verifications`
--

LOCK TABLES `otp_verifications` WRITE;
/*!40000 ALTER TABLE `otp_verifications` DISABLE KEYS */;
INSERT INTO `otp_verifications` VALUES (1,'2026-01-28 05:21:03.886889','2026-01-28 05:31:03.885852','kathan7104@gmail.com','652035','EMAIL_VERIFICATION',_binary '\0',NULL),(2,'2026-01-28 05:21:03.888890','2026-01-28 05:31:03.888890','9265826450','057559','MOBILE_VERIFICATION',_binary '\0',NULL),(3,'2026-01-29 03:46:37.500525','2026-01-29 03:56:37.494308','kathan7104@gmail.com','557091','FORGOT_PASSWORD',_binary '\0',1),(4,'2026-01-29 04:06:46.514817','2026-01-29 04:16:46.507484','kathan7104@gmail.com','000902','FORGOT_PASSWORD',_binary '',1),(5,'2026-01-30 17:12:48.626344','2026-01-30 17:22:48.625344','amitabhavsar007@gmail.com','536946','EMAIL_VERIFICATION',_binary '',NULL),(6,'2026-01-30 17:12:53.663043','2026-01-30 17:22:53.663043','8690721985','638725','MOBILE_VERIFICATION',_binary '\0',NULL),(7,'2026-02-04 16:15:37.835820','2026-02-04 16:25:37.835820','jigesh310@gmail.com','971834','EMAIL_VERIFICATION',_binary '\0',NULL),(8,'2026-02-04 16:15:43.241721','2026-02-04 16:25:43.241721','7016297806','241826','MOBILE_VERIFICATION',_binary '\0',NULL),(9,'2026-02-04 16:24:34.139473','2026-02-04 16:34:34.139473','jigesh310@gmail.com','428401','EMAIL_VERIFICATION',_binary '\0',NULL),(10,'2026-02-04 16:24:38.790662','2026-02-04 16:34:38.790357','7016297806','103940','MOBILE_VERIFICATION',_binary '\0',NULL),(11,'2026-02-04 16:27:28.674034','2026-02-04 16:37:28.674034','jigesh310@gmail.com','283699','EMAIL_VERIFICATION',_binary '\0',NULL),(12,'2026-02-04 16:27:30.659318','2026-02-04 16:37:30.653179','jigesh310@gmail.com','083377','EMAIL_VERIFICATION',_binary '',NULL),(13,'2026-02-04 16:27:33.300411','2026-02-04 16:37:33.300411','7016297806','133580','MOBILE_VERIFICATION',_binary '\0',NULL),(14,'2026-02-04 16:27:35.086017','2026-02-04 16:37:35.086017','7016297806','065729','MOBILE_VERIFICATION',_binary '\0',NULL),(15,'2026-02-04 16:30:22.431242','2026-02-04 16:40:22.430049','7016297806','147551','MOBILE_VERIFICATION',_binary '\0',NULL);
/*!40000 ALTER TABLE `otp_verifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(16,2) NOT NULL,
  `method` enum('CARD','CASH','CHEQUE','ONLINE') NOT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `status` enum('FAILED','PENDING','SUCCESS') NOT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `invoice_id` bigint NOT NULL,
  `gateway_order_id` varchar(255) DEFAULT NULL,
  `gateway_signature` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_payment_tx` (`transaction_id`),
  KEY `FKrbqec6be74wab8iifh8g3i50i` (`invoice_id`),
  CONSTRAINT `FKrbqec6be74wab8iifh8g3i50i` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (4,31066.67,'ONLINE',NULL,'PENDING',NULL,6,'mock_order_7f07ae5400994e2699e885cda79bd540',NULL),(5,31066.67,'ONLINE',NULL,'PENDING',NULL,6,'mock_order_6a3bbeba22954558839da5e2bdee84d7',NULL),(6,31066.67,'ONLINE',NULL,'PENDING',NULL,6,'mock_order_cd7cf85e5aaa4495a91d1a8c1e59a9cf',NULL),(7,31066.67,'ONLINE','2026-02-23 04:24:39.462635','SUCCESS','mock_pay_c69056028a7f49dd81f3c7a08101ca28',6,'mock_order_15e48899a38f498da84b4c7325e9acb1','mock_signature_ok'),(8,31066.67,'ONLINE','2026-02-23 04:41:11.135475','SUCCESS','mock_upi_CICI_d49b6f62',7,'mock_order_d1960610c30545e6a7983f47d8fdc7fb','mock_signature_ok|mode=UPI|upi=kathan7104@okicici'),(9,31066.67,'ONLINE',NULL,'PENDING',NULL,8,'mock_order_b304ca8cc1b04019a580b170a5fd188f',NULL),(10,31066.67,'ONLINE',NULL,'PENDING',NULL,8,'mock_order_f6bf129fa49b4a05bc879eb763994282',NULL),(11,31066.67,'ONLINE',NULL,'PENDING',NULL,8,'mock_order_6a62216c07b84164b490b9ba3398ac80',NULL);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subjects`
--

DROP TABLE IF EXISTS `subjects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subjects` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `credits` int DEFAULT NULL,
  `department` varchar(100) NOT NULL,
  `program_name` varchar(100) NOT NULL,
  `semester` int DEFAULT NULL,
  `subject_code` varchar(50) NOT NULL,
  `subject_name` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `teaching_schema_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_subject_department_code` (`department`,`subject_code`),
  KEY `idx_subject_department` (`department`),
  KEY `FK4ln33p0sguy95jemlo3viq6b6` (`teaching_schema_id`),
  CONSTRAINT `FK4ln33p0sguy95jemlo3viq6b6` FOREIGN KEY (`teaching_schema_id`) REFERENCES `teaching_schemas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=208 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subjects`
--

LOCK TABLES `subjects` WRITE;
/*!40000 ALTER TABLE `subjects` DISABLE KEYS */;
INSERT INTO `subjects` VALUES (1,'2026-02-23 03:57:48.251609',4,'Computer Applications','BCA',1,'BCA-S1-101','Programming Fundamentals Credits:','2026-02-24 06:02:35.374854',5),(2,'2026-02-23 03:57:48.259367',3,'Computer Applications','BCA',1,'BCA-S1-102','Discrete Mathematics Credits:','2026-02-24 06:02:35.381661',5),(3,'2026-02-23 03:57:48.267229',3,'Computer Applications','BCA',1,'BCA-S1-103','Digital Computer Basics Credits:','2026-02-24 06:02:35.388753',5),(4,'2026-02-23 03:57:48.273229',4,'Computer Applications','BCA',2,'BCA-S2-201','Data Structures Credits:','2026-02-24 06:02:35.408179',5),(5,'2026-02-23 03:57:48.279700',4,'Computer Applications','BCA',2,'BCA-S2-202','Database Management Systems Credits:','2026-02-24 06:02:35.409618',5),(6,'2026-02-23 03:57:48.283304',3,'Computer Applications','BCA',2,'BCA-S2-203','Operating Systems Credits:','2026-02-24 06:02:35.416527',5),(7,'2026-02-23 03:57:48.286026',4,'Computer Applications','BCA',3,'BCA-S3-301','Java Programming Credits:','2026-02-24 06:02:35.437337',5),(8,'2026-02-23 03:57:48.292967',4,'Computer Applications','BCA',3,'BCA-S3-302','Web Technologies Credits:','2026-02-24 06:02:35.437337',5),(9,'2026-02-23 03:57:48.292967',4,'Computer Applications','MCA',1,'MCA-S1-101','Advanced Data Structures Credits:','2026-02-24 06:04:05.829378',10),(10,'2026-02-23 03:57:48.303090',4,'Computer Applications','MCA',1,'MCA-S1-102','Design and Analysis of Algorithms Credits:','2026-02-24 06:04:05.836315',10),(11,'2026-02-23 03:57:48.306849',4,'Computer Applications','MCA',1,'MCA-S1-103','Advanced DBMS Credits:','2026-02-24 06:04:05.842283',10),(12,'2026-02-23 03:57:48.313785',4,'Computer Applications','MCA',2,'MCA-S2-201','Cloud Computing Credits:','2026-02-24 06:04:05.859250',10),(13,'2026-02-23 03:57:48.313785',4,'Computer Applications','MCA',2,'MCA-S2-202','Machine Learning Basics Credits:','2026-02-24 06:04:05.864671',10),(14,'2026-02-23 03:57:48.323534',3,'Computer Applications','MCA',2,'MCA-S2-203','Software Project Management Credits:','2026-02-24 06:04:05.870085',10),(15,'2026-02-23 03:57:48.327668',3,'Management','BBA',1,'BBA-S1-101','Principles of Management Credits:','2026-02-24 06:02:20.690112',4),(16,'2026-02-23 03:57:48.334626',3,'Management','BBA',1,'BBA-S1-102','Business Communication Credits:','2026-02-24 06:02:20.697194',4),(17,'2026-02-23 03:57:48.334626',4,'Management','BBA',1,'BBA-S1-103','Financial Accounting Credits:','2026-02-24 06:02:20.706159',4),(18,'2026-02-23 03:57:48.343358',3,'Management','BBA',2,'BBA-S2-201','Human Resource Management Credits:','2026-02-24 06:02:20.724866',4),(19,'2026-02-23 03:57:48.348481',4,'Management','BBA',2,'BBA-S2-202','Marketing Management Credits:','2026-02-24 06:02:20.730876',4),(20,'2026-02-23 03:57:48.352834',4,'Management','BBA',2,'BBA-S2-203','Business Statistics Credits:','2026-02-24 06:02:20.734885',4),(21,'2026-02-23 03:57:48.355458',4,'Management','MBA',1,'MBA-S1-101','Managerial Economics Credits:','2026-02-24 06:03:51.969983',9),(22,'2026-02-23 03:57:48.355458',3,'Management','MBA',1,'MBA-S1-102','Organizational Behavior Credits:','2026-02-24 06:03:51.974905',9),(23,'2026-02-23 03:57:48.363197',4,'Management','MBA',1,'MBA-S1-103','Corporate Finance Credits:','2026-02-24 06:03:51.977968',9),(24,'2026-02-23 03:57:48.369336',4,'Management','MBA',2,'MBA-S2-201','Strategic Management Credits:','2026-02-24 06:03:51.998754',9),(25,'2026-02-23 03:57:48.373080',4,'Management','MBA',2,'MBA-S2-202','Business Analytics Credits:','2026-02-24 06:03:52.003751',9),(26,'2026-02-23 03:57:48.376265',3,'Management','MBA',2,'MBA-S2-203','Operations Strategy Credits:','2026-02-24 06:03:52.008759',9),(27,'2026-02-23 03:57:48.383198',4,'Engineering','BTECH',1,'BTECH-S1-101','Engineering Mathematics I Credits:','2026-02-24 06:29:22.208103',8),(28,'2026-02-23 03:57:48.383198',4,'Engineering','BTECH',1,'BTECH-S1-102','Engineering Physics Credits:','2026-02-24 06:29:22.216645',8),(29,'2026-02-23 03:57:48.391914',3,'Engineering','BTECH',1,'BTECH-S1-103','Basic Electrical Engineering Credits:','2026-02-24 06:29:22.220793',8),(30,'2026-02-23 03:57:48.392941',4,'Engineering','BTECH',2,'BTECH-S2-201','Data Structures and Algorithms Credits:','2026-02-24 06:29:22.248917',8),(31,'2026-02-23 03:57:48.397127',4,'Engineering','BTECH',2,'BTECH-S2-202','Object Oriented Programming Credits:','2026-02-24 06:29:22.254922',8),(32,'2026-02-23 03:57:48.404043',3,'Engineering','BTECH',2,'BTECH-S2-203','Computer Organization Credits:','2026-02-24 06:29:22.261331',8),(33,'2026-02-23 03:57:48.410989',3,'Engineering','MTECH',1,'MTECH-S1-101','Research Methodology Credits:','2026-02-24 06:04:18.239836',11),(34,'2026-02-23 03:57:48.413343',4,'Engineering','MTECH',1,'MTECH-S1-102','Advanced Computing Systems Credits:','2026-02-24 06:04:18.243441',11),(35,'2026-02-23 03:57:48.419942',4,'Engineering','MTECH',1,'MTECH-S1-103','High Performance Computing Credits:','2026-02-24 06:04:18.250479',11),(36,'2026-02-23 03:57:48.425115',4,'Engineering','MTECH',2,'MTECH-S2-201','Distributed Systems Credits:','2026-02-24 06:04:18.269966',11),(37,'2026-02-23 03:57:48.429331',4,'Engineering','MTECH',2,'MTECH-S2-202','AI for Engineers Credits:','2026-02-24 06:04:18.275476',11),(38,'2026-02-23 03:57:48.433073',2,'Engineering','MTECH',2,'MTECH-S2-203','Seminar and Review Credits:','2026-02-24 06:04:18.279916',11),(39,'2026-02-23 03:57:48.438773',3,'Hospitality','BHM',1,'BHM-S1-101','Front Office Operations Credits:','2026-02-24 06:02:47.267367',6),(40,'2026-02-23 03:57:48.442996',4,'Hospitality','BHM',1,'BHM-S1-102','Food Production Basics Credits:','2026-02-24 06:02:47.275063',6),(41,'2026-02-23 03:57:48.445699',3,'Hospitality','BHM',1,'BHM-S1-103','Hospitality Communication Credits:','2026-02-24 06:02:47.281581',6),(42,'2026-02-23 03:57:48.453364',3,'Hospitality','BHM',2,'BHM-S2-201','Housekeeping Management Credits:','2026-02-24 06:02:47.303177',6),(43,'2026-02-23 03:57:48.459605',4,'Hospitality','BHM',2,'BHM-S2-202','Food and Beverage Service Credits:','2026-02-24 06:02:47.309692',6),(44,'2026-02-23 03:57:48.463097',3,'Hospitality','BHM',2,'BHM-S2-203','Hospitality Marketing Credits:','2026-02-24 06:02:47.316073',6),(45,'2026-02-24 05:49:08.081167',4,'Commerce','BCOM',1,'BCOM-S1-101','Financial Accounting I Credits:','2026-02-24 05:49:08.081167',3),(46,'2026-02-24 05:49:08.091105',4,'Commerce','BCOM',1,'BCOM-S1-102','Business Economics I Credits:','2026-02-24 05:49:08.091105',3),(47,'2026-02-24 05:49:08.098045',4,'Commerce','BCOM',1,'BCOM-S1-103','Business Organization and Management Credits:','2026-02-24 05:49:08.099040',3),(48,'2026-02-24 05:49:08.102076',3,'Commerce','BCOM',1,'BCOM-S1-104','Business Communication Credits:','2026-02-24 05:49:08.102076',3),(49,'2026-02-24 05:49:08.110932',2,'Commerce','BCOM',1,'BCOM-S1-105','Environmental Studies Credits:','2026-02-24 05:49:08.110932',3),(50,'2026-02-24 05:49:08.120026',4,'Commerce','BCOM',2,'BCOM-S2-201','Financial Accounting II Credits:','2026-02-24 05:49:08.120026',3),(51,'2026-02-24 05:49:08.122813',4,'Commerce','BCOM',2,'BCOM-S2-202','Business Economics II Credits:','2026-02-24 05:49:08.122813',3),(52,'2026-02-24 05:49:08.131469',4,'Commerce','BCOM',2,'BCOM-S2-203','Corporate Accounting Credits:','2026-02-24 05:49:08.131469',3),(53,'2026-02-24 05:49:08.142636',3,'Commerce','BCOM',2,'BCOM-S2-204','Business Mathematics Credits:','2026-02-24 05:49:08.142636',3),(54,'2026-02-24 05:49:08.149594',3,'Commerce','BCOM',2,'BCOM-S2-205','Computer Applications in Business Credits:','2026-02-24 05:49:08.149594',3),(55,'2026-02-24 05:49:08.157320',4,'Commerce','BCOM',3,'BCOM-S3-301','Cost Accounting Credits:','2026-02-24 05:49:08.157320',3),(56,'2026-02-24 05:49:08.164906',4,'Commerce','BCOM',3,'BCOM-S3-302','Company Law Credits:','2026-02-24 05:49:08.164906',3),(57,'2026-02-24 05:49:08.173095',4,'Commerce','BCOM',3,'BCOM-S3-303','Income Tax Law and Practice Credits:','2026-02-24 05:49:08.173095',3),(58,'2026-02-24 05:49:08.180189',3,'Commerce','BCOM',3,'BCOM-S3-304','Banking and Insurance Credits:','2026-02-24 05:49:08.180189',3),(59,'2026-02-24 05:49:08.187700',3,'Commerce','BCOM',3,'BCOM-S3-305','Principles of Marketing Credits:','2026-02-24 05:49:08.187700',3),(60,'2026-02-24 05:49:08.196287',4,'Commerce','BCOM',4,'BCOM-S4-401','Management Accounting Credits:','2026-02-24 05:49:08.196287',3),(61,'2026-02-24 05:49:08.204806',4,'Commerce','BCOM',4,'BCOM-S4-402','Goods and Services Tax Credits:','2026-02-24 05:49:08.204806',3),(62,'2026-02-24 05:49:08.212088',4,'Commerce','BCOM',4,'BCOM-S4-403','Auditing Principles and Practice Credits:','2026-02-24 05:49:08.212088',3),(63,'2026-02-24 05:49:08.220009',3,'Commerce','BCOM',4,'BCOM-S4-404','Human Resource Management Credits:','2026-02-24 05:49:08.220009',3),(64,'2026-02-24 05:49:08.226946',3,'Commerce','BCOM',4,'BCOM-S4-405','Business Statistics Credits:','2026-02-24 05:49:08.226946',3),(65,'2026-02-24 05:49:08.233870',4,'Commerce','BCOM',5,'BCOM-S5-501','Financial Management Credits:','2026-02-24 05:49:08.233870',3),(66,'2026-02-24 05:49:08.244290',3,'Commerce','BCOM',5,'BCOM-S5-502','E Commerce Credits:','2026-02-24 05:49:08.244290',3),(67,'2026-02-24 05:49:08.250869',3,'Commerce','BCOM',5,'BCOM-S5-503','Entrepreneurship Development Credits:','2026-02-24 05:49:08.250869',3),(68,'2026-02-24 05:49:08.253743',4,'Commerce','BCOM',5,'BCOM-S5-504','International Business Credits:','2026-02-24 05:49:08.253743',3),(69,'2026-02-24 05:49:08.260924',3,'Commerce','BCOM',5,'BCOM-S5-505','Retail Management Credits:','2026-02-24 05:49:08.260924',3),(70,'2026-02-24 05:49:08.273145',4,'Commerce','BCOM',6,'BCOM-S6-601','Advanced Accounting Credits:','2026-02-24 05:49:08.273145',3),(71,'2026-02-24 05:49:08.279807',3,'Commerce','BCOM',6,'BCOM-S6-602','Business Research Methods Credits:','2026-02-24 05:49:08.279807',3),(72,'2026-02-24 05:49:08.282313',4,'Commerce','BCOM',6,'BCOM-S6-603','Strategic Management Credits:','2026-02-24 05:49:08.282313',3),(73,'2026-02-24 05:49:08.295321',4,'Commerce','BCOM',6,'BCOM-S6-604','Project Work Credits:','2026-02-24 05:49:08.295321',3),(74,'2026-02-24 05:49:08.301791',3,'Commerce','BCOM',6,'BCOM-S6-605','Elective Commerce Paper Credits:','2026-02-24 05:49:08.301791',3),(75,'2026-02-24 06:02:20.712057',3,'Management','BBA',1,'BBA-S1-104','Micro Economics Credits:','2026-02-24 06:02:20.712057',4),(76,'2026-02-24 06:02:20.719066',3,'Management','BBA',1,'BBA-S1-105','Business Mathematics Credits:','2026-02-24 06:02:20.719066',4),(77,'2026-02-24 06:02:20.738845',3,'Management','BBA',2,'BBA-S2-204','Macro Economics Credits:','2026-02-24 06:02:20.738845',4),(78,'2026-02-24 06:02:20.745588',2,'Management','BBA',2,'BBA-S2-205','Computer Applications Credits:','2026-02-24 06:02:20.745588',4),(79,'2026-02-24 06:02:20.752550',4,'Management','BBA',3,'BBA-S3-301','Operations Management Credits:','2026-02-24 06:02:20.752550',4),(80,'2026-02-24 06:02:20.759655',4,'Management','BBA',3,'BBA-S3-302','Cost Accounting Credits:','2026-02-24 06:02:20.759655',4),(81,'2026-02-24 06:02:20.766565',3,'Management','BBA',3,'BBA-S3-303','Business Law Credits:','2026-02-24 06:02:20.766565',4),(82,'2026-02-24 06:02:20.773523',3,'Management','BBA',3,'BBA-S3-304','Entrepreneurship Development Credits:','2026-02-24 06:02:20.773523',4),(83,'2026-02-24 06:02:20.779480',2,'Management','BBA',3,'BBA-S3-305','Elective I Credits:','2026-02-24 06:02:20.779480',4),(84,'2026-02-24 06:02:20.784991',4,'Management','BBA',4,'BBA-S4-401','Financial Management Credits:','2026-02-24 06:02:20.784991',4),(85,'2026-02-24 06:02:20.787304',3,'Management','BBA',4,'BBA-S4-402','Consumer Behavior Credits:','2026-02-24 06:02:20.787304',4),(86,'2026-02-24 06:02:20.795178',3,'Management','BBA',4,'BBA-S4-403','Supply Chain Management Credits:','2026-02-24 06:02:20.795178',4),(87,'2026-02-24 06:02:20.802614',3,'Management','BBA',4,'BBA-S4-404','Management Information Systems Credits:','2026-02-24 06:02:20.802614',4),(88,'2026-02-24 06:02:20.809074',2,'Management','BBA',4,'BBA-S4-405','Elective II Credits:','2026-02-24 06:02:20.809074',4),(89,'2026-02-24 06:02:20.815079',4,'Management','BBA',5,'BBA-S5-501','Strategic Management Credits:','2026-02-24 06:02:20.815079',4),(90,'2026-02-24 06:02:20.815079',4,'Management','BBA',5,'BBA-S5-502','International Business Credits:','2026-02-24 06:02:20.815079',4),(91,'2026-02-24 06:02:20.824993',3,'Management','BBA',5,'BBA-S5-503','Sales and Distribution Credits:','2026-02-24 06:02:20.824993',4),(92,'2026-02-24 06:02:20.828952',3,'Management','BBA',5,'BBA-S5-504','Business Analytics Credits:','2026-02-24 06:02:20.828952',4),(93,'2026-02-24 06:02:20.835844',2,'Management','BBA',5,'BBA-S5-505','Project I Credits:','2026-02-24 06:02:20.835844',4),(94,'2026-02-24 06:02:20.842954',4,'Management','BBA',6,'BBA-S6-601','Project II Credits:','2026-02-24 06:02:20.842954',4),(95,'2026-02-24 06:02:20.849882',4,'Management','BBA',6,'BBA-S6-602','Internship Credits:','2026-02-24 06:02:20.849882',4),(96,'2026-02-24 06:02:20.849882',2,'Management','BBA',6,'BBA-S6-603','Business Ethics Credits:','2026-02-24 06:02:20.855169',4),(97,'2026-02-24 06:02:20.856852',2,'Management','BBA',6,'BBA-S6-604','Corporate Governance Credits:','2026-02-24 06:02:20.856852',4),(98,'2026-02-24 06:02:20.865104',2,'Management','BBA',6,'BBA-S6-605','Seminar Credits:','2026-02-24 06:02:20.865104',4),(99,'2026-02-24 06:02:35.395707',2,'Computer Applications','BCA',1,'BCA-S1-104','Communication Skills Credits:','2026-02-24 06:02:35.395707',5),(100,'2026-02-24 06:02:35.401577',2,'Computer Applications','BCA',1,'BCA-S1-105','Environmental Studies Credits:','2026-02-24 06:02:35.401577',5),(101,'2026-02-24 06:02:35.424728',3,'Computer Applications','BCA',2,'BCA-S2-204','Object Oriented Concepts Credits:','2026-02-24 06:02:35.424728',5),(102,'2026-02-24 06:02:35.430178',2,'Computer Applications','BCA',2,'BCA-S2-205','Business Accounting Basics Credits:','2026-02-24 06:02:35.430178',5),(103,'2026-02-24 06:02:35.445306',3,'Computer Applications','BCA',3,'BCA-S3-303','Computer Networks Credits:','2026-02-24 06:02:35.445306',5),(104,'2026-02-24 06:02:35.451244',3,'Computer Applications','BCA',3,'BCA-S3-304','Software Engineering Credits:','2026-02-24 06:02:35.451244',5),(105,'2026-02-24 06:02:35.458139',2,'Computer Applications','BCA',3,'BCA-S3-305','Statistical Methods Credits:','2026-02-24 06:02:35.458139',5),(106,'2026-02-24 06:02:35.465081',4,'Computer Applications','BCA',4,'BCA-S4-401','Python Programming Credits:','2026-02-24 06:02:35.465081',5),(107,'2026-02-24 06:02:35.465081',4,'Computer Applications','BCA',4,'BCA-S4-402','Advanced DBMS Credits:','2026-02-24 06:02:35.465081',5),(108,'2026-02-24 06:02:35.474990',3,'Computer Applications','BCA',4,'BCA-S4-403','Mobile Application Development Credits:','2026-02-24 06:02:35.474990',5),(109,'2026-02-24 06:02:35.478825',3,'Computer Applications','BCA',4,'BCA-S4-404','Cloud Fundamentals Credits:','2026-02-24 06:02:35.478825',5),(110,'2026-02-24 06:02:35.485951',2,'Computer Applications','BCA',4,'BCA-S4-405','Aptitude and Reasoning Credits:','2026-02-24 06:02:35.485951',5),(111,'2026-02-24 06:02:35.494922',4,'Computer Applications','BCA',5,'BCA-S5-501','Data Analytics Credits:','2026-02-24 06:02:35.494922',5),(112,'2026-02-24 06:02:35.500334',3,'Computer Applications','BCA',5,'BCA-S5-502','Machine Learning Basics Credits:','2026-02-24 06:02:35.500334',5),(113,'2026-02-24 06:02:35.506673',3,'Computer Applications','BCA',5,'BCA-S5-503','Information Security Credits:','2026-02-24 06:02:35.506673',5),(114,'2026-02-24 06:02:35.506673',3,'Computer Applications','BCA',5,'BCA-S5-504','Internet of Things Credits:','2026-02-24 06:02:35.506673',5),(115,'2026-02-24 06:02:35.514733',2,'Computer Applications','BCA',5,'BCA-S5-505','Elective I Credits:','2026-02-24 06:02:35.514733',5),(116,'2026-02-24 06:02:35.521496',6,'Computer Applications','BCA',6,'BCA-S6-601','Project Work Credits:','2026-02-24 06:02:35.521496',5),(117,'2026-02-24 06:02:35.527572',3,'Computer Applications','BCA',6,'BCA-S6-602','Software Testing Credits:','2026-02-24 06:02:35.527572',5),(118,'2026-02-24 06:02:35.535023',3,'Computer Applications','BCA',6,'BCA-S6-603','DevOps Basics Credits:','2026-02-24 06:02:35.535023',5),(119,'2026-02-24 06:02:35.535023',4,'Computer Applications','BCA',6,'BCA-S6-604','Internship Credits:','2026-02-24 06:02:35.540483',5),(120,'2026-02-24 06:02:35.544841',2,'Computer Applications','BCA',6,'BCA-S6-605','Elective II Credits:','2026-02-24 06:02:35.544841',5),(121,'2026-02-24 06:02:47.289591',3,'Hospitality','BHM',1,'BHM-S1-104','Housekeeping Fundamentals Credits:','2026-02-24 06:02:47.289591',6),(122,'2026-02-24 06:02:47.297164',2,'Hospitality','BHM',1,'BHM-S1-105','Nutrition and Hygiene Credits:','2026-02-24 06:02:47.297164',6),(123,'2026-02-24 06:02:47.321055',3,'Hospitality','BHM',2,'BHM-S2-204','Hotel Accounting Credits:','2026-02-24 06:02:47.321055',6),(124,'2026-02-24 06:02:47.324767',2,'Hospitality','BHM',2,'BHM-S2-205','Computer Applications Credits:','2026-02-24 06:02:47.324767',6),(125,'2026-02-24 06:02:47.335073',4,'Hospitality','BHM',3,'BHM-S3-301','Food Production Advanced Credits:','2026-02-24 06:02:47.335073',6),(126,'2026-02-24 06:02:47.339866',3,'Hospitality','BHM',3,'BHM-S3-302','Accommodation Operations Credits:','2026-02-24 06:02:47.339866',6),(127,'2026-02-24 06:02:47.345165',3,'Hospitality','BHM',3,'BHM-S3-303','Customer Relationship Management Credits:','2026-02-24 06:02:47.345165',6),(128,'2026-02-24 06:02:47.354136',3,'Hospitality','BHM',3,'BHM-S3-304','Travel and Tourism Basics Credits:','2026-02-24 06:02:47.354136',6),(129,'2026-02-24 06:02:47.357958',2,'Hospitality','BHM',3,'BHM-S3-305','Event Management Credits:','2026-02-24 06:02:47.357958',6),(130,'2026-02-24 06:02:47.364916',3,'Hospitality','BHM',4,'BHM-S4-401','Hotel Facility Planning Credits:','2026-02-24 06:02:47.364916',6),(131,'2026-02-24 06:02:47.371549',3,'Hospitality','BHM',4,'BHM-S4-402','Hospitality HRM Credits:','2026-02-24 06:02:47.371549',6),(132,'2026-02-24 06:02:47.378460',3,'Hospitality','BHM',4,'BHM-S4-403','Hospitality Law Credits:','2026-02-24 06:02:47.378460',6),(133,'2026-02-24 06:02:47.385681',3,'Hospitality','BHM',4,'BHM-S4-404','Bakery and Confectionery Credits:','2026-02-24 06:02:47.385681',6),(134,'2026-02-24 06:02:47.395020',2,'Hospitality','BHM',4,'BHM-S4-405','Elective I Credits:','2026-02-24 06:02:47.395020',6),(135,'2026-02-24 06:02:47.399278',4,'Hospitality','BHM',5,'BHM-S5-501','Strategic Hospitality Management Credits:','2026-02-24 06:02:47.399278',6),(136,'2026-02-24 06:02:47.406228',3,'Hospitality','BHM',5,'BHM-S5-502','International Hospitality Credits:','2026-02-24 06:02:47.406228',6),(137,'2026-02-24 06:02:47.415170',3,'Hospitality','BHM',5,'BHM-S5-503','Revenue Management Credits:','2026-02-24 06:02:47.415170',6),(138,'2026-02-24 06:02:47.421182',4,'Hospitality','BHM',5,'BHM-S5-504','Internship I Credits:','2026-02-24 06:02:47.421182',6),(139,'2026-02-24 06:02:47.427411',2,'Hospitality','BHM',5,'BHM-S5-505','Elective II Credits:','2026-02-24 06:02:47.427411',6),(140,'2026-02-24 06:02:47.427411',6,'Hospitality','BHM',6,'BHM-S6-601','Internship II Credits:','2026-02-24 06:02:47.427411',6),(141,'2026-02-24 06:02:47.435263',4,'Hospitality','BHM',6,'BHM-S6-602','Project Work Credits:','2026-02-24 06:02:47.435263',6),(142,'2026-02-24 06:02:47.444713',2,'Hospitality','BHM',6,'BHM-S6-603','Seminar Credits:','2026-02-24 06:02:47.444713',6),(143,'2026-02-24 06:02:47.447881',2,'Hospitality','BHM',6,'BHM-S6-604','Hospitality Entrepreneurship Credits:','2026-02-24 06:02:47.447881',6),(144,'2026-02-24 06:02:59.438569',3,'Engineering','BTECH',1,'BTECH-S1-104','Engineering Graphics Credits:','2026-02-24 06:29:22.232661',8),(145,'2026-02-24 06:02:59.443927',2,'Engineering','BTECH',1,'BTECH-S1-105','Communication Skills Credits:','2026-02-24 06:29:22.240556',8),(146,'2026-02-24 06:02:59.464827',4,'Engineering','BTECH',2,'BTECH-S2-204','Engineering Mathematics II Credits:','2026-02-24 06:29:22.269141',8),(147,'2026-02-24 06:02:59.467313',2,'Engineering','BTECH',2,'BTECH-S2-205','Environmental Studies Credits:','2026-02-24 06:29:22.278010',8),(148,'2026-02-24 06:02:59.474705',4,'Engineering','BTECH',3,'BTECH-S3-301','Database Management Systems Credits:','2026-02-24 06:29:22.287946',8),(149,'2026-02-24 06:02:59.481158',4,'Engineering','BTECH',3,'BTECH-S3-302','Operating Systems Credits:','2026-02-24 06:29:22.296112',8),(150,'2026-02-24 06:02:59.489258',3,'Engineering','BTECH',3,'BTECH-S3-303','Software Engineering Credits:','2026-02-24 06:29:22.301412',8),(151,'2026-02-24 06:02:59.494946',3,'Engineering','BTECH',3,'BTECH-S3-304','Probability and Statistics Credits:','2026-02-24 06:29:22.307843',8),(152,'2026-02-24 06:02:59.501252',2,'Engineering','BTECH',3,'BTECH-S3-305','Mini Project I Credits:','2026-02-24 06:29:22.318192',8),(153,'2026-02-24 06:02:59.504963',4,'Engineering','BTECH',4,'BTECH-S4-401','Computer Networks Credits:','2026-02-24 06:29:22.324190',8),(154,'2026-02-24 06:02:59.509346',4,'Engineering','BTECH',4,'BTECH-S4-402','Theory of Computation Credits:','2026-02-24 06:29:22.331125',8),(155,'2026-02-24 06:02:59.515804',3,'Engineering','BTECH',4,'BTECH-S4-403','Web Technologies Credits:','2026-02-24 06:29:22.337707',8),(156,'2026-02-24 06:02:59.521914',3,'Engineering','BTECH',4,'BTECH-S4-404','Microprocessors Credits:','2026-02-24 06:29:22.347653',8),(157,'2026-02-24 06:02:59.524960',2,'Engineering','BTECH',4,'BTECH-S4-405','Mini Project II Credits:','2026-02-24 06:29:22.351600',8),(158,'2026-02-24 06:02:59.529714',4,'Engineering','BTECH',5,'BTECH-S5-501','Artificial Intelligence Credits:','2026-02-24 06:29:22.358881',8),(159,'2026-02-24 06:02:59.536712',4,'Engineering','BTECH',5,'BTECH-S5-502','Machine Learning Credits:','2026-02-24 06:29:22.367743',8),(160,'2026-02-24 06:02:59.536712',3,'Engineering','BTECH',5,'BTECH-S5-503','Cloud Computing Credits:','2026-02-24 06:29:22.372440',8),(161,'2026-02-24 06:02:59.544743',3,'Engineering','BTECH',5,'BTECH-S5-504','Compiler Design Credits:','2026-02-24 06:29:22.379371',8),(162,'2026-02-24 06:02:59.550557',2,'Engineering','BTECH',5,'BTECH-S5-505','Elective I Credits:','2026-02-24 06:29:22.388001',8),(163,'2026-02-24 06:02:59.557566',4,'Engineering','BTECH',6,'BTECH-S6-601','Big Data Analytics Credits:','2026-02-24 06:29:22.397965',8),(164,'2026-02-24 06:02:59.563563',3,'Engineering','BTECH',6,'BTECH-S6-602','Internet of Things Credits:','2026-02-24 06:29:22.401991',8),(165,'2026-02-24 06:02:59.564976',3,'Engineering','BTECH',6,'BTECH-S6-603','DevOps Credits:','2026-02-24 06:29:22.414093',8),(166,'2026-02-24 06:02:59.571444',3,'Engineering','BTECH',6,'BTECH-S6-604','Information Security Credits:','2026-02-24 06:29:22.421049',8),(167,'2026-02-24 06:02:59.578345',2,'Engineering','BTECH',6,'BTECH-S6-605','Elective II Credits:','2026-02-24 06:29:22.428222',8),(168,'2026-02-24 06:02:59.584376',4,'Engineering','BTECH',7,'BTECH-S7-701','Distributed Systems Credits:','2026-02-24 06:29:22.434969',8),(169,'2026-02-24 06:02:59.585306',3,'Engineering','BTECH',7,'BTECH-S7-702','Mobile Application Development Credits:','2026-02-24 06:29:22.441893',8),(170,'2026-02-24 06:02:59.595006',3,'Engineering','BTECH',7,'BTECH-S7-703','Data Mining Credits:','2026-02-24 06:29:22.449208',8),(171,'2026-02-24 06:02:59.600333',4,'Engineering','BTECH',7,'BTECH-S7-704','Project Phase I Credits:','2026-02-24 06:29:22.458026',8),(172,'2026-02-24 06:02:59.605851',2,'Engineering','BTECH',7,'BTECH-S7-705','Elective III Credits:','2026-02-24 06:29:22.462683',8),(173,'2026-02-24 06:02:59.610844',8,'Engineering','BTECH',8,'BTECH-S8-801','Project Phase II Credits:','2026-02-24 06:29:22.469640',8),(174,'2026-02-24 06:02:59.614840',4,'Engineering','BTECH',8,'BTECH-S8-802','Internship Credits:','2026-02-24 06:29:22.477781',8),(175,'2026-02-24 06:02:59.620061',2,'Engineering','BTECH',8,'BTECH-S8-803','Seminar and Viva Credits:','2026-02-24 06:29:22.487640',8),(176,'2026-02-24 06:03:51.984880',3,'Management','MBA',1,'MBA-S1-104','Quantitative Techniques Credits:','2026-02-24 06:03:51.984880',9),(177,'2026-02-24 06:03:51.991829',2,'Management','MBA',1,'MBA-S1-105','Business Communication Credits:','2026-02-24 06:03:51.991829',9),(178,'2026-02-24 06:03:52.012689',3,'Management','MBA',2,'MBA-S2-204','Marketing Strategy Credits:','2026-02-24 06:03:52.012689',9),(179,'2026-02-24 06:03:52.019621',2,'Management','MBA',2,'MBA-S2-205','Elective I Credits:','2026-02-24 06:03:52.019621',9),(180,'2026-02-24 06:03:52.024796',3,'Management','MBA',3,'MBA-S3-301','Leadership and Change Credits:','2026-02-24 06:03:52.024796',9),(181,'2026-02-24 06:03:52.026527',4,'Management','MBA',3,'MBA-S3-302','International Finance Credits:','2026-02-24 06:03:52.026527',9),(182,'2026-02-24 06:03:52.034954',3,'Management','MBA',3,'MBA-S3-303','Digital Marketing Credits:','2026-02-24 06:03:52.034954',9),(183,'2026-02-24 06:03:52.039406',3,'Management','MBA',3,'MBA-S3-304','HR Analytics Credits:','2026-02-24 06:03:52.039406',9),(184,'2026-02-24 06:03:52.044842',2,'Management','MBA',3,'MBA-S3-305','Elective II Credits:','2026-02-24 06:03:52.044842',9),(185,'2026-02-24 06:03:52.047404',6,'Management','MBA',4,'MBA-S4-401','Capstone Project Credits:','2026-02-24 06:03:52.047404',9),(186,'2026-02-24 06:03:52.054834',4,'Management','MBA',4,'MBA-S4-402','Internship Credits:','2026-02-24 06:03:52.054834',9),(187,'2026-02-24 06:03:52.059837',2,'Management','MBA',4,'MBA-S4-403','Seminar and Viva Credits:','2026-02-24 06:03:52.059837',9),(188,'2026-02-24 06:04:05.844957',3,'Computer Applications','MCA',1,'MCA-S1-104','Research Methodology Credits:','2026-02-24 06:04:05.844957',10),(189,'2026-02-24 06:04:05.853255',2,'Computer Applications','MCA',1,'MCA-S1-105','Professional Communication Credits:','2026-02-24 06:04:05.853255',10),(190,'2026-02-24 06:04:05.874813',3,'Computer Applications','MCA',2,'MCA-S2-204','Cyber Security Credits:','2026-02-24 06:04:05.874813',10),(191,'2026-02-24 06:04:05.877981',2,'Computer Applications','MCA',2,'MCA-S2-205','Elective I Credits:','2026-02-24 06:04:05.877981',10),(192,'2026-02-24 06:04:05.884838',4,'Computer Applications','MCA',3,'MCA-S3-301','Big Data Analytics Credits:','2026-02-24 06:04:05.884838',10),(193,'2026-02-24 06:04:05.890856',4,'Computer Applications','MCA',3,'MCA-S3-302','Deep Learning Fundamentals Credits:','2026-02-24 06:04:05.890856',10),(194,'2026-02-24 06:04:05.894762',3,'Computer Applications','MCA',3,'MCA-S3-303','Enterprise Java Credits:','2026-02-24 06:04:05.894762',10),(195,'2026-02-24 06:04:05.898840',3,'Computer Applications','MCA',3,'MCA-S3-304','Microservices Architecture Credits:','2026-02-24 06:04:05.898840',10),(196,'2026-02-24 06:04:05.905750',2,'Computer Applications','MCA',3,'MCA-S3-305','Elective II Credits:','2026-02-24 06:04:05.905750',10),(197,'2026-02-24 06:04:05.921894',8,'Computer Applications','MCA',4,'MCA-S4-401','Major Project Credits:','2026-02-24 06:04:05.921894',10),(198,'2026-02-24 06:04:05.926497',4,'Computer Applications','MCA',4,'MCA-S4-402','Internship Credits:','2026-02-24 06:04:05.926497',10),(199,'2026-02-24 06:04:05.934890',2,'Computer Applications','MCA',4,'MCA-S4-403','Seminar and Viva Credits:','2026-02-24 06:04:05.934890',10),(200,'2026-02-24 06:04:18.257548',3,'Engineering','MTECH',1,'MTECH-S1-104','Mathematical Foundations Credits:','2026-02-24 06:04:18.257548',11),(201,'2026-02-24 06:04:18.263513',2,'Engineering','MTECH',1,'MTECH-S1-105','Technical Writing Credits:','2026-02-24 06:04:18.263513',11),(202,'2026-02-24 06:04:18.285006',3,'Engineering','MTECH',2,'MTECH-S2-204','Cloud Native Architecture Credits:','2026-02-24 06:04:18.285006',11),(203,'2026-02-24 06:04:18.290021',2,'Engineering','MTECH',2,'MTECH-S2-205','Elective I Credits:','2026-02-24 06:04:18.290021',11),(204,'2026-02-24 06:04:18.292077',8,'Engineering','MTECH',3,'MTECH-S3-301','Thesis Phase I Credits:','2026-02-24 06:04:18.292077',11),(205,'2026-02-24 06:04:18.299624',3,'Engineering','MTECH',3,'MTECH-S3-302','Advanced Elective Credits:','2026-02-24 06:04:18.299624',11),(206,'2026-02-24 06:04:18.299624',10,'Engineering','MTECH',4,'MTECH-S4-401','Thesis Phase II Credits:','2026-02-24 06:04:18.299624',11),(207,'2026-02-24 06:04:18.309798',2,'Engineering','MTECH',4,'MTECH-S4-402','Viva Voce Credits:','2026-02-24 06:04:18.309798',11);
/*!40000 ALTER TABLE `subjects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teaching_schemas`
--

DROP TABLE IF EXISTS `teaching_schemas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teaching_schemas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `department` varchar(100) NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_path` varchar(500) NOT NULL,
  `program_name` varchar(100) NOT NULL,
  `schema_version` int NOT NULL,
  `uploaded_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_teaching_schema_department_program` (`department`,`program_name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teaching_schemas`
--

LOCK TABLES `teaching_schemas` WRITE;
/*!40000 ALTER TABLE `teaching_schemas` DISABLE KEYS */;
INSERT INTO `teaching_schemas` VALUES (1,'Commerce','BCOM_Teaching_Schema.docx','uploads\\teaching-schemas\\f809768e-fb2c-482f-8f95-b16eb1447759_BCOM_Teaching_Schema.docx','BCOM',1,'2026-02-24 03:46:12.267893'),(2,'Commerce','f809768e-fb2c-482f-8f95-b16eb1447759_BCOM_Teaching_Schema.docx','D:\\proj\\CCRS\\SCRS\\uploads\\teaching-schemas\\f809768e-fb2c-482f-8f95-b16eb1447759_BCOM_Teaching_Schema.docx','BCOM',2,'2026-02-24 05:18:49.790562'),(3,'Commerce','BCOM_Teaching_Schema_PROPER.docx','D:\\proj\\CCRS\\SCRS\\uploads\\teaching-schemas\\af7fdbaa-e4ca-458e-86fd-48d58280a059_BCOM_Teaching_Schema_PROPER.docx','BCOM',3,'2026-02-24 05:49:08.032550'),(4,'Management','BBA_Teaching_Schema.docx','D:\\proj\\CCRS\\SCRS\\uploads\\teaching-schemas\\cd9c16ad-f81a-4819-97f1-1defa6e460bd_BBA_Teaching_Schema.docx','BBA',1,'2026-02-24 06:02:20.676309'),(5,'Computer Applications','BCA_Teaching_Schema.docx','D:\\proj\\CCRS\\SCRS\\uploads\\teaching-schemas\\136b45ad-5a9a-4bf2-ac1e-4d96ad3a6498_BCA_Teaching_Schema.docx','BCA',1,'2026-02-24 06:02:35.367758'),(6,'Hospitality','BHM_Teaching_Schema.docx','D:\\proj\\CCRS\\SCRS\\uploads\\teaching-schemas\\69089e12-0e03-4722-8ff3-cd3961546fed_BHM_Teaching_Schema.docx','BHM',1,'2026-02-24 06:02:47.260448'),(7,'Engineering','BTECH_Teaching_Schema.docx','D:\\proj\\CCRS\\SCRS\\uploads\\teaching-schemas\\374d8b40-28a2-45b3-8770-3717b181b5e7_BTECH_Teaching_Schema.docx','BTECH',1,'2026-02-24 06:02:59.411799'),(8,'Engineering','BTECH_Teaching_Schema.docx','D:\\proj\\CCRS\\SCRS\\uploads\\teaching-schemas\\dc2cd98a-15f2-431d-b092-f1c9f068570b_BTECH_Teaching_Schema.docx','BTECH',2,'2026-02-24 06:03:30.065519'),(9,'Management','MBA_Teaching_Schema.docx','D:\\proj\\CCRS\\SCRS\\uploads\\teaching-schemas\\c9467c04-d7be-40f6-ab96-0830080feb24_MBA_Teaching_Schema.docx','MBA',1,'2026-02-24 06:03:51.956946'),(10,'Computer Applications','MCA_Teaching_Schema.docx','D:\\proj\\CCRS\\SCRS\\uploads\\teaching-schemas\\89f8b777-ed99-427a-baa9-41f3b4e96190_MCA_Teaching_Schema.docx','MCA',1,'2026-02-24 06:04:05.808768'),(11,'Engineering','MTECH_Teaching_Schema.docx','D:\\proj\\CCRS\\SCRS\\uploads\\teaching-schemas\\1a74d2dd-69e3-49f7-868d-0c127d30674c_MTECH_Teaching_Schema.docx','MTECH',1,'2026-02-24 06:04:18.233755');
/*!40000 ALTER TABLE `teaching_schemas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  KEY `FKhfh9dx7w3ubf1co1vdev94g3f` (`user_id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (1,'STUDENT'),(2,'AUTHORITY_DIRECTOR'),(3,'AUTHORITY_ADMIN'),(4,'AUTHORITY_FACULTY'),(5,'STUDENT'),(8,'STUDENT'),(10,'AUTHORITY_STAFF');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_active` bit(1) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `email_verified` bit(1) DEFAULT NULL,
  `full_name` varchar(100) NOT NULL,
  `mobile_number` varchar(10) NOT NULL,
  `mobile_verified` bit(1) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `department` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_email` (`email`),
  UNIQUE KEY `idx_user_mobile` (`mobile_number`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,_binary '','2026-01-28 05:21:03.851473','kathan7104@gmail.com',_binary '\0','kathan jigeshkumar bhavsar','9265826450',_binary '\0','$2a$10$C4mzXBoHKIe1E49zGGUsCeRZC072wleZgyr5SJJShP8JtXlzcHue.','2026-02-20 17:38:44.730472','Computer Applications'),(2,_binary '','2026-01-30 11:39:05.855932','director@college.edu',_binary '','College Director (DEMO)','9000000001',_binary '','$2a$10$AQ29Mj0vQC4FZ6stuatsse3IEafItZuRjcdqG1Pm/oUW/7u974H9m','2026-02-24 05:50:40.340994','Computer Applications'),(3,_binary '','2026-01-30 11:39:05.991138','admin@college.edu',_binary '','College Admin (DEMO)','9000000002',_binary '','$2a$10$jjfMbU.h7.9xoKdqHAyFiO1FhJmgsSaybXxQVHsOVDuIOnsXYEeXq','2026-01-30 11:39:05.991138','General'),(4,_binary '','2026-01-30 11:39:06.056063','faculty@college.edu',_binary '','Faculty Demo (DEMO)','9000000003',_binary '','$2a$10$csOq3cip8i6/R6ImOBDpcu3uAGQJeS1lSFqkneRopbCHc6xT5382e','2026-02-23 03:57:48.553322','Computer Applications'),(5,_binary '','2026-01-30 17:12:48.596243','amitabhavsar007@gmail.com',_binary '','idrish hushenbhai barad','8690721985',_binary '\0','$2a$10$pFZ8de7Ud13eVFdfZQ5Uo.CWrZy7JVnh9KNPvforDbkmxXLCFG0K6','2026-01-30 17:14:43.943209','General'),(8,_binary '','2026-02-04 16:24:34.132115','jigesh310@gmail.com',_binary '','jigesh ramanlal bhavsar','7016297806',_binary '\0','$2a$10$DedoJg8Y8Drzs60n/39lt.OXzihM6IRV2nGsXzL7BqbAuttzJ.f.u','2026-02-04 16:30:53.828900',NULL),(10,_binary '','2026-02-16 03:38:18.783649','staff@college.edu',_binary '','Account Staff (DEMO)','9000000004',_binary '','$2a$10$pCLBPhonUJO..L67P99nTOAW9zLt8bHYujBu2nU8thtzD7Z3DbgHS','2026-02-16 03:38:18.783649','Accounts');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'ccrs_db'
--

--
-- Dumping routines for database 'ccrs_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-24 17:51:11
