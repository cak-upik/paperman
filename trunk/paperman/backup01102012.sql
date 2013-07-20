-- MySQL dump 10.11
--
-- Host: localhost    Database: papermandb
-- ------------------------------------------------------
-- Server version	5.0.51b-community-nt-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `kendaraan`
--

DROP TABLE IF EXISTS `kendaraan`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `kendaraan` (
  `id` int(11) NOT NULL auto_increment,
  `keterangan` varchar(255) default NULL,
  `noLambung` int(11) NOT NULL,
  `noMesin` varchar(255) default NULL,
  `noPolisi` varchar(255) default NULL,
  `noRangka` varchar(255) default NULL,
  `noSTNK` varchar(255) default NULL,
  `tglJatuhTempo` date default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `noLambung` (`noLambung`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `kendaraan`
--

LOCK TABLES `kendaraan` WRITE;
/*!40000 ALTER TABLE `kendaraan` DISABLE KEYS */;
INSERT INTO `kendaraan` VALUES (1,' ',101,'91384','2938429348','234929384KW','9283J2JW0','2012-10-11');
/*!40000 ALTER TABLE `kendaraan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kendaraan_pth`
--

DROP TABLE IF EXISTS `kendaraan_pth`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `kendaraan_pth` (
  `id_pth` int(11) NOT NULL auto_increment,
  `keterangan` varchar(255) default NULL,
  `noLambung` int(11) NOT NULL,
  `noMesin` varchar(255) default NULL,
  `noPolisi` varchar(255) default NULL,
  `noRangka` varchar(255) default NULL,
  `noSTNK` varchar(255) default NULL,
  `tglJatuhTempo` date default NULL,
  PRIMARY KEY  (`id_pth`),
  UNIQUE KEY `noLambung` (`noLambung`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `kendaraan_pth`
--

LOCK TABLES `kendaraan_pth` WRITE;
/*!40000 ALTER TABLE `kendaraan_pth` DISABLE KEYS */;
/*!40000 ALTER TABLE `kendaraan_pth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `number_factory`
--

DROP TABLE IF EXISTS `number_factory`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `number_factory` (
  `id` int(11) NOT NULL,
  `ArmadaType` varchar(255) default NULL,
  `TransactionType` varchar(255) default NULL,
  `last_number` int(11) default NULL,
  `tanggal` date default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `number_factory`
--

LOCK TABLES `number_factory` WRITE;
/*!40000 ALTER TABLE `number_factory` DISABLE KEYS */;
INSERT INTO `number_factory` VALUES (1,'BIRU','SETORAN',15,'2012-10-15');
/*!40000 ALTER TABLE `number_factory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pengemudi`
--

DROP TABLE IF EXISTS `pengemudi`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `pengemudi` (
  `id` int(11) NOT NULL auto_increment,
  `alamat` varchar(255) default NULL,
  `kontrakBulan` int(11) default NULL,
  `kontrakHari` int(11) default NULL,
  `kota` varchar(255) default NULL,
  `nama` varchar(255) default NULL,
  `nrp` varchar(255) NOT NULL,
  `tglMasuk` date default NULL,
  `id_ref_kendaraan` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `nrp` (`nrp`),
  UNIQUE KEY `id_ref_kendaraan` (`id_ref_kendaraan`),
  KEY `FK4A3A786418B9B4E5` (`id_ref_kendaraan`),
  CONSTRAINT `FK4A3A786418B9B4E5` FOREIGN KEY (`id_ref_kendaraan`) REFERENCES `kendaraan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `pengemudi`
--

LOCK TABLES `pengemudi` WRITE;
/*!40000 ALTER TABLE `pengemudi` DISABLE KEYS */;
INSERT INTO `pengemudi` VALUES (1,'RUNGKUT',20,100,'SURABAYA','TAUFIK','2120398','2012-10-11',1);
/*!40000 ALTER TABLE `pengemudi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pengemudi_pth`
--

DROP TABLE IF EXISTS `pengemudi_pth`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `pengemudi_pth` (
  `id_pth` int(11) NOT NULL auto_increment,
  `alamat` varchar(255) default NULL,
  `kontrakBulan` int(11) default NULL,
  `kontrakHari` int(11) default NULL,
  `kota` varchar(255) default NULL,
  `nama` varchar(255) default NULL,
  `nrp` varchar(255) NOT NULL,
  `tglMasuk` date default NULL,
  `id_ref_kendaraan` int(11) NOT NULL,
  PRIMARY KEY  (`id_pth`),
  UNIQUE KEY `nrp` (`nrp`),
  UNIQUE KEY `id_ref_kendaraan` (`id_ref_kendaraan`),
  KEY `FK3DEC8889F07B86A3` (`id_ref_kendaraan`),
  CONSTRAINT `FK3DEC8889F07B86A3` FOREIGN KEY (`id_ref_kendaraan`) REFERENCES `kendaraan_pth` (`id_pth`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `pengemudi_pth`
--

LOCK TABLES `pengemudi_pth` WRITE;
/*!40000 ALTER TABLE `pengemudi_pth` DISABLE KEYS */;
/*!40000 ALTER TABLE `pengemudi_pth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sistem`
--

DROP TABLE IF EXISTS `sistem`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `sistem` (
  `id` int(11) NOT NULL,
  `alamat_perusahaan` varchar(255) default NULL,
  `nilai_angsuran` decimal(19,2) default NULL,
  `email` varchar(255) default NULL,
  `kota` varchar(255) default NULL,
  `nama_perusahaan` varchar(255) default NULL,
  `nilai_tabungan` decimal(19,2) default NULL,
  `no_telp` varchar(255) default NULL,
  `tglKerja` date default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `sistem`
--

LOCK TABLES `sistem` WRITE;
/*!40000 ALTER TABLE `sistem` DISABLE KEYS */;
INSERT INTO `sistem` VALUES (1,'KENDANGSARI','150000.00','GAMA.GMAIL.COM','SURABAYA','PT MANDALA SATATA GAMA','25000.00','031938494','2012-10-15');
/*!40000 ALTER TABLE `sistem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tr_closing_bulanan`
--

DROP TABLE IF EXISTS `tr_closing_bulanan`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tr_closing_bulanan` (
  `id` int(11) NOT NULL auto_increment,
  `periodeBulan` date default NULL,
  `tglClosing` date default NULL,
  `total_angsuran` decimal(19,2) default '0.00',
  `total_bayar` decimal(19,2) default '0.00',
  `total_cicilan` decimal(19,2) default '0.00',
  `total_kasbon` decimal(19,2) default '0.00',
  `total_overtime` decimal(19,2) default '0.00',
  `total_setoran` decimal(19,2) default '0.00',
  `total_tabungan` decimal(19,2) default '0.00',
  `user_set` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `tr_closing_bulanan`
--

LOCK TABLES `tr_closing_bulanan` WRITE;
/*!40000 ALTER TABLE `tr_closing_bulanan` DISABLE KEYS */;
INSERT INTO `tr_closing_bulanan` VALUES (7,'2012-10-01','2012-10-12','300000.00','0.00','0.00','175000.00','0.00','175000.00','50000.00',NULL),(8,'2012-10-01','2012-10-12','600000.00','175000.00','0.00','175000.00','0.00','700000.00','100000.00',NULL),(9,'2012-11-01','2012-11-01','150000.00','0.00','0.00','0.00','0.00','175000.00','25000.00',NULL),(10,'2012-10-01','2012-10-13','750000.00','175000.00','0.00','175000.00','0.00','875000.00','125000.00',NULL),(11,'2012-10-01','2012-10-13','1050000.00','350000.00','0.00','350000.00','0.00','1225000.00','175000.00',NULL),(12,'2012-10-01','2012-10-13','1350000.00','350000.00','0.00','350000.00','0.00','1575000.00','225000.00',NULL),(13,'2012-10-01','2012-10-14','1500000.00','350000.00','0.00','350000.00','0.00','1750000.00','250000.00',NULL),(14,'2012-10-01','2012-10-14','1650000.00','350000.00','0.00','350000.00','0.00','1925000.00','275000.00',NULL),(15,'2012-10-01','2012-10-15','1800000.00','350000.00','0.00','350000.00','0.00','2100000.00','300000.00',NULL),(16,'2012-10-01','2012-10-15','1950000.00','350000.00','0.00','350000.00','0.00','2275000.00','325000.00',NULL),(17,'2012-10-01','2012-10-15','2100000.00','350000.00','0.00','350000.00','0.00','2450000.00','350000.00',NULL);
/*!40000 ALTER TABLE `tr_closing_bulanan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tr_closing_tahunan`
--

DROP TABLE IF EXISTS `tr_closing_tahunan`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tr_closing_tahunan` (
  `id` int(11) NOT NULL auto_increment,
  `periodeAkhir` date default NULL,
  `periodeAwal` date default NULL,
  `tglClosing` date default NULL,
  `total_angsuran` decimal(19,2) default '0.00',
  `total_bayar` decimal(19,2) default '0.00',
  `total_cicilan` decimal(19,2) default '0.00',
  `total_kasbon` decimal(19,2) default '0.00',
  `total_overtime` decimal(19,2) default '0.00',
  `total_setoran` decimal(19,2) default '0.00',
  `total_tabungan` decimal(19,2) default '0.00',
  `user_set` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `tr_closing_tahunan`
--

LOCK TABLES `tr_closing_tahunan` WRITE;
/*!40000 ALTER TABLE `tr_closing_tahunan` DISABLE KEYS */;
/*!40000 ALTER TABLE `tr_closing_tahunan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tr_setoran`
--

DROP TABLE IF EXISTS `tr_setoran`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tr_setoran` (
  `id` int(11) NOT NULL auto_increment,
  `closing_status` varchar(255) default NULL,
  `setoran_counter` int(11) default NULL,
  `jalan_state` varchar(255) default NULL,
  `kode_setoran` varchar(255) NOT NULL,
  `tr_status` varchar(255) default NULL,
  `tglJatuhTempo` date default NULL,
  `tglSPO` date default NULL,
  `tglSetoran` date default NULL,
  `total_hutang` decimal(19,2) default NULL,
  `total_setoran` decimal(19,2) default NULL,
  `kode_user` varchar(255) default NULL,
  `id_closing` int(11) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `kode_setoran` (`kode_setoran`),
  KEY `FKBAAD3FF162283D34` (`id_closing`),
  CONSTRAINT `FKBAAD3FF162283D34` FOREIGN KEY (`id_closing`) REFERENCES `tr_closing_bulanan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `tr_setoran`
--

LOCK TABLES `tr_setoran` WRITE;
/*!40000 ALTER TABLE `tr_setoran` DISABLE KEYS */;
INSERT INTO `tr_setoran` VALUES (1,'CLOSED',1,'J','111012-SET-B-1','L','2012-10-11','2012-10-11','2012-10-11','0.00','175000.00',NULL,17),(2,'CLOSED',2,'J','111012-SET-B-2','L','2012-10-12','2012-10-11','2012-10-11','175000.00','0.00',NULL,17),(3,'CLOSED',3,'J','121012-SET-B-3','L','2012-10-13','2012-10-12','2012-10-12','0.00','350000.00',NULL,17),(4,'CLOSED',4,'J','121012-SET-B-4','L','2012-10-14','2012-10-12','2012-10-12','0.00','175000.00',NULL,17),(5,'AVAILABLE',5,'J','011112-SET-B-5','L','2012-10-15','2012-11-01','2012-11-01','0.00','175000.00',NULL,9),(6,'CLOSED',6,'J','131012-SET-B-6','L','2012-10-16','2012-10-13','2012-10-13','0.00','175000.00',NULL,17),(7,'CLOSED',7,'M','131012-SET-B-7','L','2012-10-17','2012-10-13','2012-10-13','175000.00','0.00',NULL,17),(8,'CLOSED',8,'J','131012-SET-B-8','L','2012-10-17','2012-10-13','2012-10-13','0.00','350000.00',NULL,17),(9,'CLOSED',9,'J','131012-SET-B-9','L','2012-10-18','2012-10-13','2012-10-13','0.00','175000.00',NULL,17),(10,'CLOSED',10,'J','131012-SET-B-10','L','2012-10-19','2012-10-13','2012-10-13','0.00','175000.00',NULL,17),(11,'CLOSED',11,'J','131012-SET-B-11','L','2012-10-20','2012-10-13','2012-10-13','0.00','175000.00',NULL,17),(12,'CLOSED',12,'J','141012-SET-B-12','L','2012-10-21','2012-10-14','2012-10-14','0.00','175000.00',NULL,17),(13,'CLOSED',13,'M','161012-SET-B-13','L','2012-10-22','2012-10-16','2012-10-16','0.00','175000.00',NULL,17),(14,'CLOSED',14,'J','151012-SET-B-14','L','2012-10-22','2012-10-15','2012-10-15','0.00','175000.00',NULL,17),(15,'CLOSED',15,'M','111012-SET-B-15','L','2012-10-23','2012-10-11','2012-10-11','0.00','175000.00',NULL,17);
/*!40000 ALTER TABLE `tr_setoran` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tr_setoran_detail`
--

DROP TABLE IF EXISTS `tr_setoran_detail`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tr_setoran_detail` (
  `id` int(11) NOT NULL auto_increment,
  `cicilanOther` decimal(19,2) default NULL,
  `angsuran` decimal(19,2) default NULL,
  `pembayaran` decimal(19,2) default NULL,
  `kasbon` decimal(19,2) default NULL,
  `keterangan` varchar(255) default NULL,
  `over_time` decimal(19,2) default NULL,
  `tabungan` decimal(19,2) default NULL,
  `id_pengemudi` int(11) NOT NULL,
  `id_kendaraan` int(11) NOT NULL,
  `id_setoran` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FKDF93345F6786AB91` (`id_kendaraan`),
  KEY `FKDF93345F3CDCDC7F` (`id_pengemudi`),
  KEY `FKDF93345FE2C73EDB` (`id_setoran`),
  CONSTRAINT `FKDF93345F3CDCDC7F` FOREIGN KEY (`id_pengemudi`) REFERENCES `pengemudi` (`id`),
  CONSTRAINT `FKDF93345F6786AB91` FOREIGN KEY (`id_kendaraan`) REFERENCES `kendaraan` (`id`),
  CONSTRAINT `FKDF93345FE2C73EDB` FOREIGN KEY (`id_setoran`) REFERENCES `tr_setoran` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `tr_setoran_detail`
--

LOCK TABLES `tr_setoran_detail` WRITE;
/*!40000 ALTER TABLE `tr_setoran_detail` DISABLE KEYS */;
INSERT INTO `tr_setoran_detail` VALUES (1,'0.00','150000.00','0.00','0.00',' ','0.00','25000.00',1,1,1),(2,'0.00','150000.00','0.00','175000.00',' ','0.00','25000.00',1,1,2),(3,'0.00','150000.00','175000.00','0.00',' ','0.00','25000.00',1,1,3),(4,'0.00','150000.00','0.00','0.00',' ','0.00','25000.00',1,1,4),(5,'0.00','150000.00','0.00','0.00',' ','0.00','25000.00',1,1,5),(6,'0.00','150000.00','0.00','0.00',' ','0.00','25000.00',1,1,6),(7,'0.00','150000.00','0.00','175000.00',' ','0.00','25000.00',1,1,7),(8,'0.00','150000.00','175000.00','0.00',' ','0.00','25000.00',1,1,8),(9,'0.00','150000.00','0.00','0.00',' ','0.00','25000.00',1,1,9),(10,'0.00','150000.00','0.00','0.00',' ','0.00','25000.00',1,1,10),(11,'0.00','150000.00','0.00','0.00',' ','0.00','25000.00',1,1,11),(12,'0.00','150000.00','0.00','0.00',' ','0.00','25000.00',1,1,12),(13,'0.00','150000.00','0.00','0.00',' ','0.00','25000.00',1,1,13),(14,'0.00','150000.00','0.00','0.00',' ','0.00','25000.00',1,1,14),(15,'0.00','150000.00','0.00','0.00',' ','0.00','25000.00',1,1,15);
/*!40000 ALTER TABLE `tr_setoran_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tr_setoran_detail_pth`
--

DROP TABLE IF EXISTS `tr_setoran_detail_pth`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tr_setoran_detail_pth` (
  `id_detail_pth` int(11) NOT NULL auto_increment,
  `cicilanOther` decimal(19,2) default NULL,
  `angsuran` decimal(19,2) default NULL,
  `pembayaran` decimal(19,2) default NULL,
  `kasbon` decimal(19,2) default NULL,
  `keterangan` varchar(255) default NULL,
  `over_time` decimal(19,2) default NULL,
  `tabungan` decimal(19,2) default NULL,
  `id_pengemudi_pth` int(11) NOT NULL,
  `id_kendaraan_pth` int(11) NOT NULL,
  `id_setoran_pth` int(11) NOT NULL,
  PRIMARY KEY  (`id_detail_pth`),
  KEY `FKC040CF049F1D52F4` (`id_kendaraan_pth`),
  KEY `FKC040CF045C9DF1D4` (`id_pengemudi_pth`),
  KEY `FKC040CF049AA45FD4` (`id_setoran_pth`),
  CONSTRAINT `FKC040CF045C9DF1D4` FOREIGN KEY (`id_pengemudi_pth`) REFERENCES `pengemudi_pth` (`id_pth`),
  CONSTRAINT `FKC040CF049AA45FD4` FOREIGN KEY (`id_setoran_pth`) REFERENCES `tr_setoran_pth` (`idSetr_pth`),
  CONSTRAINT `FKC040CF049F1D52F4` FOREIGN KEY (`id_kendaraan_pth`) REFERENCES `kendaraan_pth` (`id_pth`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `tr_setoran_detail_pth`
--

LOCK TABLES `tr_setoran_detail_pth` WRITE;
/*!40000 ALTER TABLE `tr_setoran_detail_pth` DISABLE KEYS */;
/*!40000 ALTER TABLE `tr_setoran_detail_pth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tr_setoran_pth`
--

DROP TABLE IF EXISTS `tr_setoran_pth`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `tr_setoran_pth` (
  `idSetr_pth` int(11) NOT NULL auto_increment,
  `setoran_counter` int(11) default NULL,
  `jalan_status` varchar(255) default NULL,
  `kode_setoran_pth` varchar(255) NOT NULL,
  `tr_status` varchar(255) default NULL,
  `tglJatuhTempo` date default NULL,
  `tglSPO` date default NULL,
  `tglSetoran` date default NULL,
  `total_hutang` decimal(19,2) default NULL,
  `total_setoran` decimal(19,2) default NULL,
  `kode_user` varchar(255) default NULL,
  PRIMARY KEY  (`idSetr_pth`),
  UNIQUE KEY `kode_setoran_pth` (`kode_setoran_pth`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;

--
-- Dumping data for table `tr_setoran_pth`
--

LOCK TABLES `tr_setoran_pth` WRITE;
/*!40000 ALTER TABLE `tr_setoran_pth` DISABLE KEYS */;
/*!40000 ALTER TABLE `tr_setoran_pth` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-10-15  2:06:28
