/*
Navicat MySQL Data Transfer

Source Server         : Ifmstory
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : piggy

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2019-05-01 21:39:41
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `accounts`
-- ----------------------------
DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`name`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`password`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`salt`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`2ndpassword`  varchar(134) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`salt2`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`loggedin`  tinyint(1) UNSIGNED NOT NULL DEFAULT 0 ,
`lastlogin`  timestamp NULL DEFAULT NULL ,
`createdat`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`birthday`  date NOT NULL DEFAULT '0000-00-00' ,
`banned`  tinyint(1) NOT NULL DEFAULT 0 ,
`banreason`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`gm`  tinyint(1) NOT NULL DEFAULT 0 ,
`email`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`macs`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`tempban`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`greason`  tinyint(4) UNSIGNED NULL DEFAULT NULL ,
`ACash`  int(11) NOT NULL DEFAULT 0 ,
`mPoints`  int(11) NOT NULL DEFAULT 0 ,
`gender`  tinyint(1) UNSIGNED NOT NULL DEFAULT 0 ,
`SessionIP`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`points`  int(11) NOT NULL DEFAULT 0 ,
`vpoints`  int(11) NOT NULL DEFAULT 0 ,
`monthvotes`  int(11) NOT NULL DEFAULT 0 ,
`totalvotes`  int(11) NOT NULL DEFAULT 0 ,
`lastvote`  int(11) NOT NULL DEFAULT 0 ,
`lastvote2`  int(11) NOT NULL DEFAULT 0 ,
`lastlogon`  timestamp NULL DEFAULT NULL ,
`lastvoteip`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`webadmin`  int(1) NULL DEFAULT 0 ,
`rebirths`  int(11) NOT NULL DEFAULT 0 ,
`ip`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`mainchar`  int(6) NOT NULL DEFAULT 0 ,
`nxcredit`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`nxprepaid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`redeemhn`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
UNIQUE INDEX `name` (`name`) USING BTREE ,
INDEX `ranking1` (`id`, `banned`, `gm`) USING BTREE ,
INDEX `id` (`id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=869

;

-- ----------------------------
-- Table structure for `accounts_event`
-- ----------------------------
DROP TABLE IF EXISTS `accounts_event`;
CREATE TABLE `accounts_event` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`accId`  int(11) NOT NULL DEFAULT 0 ,
`eventId`  varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`count`  int(11) NOT NULL DEFAULT 0 ,
`type`  int(11) NOT NULL DEFAULT 0 ,
`updateTime`  timestamp NULL DEFAULT NULL ,
PRIMARY KEY (`id`),
INDEX `accid` (`accId`) USING BTREE ,
INDEX `eventid` (`eventId`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=latin1 COLLATE=latin1_swedish_ci
AUTO_INCREMENT=41569

;

-- ----------------------------
-- Table structure for `achievements`
-- ----------------------------
DROP TABLE IF EXISTS `achievements`;
CREATE TABLE `achievements` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`achievementid`  int(9) UNSIGNED NOT NULL DEFAULT 0 ,
`characterid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`accountid`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `achievementid` (`achievementid`) USING BTREE ,
INDEX `accountid` (`accountid`) USING BTREE ,
INDEX `characterid` (`characterid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=71368101

;

-- ----------------------------
-- Table structure for `alliances`
-- ----------------------------
DROP TABLE IF EXISTS `alliances`;
CREATE TABLE `alliances` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`name`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`leaderid`  int(11) UNSIGNED NOT NULL ,
`guild1`  int(11) NOT NULL ,
`guild2`  int(11) NOT NULL ,
`guild3`  int(11) NOT NULL DEFAULT 0 ,
`guild4`  int(11) NOT NULL DEFAULT 0 ,
`guild5`  int(11) NOT NULL DEFAULT 0 ,
`rank1`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'Master' ,
`rank2`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'Jr.Master' ,
`rank3`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'Member' ,
`rank4`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'Member' ,
`rank5`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'Member' ,
`capacity`  int(11) NOT NULL DEFAULT 2 ,
`notice`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
PRIMARY KEY (`id`),
FOREIGN KEY (`leaderid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
UNIQUE INDEX `name` (`name`) USING BTREE ,
INDEX `id` (`id`) USING BTREE ,
INDEX `leaderid` (`leaderid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `androids`
-- ----------------------------
DROP TABLE IF EXISTS `androids`;
CREATE TABLE `androids` (
`uniqueid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`name`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'Android' ,
`hair`  int(11) NOT NULL DEFAULT 0 ,
`face`  int(11) NOT NULL DEFAULT 0 ,
`skin`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`uniqueid`),
INDEX `uniqueid` (`uniqueid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=156466

;

-- ----------------------------
-- Table structure for `bbs_replies`
-- ----------------------------
DROP TABLE IF EXISTS `bbs_replies`;
CREATE TABLE `bbs_replies` (
`replyid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`threadid`  int(11) UNSIGNED NOT NULL ,
`postercid`  int(11) UNSIGNED NOT NULL ,
`timestamp`  bigint(20) UNSIGNED NOT NULL ,
`content`  varchar(26) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`guildid`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`replyid`),
FOREIGN KEY (`postercid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `bbs_replies_ibfk_1` (`postercid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=26

;

-- ----------------------------
-- Table structure for `bbs_threads`
-- ----------------------------
DROP TABLE IF EXISTS `bbs_threads`;
CREATE TABLE `bbs_threads` (
`threadid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`postercid`  int(11) UNSIGNED NOT NULL ,
`name`  varchar(26) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`timestamp`  bigint(20) UNSIGNED NOT NULL ,
`icon`  smallint(5) UNSIGNED NOT NULL ,
`startpost`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`guildid`  int(11) UNSIGNED NOT NULL ,
`localthreadid`  int(11) UNSIGNED NOT NULL ,
PRIMARY KEY (`threadid`),
FOREIGN KEY (`postercid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `bbs_threads_ibfk_1` (`postercid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=168

;

-- ----------------------------
-- Table structure for `bosslog`
-- ----------------------------
DROP TABLE IF EXISTS `bosslog`;
CREATE TABLE `bosslog` (
`bosslogid`  int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(10) UNSIGNED NOT NULL ,
`bossid`  varchar(20) CHARACTER SET big5 COLLATE big5_chinese_ci NOT NULL ,
`lastattempt`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
PRIMARY KEY (`bosslogid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=big5 COLLATE=big5_chinese_ci
AUTO_INCREMENT=37393

;

-- ----------------------------
-- Table structure for `buddies`
-- ----------------------------
DROP TABLE IF EXISTS `buddies`;
CREATE TABLE `buddies` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NOT NULL ,
`buddyid`  int(11) NOT NULL ,
`pending`  tinyint(4) NOT NULL DEFAULT 0 ,
`groupname`  varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'ETC' ,
PRIMARY KEY (`id`),
FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `buddies_ibfk_1` (`characterid`) USING BTREE ,
INDEX `buddyid` (`buddyid`) USING BTREE ,
INDEX `id` (`id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=133817

;

-- ----------------------------
-- Table structure for `cashshop_limit_sell`
-- ----------------------------
DROP TABLE IF EXISTS `cashshop_limit_sell`;
CREATE TABLE `cashshop_limit_sell` (
`serial`  int(11) NOT NULL ,
`amount`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`serial`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `cashshop_modified_items`
-- ----------------------------
DROP TABLE IF EXISTS `cashshop_modified_items`;
CREATE TABLE `cashshop_modified_items` (
`name`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`serial`  int(11) NOT NULL ,
`discount_price`  int(11) NOT NULL DEFAULT '-1' ,
`mark`  tinyint(1) NOT NULL DEFAULT '-1' ,
`showup`  tinyint(1) NOT NULL DEFAULT 0 ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
`priority`  tinyint(3) NOT NULL DEFAULT 0 ,
`package`  tinyint(1) NOT NULL DEFAULT 0 ,
`period`  tinyint(3) NOT NULL DEFAULT 0 ,
`gender`  tinyint(1) NOT NULL DEFAULT 0 ,
`count`  tinyint(3) NOT NULL DEFAULT 0 ,
`meso`  int(11) NOT NULL DEFAULT 0 ,
`unk_1`  tinyint(1) NOT NULL DEFAULT 0 ,
`unk_2`  tinyint(1) NOT NULL DEFAULT 0 ,
`unk_3`  tinyint(1) NOT NULL DEFAULT 0 ,
`extra_flags`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`serial`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `character_cards`
-- ----------------------------
DROP TABLE IF EXISTS `character_cards`;
CREATE TABLE `character_cards` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`accountid`  int(11) UNSIGNED NOT NULL ,
`worldid`  int(11) NOT NULL ,
`characterid`  int(11) NOT NULL ,
`position`  int(11) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`accountid`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `id` (`id`) USING BTREE ,
INDEX `character_cards_ibfk_1` (`accountid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `character_slots`
-- ----------------------------
DROP TABLE IF EXISTS `character_slots`;
CREATE TABLE `character_slots` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`accountid`  int(11) UNSIGNED NOT NULL ,
`worldid`  int(11) NOT NULL DEFAULT 0 ,
`charslots`  int(11) NOT NULL DEFAULT 6 ,
PRIMARY KEY (`id`),
FOREIGN KEY (`accountid`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `id` (`id`) USING BTREE ,
INDEX `character_slots_ibfk_1` (`accountid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=741

;

-- ----------------------------
-- Table structure for `characters`
-- ----------------------------
DROP TABLE IF EXISTS `characters`;
CREATE TABLE `characters` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`accountid`  int(11) UNSIGNED NOT NULL ,
`world`  tinyint(1) NOT NULL DEFAULT 0 ,
`name`  varchar(18) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`level`  int(3) UNSIGNED NOT NULL DEFAULT 0 ,
`exp`  int(11) NOT NULL DEFAULT 0 ,
`str`  int(5) NOT NULL DEFAULT 0 ,
`dex`  int(5) NOT NULL DEFAULT 0 ,
`luk`  int(5) NOT NULL DEFAULT 0 ,
`int`  int(5) NOT NULL DEFAULT 0 ,
`hp`  int(5) NOT NULL DEFAULT 0 ,
`mp`  int(5) NOT NULL DEFAULT 0 ,
`maxhp`  int(5) NOT NULL DEFAULT 0 ,
`maxmp`  int(5) NOT NULL DEFAULT 0 ,
`meso`  int(11) NOT NULL DEFAULT 0 ,
`hpApUsed`  int(5) NOT NULL DEFAULT 0 ,
`job`  int(5) NOT NULL DEFAULT 0 ,
`skincolor`  tinyint(1) NOT NULL DEFAULT 0 ,
`gender`  tinyint(1) NOT NULL DEFAULT 0 ,
`fame`  int(5) NOT NULL DEFAULT 0 ,
`hair`  int(11) NOT NULL DEFAULT 0 ,
`face`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`demonMarking`  int(11) NOT NULL DEFAULT 0 ,
`ap`  int(11) NOT NULL DEFAULT 0 ,
`map`  int(11) NOT NULL DEFAULT 0 ,
`spawnpoint`  int(3) NOT NULL DEFAULT 0 ,
`gm`  int(3) NOT NULL DEFAULT 0 ,
`party`  int(11) NOT NULL DEFAULT 0 ,
`buddyCapacity`  int(11) NOT NULL DEFAULT 25 ,
`createdate`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`guildid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`guildrank`  tinyint(1) UNSIGNED NOT NULL DEFAULT 5 ,
`allianceRank`  tinyint(1) UNSIGNED NOT NULL DEFAULT 5 ,
`guildContribution`  int(11) NOT NULL DEFAULT 0 ,
`gpcon`  int(11) NOT NULL DEFAULT 0 ,
`pets`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '-1,-1,-1' ,
`sp`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0,0,0,0,0,0,0,0,0,0' ,
`subcategory`  int(11) NOT NULL DEFAULT 0 ,
`rank`  int(11) NOT NULL DEFAULT 1 ,
`rankMove`  int(11) NOT NULL DEFAULT 0 ,
`jobRank`  int(11) NOT NULL DEFAULT 1 ,
`jobRankMove`  int(11) NOT NULL DEFAULT 0 ,
`marriageId`  int(11) NOT NULL DEFAULT 0 ,
`familyid`  int(11) NOT NULL DEFAULT 0 ,
`seniorid`  int(11) NOT NULL DEFAULT 0 ,
`junior1`  int(11) NOT NULL DEFAULT 0 ,
`junior2`  int(11) NOT NULL DEFAULT 0 ,
`currentrep`  int(11) NOT NULL DEFAULT 0 ,
`totalrep`  int(11) NOT NULL DEFAULT 0 ,
`gachexp`  int(11) NOT NULL DEFAULT 0 ,
`fatigue`  mediumint(7) NOT NULL DEFAULT 0 ,
`charm`  mediumint(7) NOT NULL DEFAULT 0 ,
`craft`  mediumint(7) NOT NULL DEFAULT 0 ,
`charisma`  mediumint(7) NOT NULL DEFAULT 0 ,
`will`  mediumint(7) NOT NULL DEFAULT 0 ,
`sense`  mediumint(7) NOT NULL DEFAULT 0 ,
`insight`  mediumint(7) NOT NULL DEFAULT 0 ,
`honourExp`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`honourLevel`  int(11) UNSIGNED NOT NULL DEFAULT 1 ,
`damage`  int(11) NOT NULL DEFAULT 0 ,
`showdamage`  int(11) NULL DEFAULT 1 ,
`totalWins`  int(11) NOT NULL DEFAULT 0 ,
`totalLosses`  int(11) NOT NULL DEFAULT 0 ,
`pvpExp`  int(11) NOT NULL DEFAULT 0 ,
`pvpPoints`  int(11) NOT NULL DEFAULT 0 ,
`rebirths`  int(11) NOT NULL DEFAULT 0 ,
`prefix`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`reborns`  int(11) NOT NULL DEFAULT 0 ,
`apstorage`  int(11) NOT NULL DEFAULT 0 ,
`donatorPoints`  int(11) NOT NULL DEFAULT 0 ,
`gmtext`  int(11) NOT NULL DEFAULT 0 ,
`occupationId`  int(11) UNSIGNED NOT NULL DEFAULT 1 ,
`occupationExp`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`occupationLevel`  int(11) UNSIGNED NOT NULL DEFAULT 1 ,
`charToggle`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`jqlevel`  int(11) UNSIGNED NOT NULL DEFAULT 1 ,
`jqexp`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`pvpKills`  int(11) UNSIGNED NOT NULL DEFAULT 1 ,
`pvpDeaths`  int(11) UNSIGNED NOT NULL DEFAULT 1 ,
`fametoggle`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`dps`  bigint(20) UNSIGNED NOT NULL DEFAULT 0 ,
`msipoints`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`muted`  tinyint(1) UNSIGNED NOT NULL DEFAULT 0 ,
`unmutetime`  bigint(20) UNSIGNED NOT NULL DEFAULT 0 ,
`dgm`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`gml`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`noacc`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`location`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`birthday`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`found`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`todo`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`autobuff`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`autoap`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`autotoken`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`elf`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`primexe`  bigint(20) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
FOREIGN KEY (`accountid`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `accountid` (`accountid`) USING BTREE ,
INDEX `id` (`id`) USING BTREE ,
INDEX `guildid` (`guildid`) USING BTREE ,
INDEX `familyid` (`familyid`) USING BTREE ,
INDEX `marriageId` (`marriageId`) USING BTREE ,
INDEX `seniorid` (`seniorid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=2857

;

-- ----------------------------
-- Table structure for `cheatlog`
-- ----------------------------
DROP TABLE IF EXISTS `cheatlog`;
CREATE TABLE `cheatlog` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`offense`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`count`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`lastoffensetime`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
`param`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
INDEX `characterid` (`characterid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `chrimg`
-- ----------------------------
DROP TABLE IF EXISTS `chrimg`;
CREATE TABLE `chrimg` (
`id`  int(11) NOT NULL ,
`hash`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
INDEX `id` (`id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `compensationlog_confirmed`
-- ----------------------------
DROP TABLE IF EXISTS `compensationlog_confirmed`;
CREATE TABLE `compensationlog_confirmed` (
`chrname`  varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`donor`  tinyint(1) NOT NULL DEFAULT 0 ,
`value`  int(11) NOT NULL DEFAULT 0 ,
`taken`  tinyint(1) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`chrname`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `csequipment`
-- ----------------------------
DROP TABLE IF EXISTS `csequipment`;
CREATE TABLE `csequipment` (
`inventoryequipmentid`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
`inventoryitemid`  bigint(20) UNSIGNED NOT NULL DEFAULT 0 ,
`upgradeslots`  int(11) NOT NULL DEFAULT 0 ,
`level`  int(11) NOT NULL DEFAULT 0 ,
`str`  int(11) NOT NULL DEFAULT 0 ,
`dex`  int(11) NOT NULL DEFAULT 0 ,
`int`  int(11) NOT NULL DEFAULT 0 ,
`luk`  int(11) NOT NULL DEFAULT 0 ,
`hp`  int(11) NOT NULL DEFAULT 0 ,
`mp`  int(11) NOT NULL DEFAULT 0 ,
`watk`  int(11) NOT NULL DEFAULT 0 ,
`matk`  int(11) NOT NULL DEFAULT 0 ,
`wdef`  int(11) NOT NULL DEFAULT 0 ,
`mdef`  int(11) NOT NULL DEFAULT 0 ,
`acc`  int(11) NOT NULL DEFAULT 0 ,
`avoid`  int(11) NOT NULL DEFAULT 0 ,
`hands`  int(11) NOT NULL DEFAULT 0 ,
`speed`  int(11) NOT NULL DEFAULT 0 ,
`jump`  int(11) NOT NULL DEFAULT 0 ,
`ViciousHammer`  tinyint(2) NOT NULL DEFAULT 0 ,
`itemEXP`  int(11) NOT NULL DEFAULT 0 ,
`durability`  int(11) NOT NULL DEFAULT '-1' ,
`enhance`  tinyint(3) NOT NULL DEFAULT 0 ,
`potential1`  int(5) NOT NULL DEFAULT 0 ,
`potential2`  int(5) NOT NULL DEFAULT 0 ,
`potential3`  int(5) NOT NULL DEFAULT 0 ,
`potential4`  int(5) NOT NULL DEFAULT 0 ,
`potential5`  int(5) NOT NULL DEFAULT 0 ,
`socket1`  int(5) NOT NULL DEFAULT '-1' ,
`socket2`  int(5) NOT NULL DEFAULT '-1' ,
`socket3`  int(5) NOT NULL DEFAULT '-1' ,
`incSkill`  int(11) NOT NULL DEFAULT '-1' ,
`charmEXP`  smallint(6) NOT NULL DEFAULT '-1' ,
`pvpDamage`  smallint(6) NOT NULL DEFAULT 0 ,
`equipLevel`  int(11) UNSIGNED NOT NULL DEFAULT 1 ,
`equipExp`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`equipMSIUpgrades`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`extrascroll`  int(11) NOT NULL DEFAULT 0 ,
`addi_str`  int(11) NOT NULL DEFAULT 0 ,
`addi_dex`  int(11) NOT NULL DEFAULT 0 ,
`addi_int`  int(11) NOT NULL DEFAULT 0 ,
`addi_luk`  int(11) NOT NULL DEFAULT 0 ,
`addi_watk`  int(11) NOT NULL DEFAULT 0 ,
`addi_matk`  int(11) NOT NULL DEFAULT 0 ,
`break_dmg`  int(11) NOT NULL ,
PRIMARY KEY (`inventoryequipmentid`),
FOREIGN KEY (`inventoryitemid`) REFERENCES `csitems` (`inventoryitemid`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `inventoryitemid` (`inventoryitemid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=60836765

;

-- ----------------------------
-- Table structure for `csitems`
-- ----------------------------
DROP TABLE IF EXISTS `csitems`;
CREATE TABLE `csitems` (
`inventoryitemid`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NULL DEFAULT NULL ,
`accountid`  int(11) UNSIGNED NULL DEFAULT NULL ,
`packageid`  int(11) NULL DEFAULT NULL ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
`inventorytype`  int(11) NOT NULL DEFAULT 0 ,
`position`  int(11) NOT NULL DEFAULT 0 ,
`quantity`  int(11) NOT NULL DEFAULT 0 ,
`owner`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`GM_Log`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`uniqueid`  int(11) NOT NULL DEFAULT '-1' ,
`flag`  int(2) NOT NULL DEFAULT 0 ,
`expiredate`  bigint(20) NOT NULL DEFAULT '-1' ,
`type`  tinyint(1) NOT NULL DEFAULT 0 ,
`sender`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
PRIMARY KEY (`inventoryitemid`),
INDEX `inventoryitems_ibfk_1` (`characterid`) USING BTREE ,
INDEX `characterid` (`characterid`) USING BTREE ,
INDEX `inventorytype` (`inventorytype`) USING BTREE ,
INDEX `accountid` (`accountid`) USING BTREE ,
INDEX `packageid` (`packageid`) USING BTREE ,
INDEX `characterid_2` (`characterid`, `inventorytype`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=80931564

;

-- ----------------------------
-- Table structure for `dojo_ranks`
-- ----------------------------
DROP TABLE IF EXISTS `dojo_ranks`;
CREATE TABLE `dojo_ranks` (
`id`  int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
`name`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`time`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
INDEX `id` (`id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=latin1 COLLATE=latin1_swedish_ci
AUTO_INCREMENT=42

;

-- ----------------------------
-- Table structure for `dojo_ranks_month`
-- ----------------------------
DROP TABLE IF EXISTS `dojo_ranks_month`;
CREATE TABLE `dojo_ranks_month` (
`id`  int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
`name`  varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`rank`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
INDEX `id` (`id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=latin1 COLLATE=latin1_swedish_ci
AUTO_INCREMENT=18

;

-- ----------------------------
-- Table structure for `donation`
-- ----------------------------
DROP TABLE IF EXISTS `donation`;
CREATE TABLE `donation` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`date`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`ip`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '127.0.0.1' ,
`username`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`quantity`  smallint(5) NULL DEFAULT NULL ,
`status`  tinyint(1) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `donorlog`
-- ----------------------------
DROP TABLE IF EXISTS `donorlog`;
CREATE TABLE `donorlog` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`accname`  varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`accId`  int(11) NOT NULL DEFAULT 0 ,
`chrname`  varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`chrId`  int(11) NOT NULL DEFAULT 0 ,
`log`  varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`time`  varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`previousPoints`  int(11) NOT NULL DEFAULT 0 ,
`currentPoints`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `drop_data`
-- ----------------------------
DROP TABLE IF EXISTS `drop_data`;
CREATE TABLE `drop_data` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`dropperid`  int(11) NOT NULL ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
`minimum_quantity`  int(11) NOT NULL DEFAULT 1 ,
`maximum_quantity`  int(11) NOT NULL DEFAULT 1 ,
`questid`  int(11) NOT NULL DEFAULT 0 ,
`chance`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
INDEX `mobid` (`dropperid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=45503

;

-- ----------------------------
-- Table structure for `drop_data_global`
-- ----------------------------
DROP TABLE IF EXISTS `drop_data_global`;
CREATE TABLE `drop_data_global` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`continent`  int(11) NOT NULL ,
`dropType`  tinyint(1) NOT NULL DEFAULT 0 ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
`minimum_quantity`  int(11) NOT NULL DEFAULT 1 ,
`maximum_quantity`  int(11) NOT NULL DEFAULT 1 ,
`questid`  int(11) NOT NULL DEFAULT 0 ,
`chance`  int(11) NOT NULL DEFAULT 0 ,
`comments`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`id`),
INDEX `mobid` (`continent`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=8

;

-- ----------------------------
-- Table structure for `dueyequipment`
-- ----------------------------
DROP TABLE IF EXISTS `dueyequipment`;
CREATE TABLE `dueyequipment` (
`inventoryequipmentid`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
`inventoryitemid`  bigint(20) UNSIGNED NOT NULL DEFAULT 0 ,
`upgradeslots`  int(11) NOT NULL DEFAULT 0 ,
`level`  int(11) NOT NULL DEFAULT 0 ,
`str`  int(11) NOT NULL DEFAULT 0 ,
`dex`  int(11) NOT NULL DEFAULT 0 ,
`int`  int(11) NOT NULL DEFAULT 0 ,
`luk`  int(11) NOT NULL DEFAULT 0 ,
`hp`  int(11) NOT NULL DEFAULT 0 ,
`mp`  int(11) NOT NULL DEFAULT 0 ,
`watk`  int(11) NOT NULL DEFAULT 0 ,
`matk`  int(11) NOT NULL DEFAULT 0 ,
`wdef`  int(11) NOT NULL DEFAULT 0 ,
`mdef`  int(11) NOT NULL DEFAULT 0 ,
`acc`  int(11) NOT NULL DEFAULT 0 ,
`avoid`  int(11) NOT NULL DEFAULT 0 ,
`hands`  int(11) NOT NULL DEFAULT 0 ,
`speed`  int(11) NOT NULL DEFAULT 0 ,
`jump`  int(11) NOT NULL DEFAULT 0 ,
`ViciousHammer`  tinyint(2) NOT NULL DEFAULT 0 ,
`itemEXP`  int(11) NOT NULL DEFAULT 0 ,
`durability`  int(11) NOT NULL DEFAULT '-1' ,
`enhance`  tinyint(3) NOT NULL DEFAULT 0 ,
`potential1`  int(5) NOT NULL DEFAULT 0 ,
`potential2`  int(5) NOT NULL DEFAULT 0 ,
`potential3`  int(5) NOT NULL DEFAULT 0 ,
`potential4`  int(5) NOT NULL DEFAULT 0 ,
`potential5`  int(5) NOT NULL DEFAULT 0 ,
`socket1`  int(5) NOT NULL DEFAULT '-1' ,
`socket2`  int(5) NOT NULL DEFAULT '-1' ,
`socket3`  int(5) NOT NULL DEFAULT '-1' ,
`incSkill`  int(11) NOT NULL DEFAULT '-1' ,
`charmEXP`  smallint(6) NOT NULL DEFAULT '-1' ,
`pvpDamage`  smallint(6) NOT NULL DEFAULT 0 ,
`equipLevel`  int(11) UNSIGNED NOT NULL DEFAULT 1 ,
`equipExp`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`equipMSIUpgrades`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`extrascroll`  int(11) NOT NULL DEFAULT 0 ,
`addi_str`  int(11) NOT NULL DEFAULT 0 ,
`addi_dex`  int(11) NOT NULL DEFAULT 0 ,
`addi_int`  int(11) NOT NULL DEFAULT 0 ,
`addi_luk`  int(11) NOT NULL DEFAULT 0 ,
`addi_watk`  int(11) NOT NULL DEFAULT 0 ,
`addi_matk`  int(11) NOT NULL DEFAULT 0 ,
`break_dmg`  int(11) NOT NULL ,
PRIMARY KEY (`inventoryequipmentid`),
FOREIGN KEY (`inventoryitemid`) REFERENCES `dueyitems` (`inventoryitemid`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `inventoryitemid` (`inventoryitemid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `dueyitems`
-- ----------------------------
DROP TABLE IF EXISTS `dueyitems`;
CREATE TABLE `dueyitems` (
`inventoryitemid`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) NULL DEFAULT NULL ,
`accountid`  int(11) NULL DEFAULT NULL ,
`packageid`  int(11) NULL DEFAULT NULL ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
`inventorytype`  int(11) NOT NULL DEFAULT 0 ,
`position`  int(11) NOT NULL DEFAULT 0 ,
`quantity`  int(11) NOT NULL DEFAULT 0 ,
`owner`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`GM_Log`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`uniqueid`  int(11) NOT NULL DEFAULT '-1' ,
`flag`  int(2) NOT NULL DEFAULT 0 ,
`expiredate`  bigint(20) NOT NULL DEFAULT '-1' ,
`type`  tinyint(1) NOT NULL DEFAULT 0 ,
`sender`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
PRIMARY KEY (`inventoryitemid`),
INDEX `inventoryitems_ibfk_1` (`characterid`) USING BTREE ,
INDEX `characterid` (`characterid`) USING BTREE ,
INDEX `inventorytype` (`inventorytype`) USING BTREE ,
INDEX `accountid` (`accountid`) USING BTREE ,
INDEX `packageid` (`packageid`) USING BTREE ,
INDEX `characterid_2` (`characterid`, `inventorytype`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `dueypackages`
-- ----------------------------
DROP TABLE IF EXISTS `dueypackages`;
CREATE TABLE `dueypackages` (
`PackageId`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`RecieverId`  int(11) NOT NULL ,
`SenderName`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`Mesos`  int(11) UNSIGNED NULL DEFAULT 0 ,
`TimeStamp`  bigint(20) UNSIGNED NULL DEFAULT NULL ,
`Checked`  tinyint(1) UNSIGNED NULL DEFAULT 1 ,
`Type`  tinyint(1) UNSIGNED NOT NULL ,
PRIMARY KEY (`PackageId`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `equipgrave`
-- ----------------------------
DROP TABLE IF EXISTS `equipgrave`;
CREATE TABLE `equipgrave` (
`equipgraveid`  int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) NULL DEFAULT NULL ,
`accountid`  int(10) NULL DEFAULT NULL ,
`itemid`  int(10) UNSIGNED NOT NULL DEFAULT 0 ,
`upgradeslots`  tinyint(3) UNSIGNED NOT NULL DEFAULT 0 ,
`level`  tinyint(3) UNSIGNED NOT NULL DEFAULT 0 ,
`str`  smallint(6) NOT NULL DEFAULT 0 ,
`dex`  smallint(6) NOT NULL DEFAULT 0 ,
`int`  smallint(6) NOT NULL DEFAULT 0 ,
`luk`  smallint(6) NOT NULL DEFAULT 0 ,
`hp`  smallint(6) NOT NULL DEFAULT 0 ,
`mp`  smallint(6) NOT NULL DEFAULT 0 ,
`watk`  smallint(6) NOT NULL DEFAULT 0 ,
`matk`  smallint(6) NOT NULL DEFAULT 0 ,
`wdef`  smallint(6) NOT NULL DEFAULT 0 ,
`mdef`  smallint(6) NOT NULL DEFAULT 0 ,
`acc`  smallint(6) NOT NULL DEFAULT 0 ,
`avoid`  smallint(6) NOT NULL DEFAULT 0 ,
`hands`  smallint(6) NOT NULL DEFAULT 0 ,
`speed`  smallint(6) NOT NULL DEFAULT 0 ,
`jump`  smallint(6) NOT NULL DEFAULT 0 ,
`ViciousHammer`  tinyint(2) NOT NULL DEFAULT 0 ,
`itemEXP`  int(11) NOT NULL DEFAULT 0 ,
`durability`  mediumint(9) NOT NULL DEFAULT '-1' ,
`enhance`  tinyint(3) NOT NULL DEFAULT 0 ,
`potential1`  int(11) NOT NULL DEFAULT 0 ,
`potential2`  int(11) NOT NULL DEFAULT 0 ,
`potential3`  int(11) NOT NULL DEFAULT 0 ,
`potential4`  int(11) NULL DEFAULT NULL ,
`potential5`  int(11) NOT NULL DEFAULT 0 ,
`owner`  tinytext CHARACTER SET big5 COLLATE big5_chinese_ci NULL ,
`GM_Log`  tinytext CHARACTER SET big5 COLLATE big5_chinese_ci NULL ,
`flag`  int(2) NOT NULL DEFAULT 0 ,
`expiredate`  bigint(20) NOT NULL DEFAULT '-1' ,
`type`  tinyint(1) NOT NULL DEFAULT 0 ,
`sender`  varchar(15) CHARACTER SET big5 COLLATE big5_chinese_ci NOT NULL DEFAULT '' ,
`extrascroll`  int(11) NOT NULL DEFAULT 0 ,
`addi_str`  int(11) NOT NULL DEFAULT 0 ,
`addi_dex`  int(11) NOT NULL DEFAULT 0 ,
`addi_int`  int(11) NOT NULL DEFAULT 0 ,
`addi_luk`  int(11) NOT NULL DEFAULT 0 ,
`addi_watk`  int(11) NOT NULL DEFAULT 0 ,
`addi_matk`  int(11) NOT NULL DEFAULT 0 ,
`break_dmg`  int(11) NOT NULL ,
PRIMARY KEY (`equipgraveid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=big5 COLLATE=big5_chinese_ci
AUTO_INCREMENT=1660

;

-- ----------------------------
-- Table structure for `extendedslots`
-- ----------------------------
DROP TABLE IF EXISTS `extendedslots`;
CREATE TABLE `extendedslots` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) NOT NULL DEFAULT 0 ,
`itemId`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `famelog`
-- ----------------------------
DROP TABLE IF EXISTS `famelog`;
CREATE TABLE `famelog` (
`famelogid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`characterid_to`  int(11) NOT NULL DEFAULT 0 ,
`when`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
PRIMARY KEY (`famelogid`),
FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `characterid` (`characterid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=723

;

-- ----------------------------
-- Table structure for `familiars`
-- ----------------------------
DROP TABLE IF EXISTS `familiars`;
CREATE TABLE `familiars` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`familiar`  int(11) NOT NULL DEFAULT 0 ,
`name`  varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`fatigue`  int(11) NOT NULL DEFAULT 0 ,
`expiry`  bigint(20) NOT NULL DEFAULT 0 ,
`vitality`  tinyint(1) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `familiars_ibfk_1` (`characterid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `families`
-- ----------------------------
DROP TABLE IF EXISTS `families`;
CREATE TABLE `families` (
`familyid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`leaderid`  int(11) NOT NULL DEFAULT 0 ,
`notice`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
PRIMARY KEY (`familyid`),
INDEX `familyid` (`familyid`) USING BTREE ,
INDEX `leaderid` (`leaderid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `gifts`
-- ----------------------------
DROP TABLE IF EXISTS `gifts`;
CREATE TABLE `gifts` (
`giftid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`recipient`  int(11) NOT NULL DEFAULT 0 ,
`from`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`message`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`sn`  int(11) NOT NULL DEFAULT 0 ,
`uniqueid`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`giftid`),
INDEX `recipient` (`recipient`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `giftsender`
-- ----------------------------
DROP TABLE IF EXISTS `giftsender`;
CREATE TABLE `giftsender` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`FBName`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`GiftName`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`isSent`  int(11) NOT NULL DEFAULT 0 ,
`charid`  int(11) NOT NULL ,
`account`  varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`SentTime`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`url`  varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=5450

;

-- ----------------------------
-- Table structure for `gmlog`
-- ----------------------------
DROP TABLE IF EXISTS `gmlog`;
CREATE TABLE `gmlog` (
`gmlogid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`cid`  int(11) NOT NULL DEFAULT 0 ,
`command`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`mapid`  int(11) NOT NULL DEFAULT 0 ,
`time`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
PRIMARY KEY (`gmlogid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `guilds`
-- ----------------------------
DROP TABLE IF EXISTS `guilds`;
CREATE TABLE `guilds` (
`guildid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`leader`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`GP`  int(11) NOT NULL DEFAULT 0 ,
`logo`  int(11) UNSIGNED NULL DEFAULT NULL ,
`logoColor`  smallint(5) UNSIGNED NOT NULL DEFAULT 0 ,
`name`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`rank1title`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '公會長' ,
`rank2title`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '副公會長' ,
`rank3title`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '會員' ,
`rank4title`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '會員' ,
`rank5title`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '會員' ,
`capacity`  int(11) UNSIGNED NOT NULL DEFAULT 10 ,
`logoBG`  int(11) UNSIGNED NULL DEFAULT NULL ,
`logoBGColor`  smallint(5) UNSIGNED NOT NULL DEFAULT 0 ,
`notice`  varchar(101) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`signature`  int(11) NOT NULL DEFAULT 0 ,
`alliance`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`rankinglastmonth`  int(11) NOT NULL DEFAULT 0 ,
`guildtotalpoints`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`guildid`),
UNIQUE INDEX `name` (`name`) USING BTREE ,
INDEX `guildid` (`guildid`) USING BTREE ,
INDEX `leader` (`leader`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=27

;

-- ----------------------------
-- Table structure for `guildskills`
-- ----------------------------
DROP TABLE IF EXISTS `guildskills`;
CREATE TABLE `guildskills` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`guildid`  int(11) NOT NULL DEFAULT 0 ,
`skillid`  int(11) NOT NULL DEFAULT 0 ,
`level`  smallint(3) NOT NULL DEFAULT 1 ,
`timestamp`  bigint(20) NOT NULL DEFAULT 0 ,
`purchaser`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=269

;

-- ----------------------------
-- Table structure for `hidelog`
-- ----------------------------
DROP TABLE IF EXISTS `hidelog`;
CREATE TABLE `hidelog` (
`logid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`cid`  int(11) NOT NULL DEFAULT 0 ,
`text`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`mapid`  int(11) NOT NULL DEFAULT 0 ,
`time`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
PRIMARY KEY (`logid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `hiredmerch`
-- ----------------------------
DROP TABLE IF EXISTS `hiredmerch`;
CREATE TABLE `hiredmerch` (
`PackageId`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NULL DEFAULT 0 ,
`accountid`  int(11) UNSIGNED NULL DEFAULT NULL ,
`Mesos`  int(11) UNSIGNED NULL DEFAULT 0 ,
`time`  bigint(20) UNSIGNED NULL DEFAULT NULL ,
PRIMARY KEY (`PackageId`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=5897

;

-- ----------------------------
-- Table structure for `hiredmerchequipment`
-- ----------------------------
DROP TABLE IF EXISTS `hiredmerchequipment`;
CREATE TABLE `hiredmerchequipment` (
`inventoryequipmentid`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
`inventoryitemid`  bigint(20) UNSIGNED NOT NULL DEFAULT 0 ,
`upgradeslots`  int(11) NOT NULL DEFAULT 0 ,
`level`  int(11) NOT NULL DEFAULT 0 ,
`str`  int(11) NOT NULL DEFAULT 0 ,
`dex`  int(11) NOT NULL DEFAULT 0 ,
`int`  int(11) NOT NULL DEFAULT 0 ,
`luk`  int(11) NOT NULL DEFAULT 0 ,
`hp`  int(11) NOT NULL DEFAULT 0 ,
`mp`  int(11) NOT NULL DEFAULT 0 ,
`watk`  int(11) NOT NULL DEFAULT 0 ,
`matk`  int(11) NOT NULL DEFAULT 0 ,
`wdef`  int(11) NOT NULL DEFAULT 0 ,
`mdef`  int(11) NOT NULL DEFAULT 0 ,
`acc`  int(11) NOT NULL DEFAULT 0 ,
`avoid`  int(11) NOT NULL DEFAULT 0 ,
`hands`  int(11) NOT NULL DEFAULT 0 ,
`speed`  int(11) NOT NULL DEFAULT 0 ,
`jump`  int(11) NOT NULL DEFAULT 0 ,
`ViciousHammer`  tinyint(2) NOT NULL DEFAULT 0 ,
`itemEXP`  int(11) NOT NULL DEFAULT 0 ,
`durability`  int(11) NOT NULL DEFAULT '-1' ,
`enhance`  tinyint(3) NOT NULL DEFAULT 0 ,
`potential1`  int(5) NOT NULL DEFAULT 0 ,
`potential2`  int(5) NOT NULL DEFAULT 0 ,
`potential3`  int(5) NOT NULL DEFAULT 0 ,
`potential4`  int(5) NOT NULL DEFAULT 0 ,
`potential5`  int(5) NOT NULL DEFAULT 0 ,
`socket1`  int(5) NOT NULL DEFAULT '-1' ,
`socket2`  int(5) NOT NULL DEFAULT '-1' ,
`socket3`  int(5) NOT NULL DEFAULT '-1' ,
`incSkill`  int(11) NOT NULL DEFAULT '-1' ,
`charmEXP`  smallint(6) NOT NULL DEFAULT '-1' ,
`pvpDamage`  smallint(6) NOT NULL DEFAULT 0 ,
`equipLevel`  int(11) UNSIGNED NOT NULL DEFAULT 1 ,
`equipExp`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`equipMSIUpgrades`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`extrascroll`  int(11) NOT NULL DEFAULT 0 ,
`addi_str`  int(11) NOT NULL DEFAULT 0 ,
`addi_dex`  int(11) NOT NULL DEFAULT 0 ,
`addi_int`  int(11) NOT NULL DEFAULT 0 ,
`addi_luk`  int(11) NOT NULL DEFAULT 0 ,
`addi_watk`  int(11) NOT NULL DEFAULT 0 ,
`addi_matk`  int(11) NOT NULL DEFAULT 0 ,
`break_dmg`  int(11) NOT NULL ,
PRIMARY KEY (`inventoryequipmentid`, `upgradeslots`),
FOREIGN KEY (`inventoryitemid`) REFERENCES `hiredmerchitems` (`inventoryitemid`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `inventoryitemid` (`inventoryitemid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=11572

;

-- ----------------------------
-- Table structure for `hiredmerchitems`
-- ----------------------------
DROP TABLE IF EXISTS `hiredmerchitems`;
CREATE TABLE `hiredmerchitems` (
`inventoryitemid`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) NULL DEFAULT NULL ,
`accountid`  int(11) NULL DEFAULT NULL ,
`packageid`  int(11) NULL DEFAULT NULL ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
`inventorytype`  int(11) NOT NULL DEFAULT 0 ,
`position`  int(11) NOT NULL DEFAULT 0 ,
`quantity`  int(11) NOT NULL DEFAULT 0 ,
`owner`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`GM_Log`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`uniqueid`  int(11) NOT NULL DEFAULT '-1' ,
`flag`  int(2) NOT NULL DEFAULT 0 ,
`expiredate`  bigint(20) NOT NULL DEFAULT '-1' ,
`type`  tinyint(1) NOT NULL DEFAULT 0 ,
`sender`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
PRIMARY KEY (`inventoryitemid`),
INDEX `inventoryitems_ibfk_1` (`characterid`) USING BTREE ,
INDEX `characterid` (`characterid`) USING BTREE ,
INDEX `inventorytype` (`inventorytype`) USING BTREE ,
INDEX `accountid` (`accountid`) USING BTREE ,
INDEX `packageid` (`packageid`) USING BTREE ,
INDEX `characterid_2` (`characterid`, `inventorytype`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=31018

;

-- ----------------------------
-- Table structure for `hyperrocklocations`
-- ----------------------------
DROP TABLE IF EXISTS `hyperrocklocations`;
CREATE TABLE `hyperrocklocations` (
`trockid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) NULL DEFAULT NULL ,
`mapid`  int(11) NULL DEFAULT NULL ,
PRIMARY KEY (`trockid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `imps`
-- ----------------------------
DROP TABLE IF EXISTS `imps`;
CREATE TABLE `imps` (
`impid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) NOT NULL DEFAULT 0 ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
`level`  tinyint(3) UNSIGNED NOT NULL DEFAULT 1 ,
`state`  tinyint(3) UNSIGNED NOT NULL DEFAULT 1 ,
`closeness`  mediumint(6) UNSIGNED NOT NULL DEFAULT 0 ,
`fullness`  mediumint(6) UNSIGNED NOT NULL DEFAULT 0 ,
PRIMARY KEY (`impid`),
INDEX `impid` (`impid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `inner_ability_skills`
-- ----------------------------
DROP TABLE IF EXISTS `inner_ability_skills`;
CREATE TABLE `inner_ability_skills` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`player_id`  int(11) NOT NULL DEFAULT 0 ,
`skill_id`  int(11) NOT NULL DEFAULT 0 ,
`skill_level`  int(11) NOT NULL DEFAULT 0 ,
`max_level`  int(11) NOT NULL DEFAULT 0 ,
`rank`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `internlog`
-- ----------------------------
DROP TABLE IF EXISTS `internlog`;
CREATE TABLE `internlog` (
`gmlogid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`cid`  int(11) NOT NULL DEFAULT 0 ,
`command`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`mapid`  int(11) NOT NULL DEFAULT 0 ,
`time`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
PRIMARY KEY (`gmlogid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `inventoryequipment`
-- ----------------------------
DROP TABLE IF EXISTS `inventoryequipment`;
CREATE TABLE `inventoryequipment` (
`inventoryequipmentid`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
`inventoryitemid`  bigint(20) UNSIGNED NOT NULL DEFAULT 0 ,
`upgradeslots`  tinyint(3) UNSIGNED NOT NULL DEFAULT 0 ,
`level`  tinyint(3) UNSIGNED NOT NULL DEFAULT 0 ,
`str`  int(6) NOT NULL DEFAULT 0 ,
`dex`  int(6) NOT NULL DEFAULT 0 ,
`int`  int(6) NOT NULL DEFAULT 0 ,
`luk`  int(6) NOT NULL DEFAULT 0 ,
`hp`  int(6) NOT NULL DEFAULT 0 ,
`mp`  int(6) NOT NULL DEFAULT 0 ,
`watk`  int(6) NOT NULL DEFAULT 0 ,
`matk`  int(6) NOT NULL DEFAULT 0 ,
`wdef`  int(6) NOT NULL DEFAULT 0 ,
`mdef`  int(6) NOT NULL DEFAULT 0 ,
`acc`  int(6) NOT NULL DEFAULT 0 ,
`avoid`  int(6) NOT NULL DEFAULT 0 ,
`hands`  int(6) NOT NULL DEFAULT 0 ,
`speed`  int(6) NOT NULL DEFAULT 0 ,
`jump`  int(6) NOT NULL DEFAULT 0 ,
`ViciousHammer`  tinyint(2) NOT NULL DEFAULT 0 ,
`itemEXP`  int(11) NOT NULL DEFAULT 0 ,
`durability`  mediumint(9) NOT NULL DEFAULT '-1' ,
`enhance`  tinyint(3) NOT NULL DEFAULT 0 ,
`potential1`  int(5) NOT NULL DEFAULT 0 ,
`potential2`  int(5) NOT NULL DEFAULT 0 ,
`potential3`  int(5) NOT NULL DEFAULT 0 ,
`potential4`  int(5) NOT NULL DEFAULT 0 ,
`potential5`  int(5) NOT NULL DEFAULT 0 ,
`socket1`  int(5) NOT NULL DEFAULT '-1' ,
`socket2`  int(5) NOT NULL DEFAULT '-1' ,
`socket3`  int(5) NOT NULL DEFAULT '-1' ,
`incSkill`  int(11) NOT NULL DEFAULT '-1' ,
`charmEXP`  int(6) NOT NULL DEFAULT '-1' ,
`pvpDamage`  int(6) NOT NULL DEFAULT 0 ,
`equipLevel`  int(11) UNSIGNED NOT NULL DEFAULT 1 ,
`equipExp`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`equipMSIUpgrades`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`extrascroll`  int(11) NOT NULL DEFAULT 0 ,
`addi_str`  int(11) NOT NULL DEFAULT 0 ,
`addi_dex`  int(11) NOT NULL DEFAULT 0 ,
`addi_int`  int(11) NOT NULL DEFAULT 0 ,
`addi_luk`  int(11) NOT NULL DEFAULT 0 ,
`addi_watk`  int(11) NOT NULL DEFAULT 0 ,
`addi_matk`  int(11) NOT NULL DEFAULT 0 ,
`break_dmg`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`inventoryequipmentid`),
FOREIGN KEY (`inventoryitemid`) REFERENCES `inventoryitems` (`inventoryitemid`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `inventoryitemid` (`inventoryitemid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=254641182

;

-- ----------------------------
-- Table structure for `inventoryitems`
-- ----------------------------
DROP TABLE IF EXISTS `inventoryitems`;
CREATE TABLE `inventoryitems` (
`inventoryitemid`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) NULL DEFAULT NULL ,
`accountid`  int(11) NULL DEFAULT NULL ,
`packageid`  int(11) NULL DEFAULT NULL ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
`inventorytype`  int(11) NOT NULL DEFAULT 0 ,
`position`  int(11) NOT NULL DEFAULT 0 ,
`quantity`  int(11) NOT NULL DEFAULT 0 ,
`owner`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`GM_Log`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`uniqueid`  int(11) NOT NULL DEFAULT '-1' ,
`flag`  int(2) NOT NULL DEFAULT 0 ,
`expiredate`  bigint(20) NOT NULL DEFAULT '-1' ,
`type`  tinyint(1) NOT NULL DEFAULT 0 ,
`sender`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
PRIMARY KEY (`inventoryitemid`),
INDEX `inventorytype` (`inventorytype`) USING BTREE ,
INDEX `accountid` (`accountid`) USING BTREE ,
INDEX `packageid` (`packageid`) USING BTREE ,
INDEX `characterid_2` (`characterid`, `inventorytype`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=851575849

;

-- ----------------------------
-- Table structure for `inventorylog`
-- ----------------------------
DROP TABLE IF EXISTS `inventorylog`;
CREATE TABLE `inventorylog` (
`inventorylogid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`inventoryitemid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`msg`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`inventorylogid`),
INDEX `inventoryitemid` (`inventoryitemid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `inventoryslot`
-- ----------------------------
DROP TABLE IF EXISTS `inventoryslot`;
CREATE TABLE `inventoryslot` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NULL DEFAULT NULL ,
`equip`  tinyint(3) UNSIGNED NULL DEFAULT NULL ,
`use`  tinyint(3) UNSIGNED NULL DEFAULT NULL ,
`setup`  tinyint(3) UNSIGNED NULL DEFAULT NULL ,
`etc`  tinyint(3) UNSIGNED NULL DEFAULT NULL ,
`cash`  tinyint(3) UNSIGNED NULL DEFAULT NULL ,
PRIMARY KEY (`id`),
UNIQUE INDEX `characterid` (`characterid`) USING BTREE ,
INDEX `id` (`id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `ipbans`
-- ----------------------------
DROP TABLE IF EXISTS `ipbans`;
CREATE TABLE `ipbans` (
`ipbanid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`ip`  varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
PRIMARY KEY (`ipbanid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=2

;

-- ----------------------------
-- Table structure for `iplog`
-- ----------------------------
DROP TABLE IF EXISTS `iplog`;
CREATE TABLE `iplog` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`accid`  int(11) NOT NULL ,
`accname`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`ip`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`time`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `ipvotelog`
-- ----------------------------
DROP TABLE IF EXISTS `ipvotelog`;
CREATE TABLE `ipvotelog` (
`vid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`accid`  int(11) NOT NULL DEFAULT 0 ,
`ipaddress`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '127.0.0.1' ,
`votetime`  bigint(20) NOT NULL DEFAULT 0 ,
`votetype`  tinyint(1) UNSIGNED NOT NULL DEFAULT 0 ,
PRIMARY KEY (`vid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `ipvotes`
-- ----------------------------
DROP TABLE IF EXISTS `ipvotes`;
CREATE TABLE `ipvotes` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`ip`  varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`accid`  int(11) NOT NULL ,
`lastvote`  int(11) NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `keymap`
-- ----------------------------
DROP TABLE IF EXISTS `keymap`;
CREATE TABLE `keymap` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`key`  tinyint(3) UNSIGNED NOT NULL DEFAULT 0 ,
`type`  tinyint(3) UNSIGNED NOT NULL DEFAULT 0 ,
`action`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `keymap_ibfk_1` (`characterid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=43194530

;

-- ----------------------------
-- Table structure for `lottery`
-- ----------------------------
DROP TABLE IF EXISTS `lottery`;
CREATE TABLE `lottery` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`itemid`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`charid`  int(11) NOT NULL ,
`charName`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`time`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`type`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1606

;

-- ----------------------------
-- Table structure for `macbans`
-- ----------------------------
DROP TABLE IF EXISTS `macbans`;
CREATE TABLE `macbans` (
`macbanid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`mac`  varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`macbanid`),
UNIQUE INDEX `mac_2` (`mac`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=3

;

-- ----------------------------
-- Table structure for `macfilters`
-- ----------------------------
DROP TABLE IF EXISTS `macfilters`;
CREATE TABLE `macfilters` (
`macfilterid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`filter`  varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`macfilterid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `monsterbook`
-- ----------------------------
DROP TABLE IF EXISTS `monsterbook`;
CREATE TABLE `monsterbook` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`charid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`cardid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`level`  tinyint(2) UNSIGNED NULL DEFAULT 1 ,
PRIMARY KEY (`id`),
INDEX `id` (`id`) USING BTREE ,
INDEX `charid` (`charid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=24379

;

-- ----------------------------
-- Table structure for `mountdata`
-- ----------------------------
DROP TABLE IF EXISTS `mountdata`;
CREATE TABLE `mountdata` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NULL DEFAULT NULL ,
`Level`  int(3) UNSIGNED NOT NULL DEFAULT 0 ,
`Exp`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`Fatigue`  int(4) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
UNIQUE INDEX `characterid` (`characterid`) USING BTREE ,
INDEX `id` (`id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=2857

;

-- ----------------------------
-- Table structure for `mrush`
-- ----------------------------
DROP TABLE IF EXISTS `mrush`;
CREATE TABLE `mrush` (
`mesos`  bigint(8) UNSIGNED NOT NULL AUTO_INCREMENT ,
PRIMARY KEY (`mesos`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `mts_cart`
-- ----------------------------
DROP TABLE IF EXISTS `mts_cart`;
CREATE TABLE `mts_cart` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
INDEX `characterid` (`characterid`) USING BTREE ,
INDEX `id` (`id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `mts_items`
-- ----------------------------
DROP TABLE IF EXISTS `mts_items`;
CREATE TABLE `mts_items` (
`id`  int(11) NOT NULL ,
`tab`  tinyint(1) NOT NULL DEFAULT 1 ,
`price`  int(11) NOT NULL DEFAULT 0 ,
`characterid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`seller`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`expiration`  bigint(20) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `mtsequipment`
-- ----------------------------
DROP TABLE IF EXISTS `mtsequipment`;
CREATE TABLE `mtsequipment` (
`inventoryequipmentid`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
`inventoryitemid`  bigint(20) UNSIGNED NOT NULL DEFAULT 0 ,
`upgradeslots`  int(11) NOT NULL DEFAULT 0 ,
`level`  int(11) NOT NULL DEFAULT 0 ,
`str`  int(11) NOT NULL DEFAULT 0 ,
`dex`  int(11) NOT NULL DEFAULT 0 ,
`int`  int(11) NOT NULL DEFAULT 0 ,
`luk`  int(11) NOT NULL DEFAULT 0 ,
`hp`  int(11) NOT NULL DEFAULT 0 ,
`mp`  int(11) NOT NULL DEFAULT 0 ,
`watk`  int(11) NOT NULL DEFAULT 0 ,
`matk`  int(11) NOT NULL DEFAULT 0 ,
`wdef`  int(11) NOT NULL DEFAULT 0 ,
`mdef`  int(11) NOT NULL DEFAULT 0 ,
`acc`  int(11) NOT NULL DEFAULT 0 ,
`avoid`  int(11) NOT NULL DEFAULT 0 ,
`hands`  int(11) NOT NULL DEFAULT 0 ,
`speed`  int(11) NOT NULL DEFAULT 0 ,
`jump`  int(11) NOT NULL DEFAULT 0 ,
`ViciousHammer`  tinyint(2) NOT NULL DEFAULT 0 ,
`itemEXP`  int(11) NOT NULL DEFAULT 0 ,
`durability`  int(11) NOT NULL DEFAULT '-1' ,
`enhance`  tinyint(3) NOT NULL DEFAULT 0 ,
`potential1`  int(5) NOT NULL DEFAULT 0 ,
`potential2`  int(5) NOT NULL DEFAULT 0 ,
`potential3`  int(5) NOT NULL DEFAULT 0 ,
`potential4`  int(5) NOT NULL DEFAULT 0 ,
`potential5`  int(5) NOT NULL DEFAULT 0 ,
`socket1`  int(5) NOT NULL DEFAULT '-1' ,
`socket2`  int(5) NOT NULL DEFAULT '-1' ,
`socket3`  int(5) NOT NULL DEFAULT '-1' ,
`incSkill`  int(11) NOT NULL DEFAULT '-1' ,
`charmEXP`  smallint(6) NOT NULL DEFAULT '-1' ,
`pvpDamage`  smallint(6) NOT NULL DEFAULT 0 ,
`extrascroll`  int(11) NOT NULL DEFAULT 0 ,
`addi_str`  int(11) NOT NULL DEFAULT 0 ,
`addi_dex`  int(11) NOT NULL DEFAULT 0 ,
`addi_int`  int(11) NOT NULL DEFAULT 0 ,
`addi_luk`  int(11) NOT NULL DEFAULT 0 ,
`addi_watk`  int(11) NOT NULL DEFAULT 0 ,
`addi_matk`  int(11) NOT NULL DEFAULT 0 ,
`break_dmg`  int(11) NOT NULL ,
PRIMARY KEY (`inventoryequipmentid`),
FOREIGN KEY (`inventoryitemid`) REFERENCES `mtsitems` (`inventoryitemid`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `inventoryitemid` (`inventoryitemid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `mtsitems`
-- ----------------------------
DROP TABLE IF EXISTS `mtsitems`;
CREATE TABLE `mtsitems` (
`inventoryitemid`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NULL DEFAULT NULL ,
`accountid`  int(11) NULL DEFAULT NULL ,
`packageId`  int(11) NULL DEFAULT NULL ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
`inventorytype`  int(11) NOT NULL DEFAULT 0 ,
`position`  int(11) NOT NULL DEFAULT 0 ,
`quantity`  int(11) NOT NULL DEFAULT 0 ,
`owner`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`GM_Log`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`uniqueid`  int(11) NOT NULL DEFAULT '-1' ,
`flag`  int(2) NOT NULL DEFAULT 0 ,
`expiredate`  bigint(20) NOT NULL DEFAULT '-1' ,
`type`  tinyint(1) NOT NULL DEFAULT 0 ,
`sender`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
PRIMARY KEY (`inventoryitemid`),
INDEX `inventoryitems_ibfk_1` (`characterid`) USING BTREE ,
INDEX `characterid` (`characterid`) USING BTREE ,
INDEX `inventorytype` (`inventorytype`) USING BTREE ,
INDEX `accountid` (`accountid`) USING BTREE ,
INDEX `characterid_2` (`characterid`, `inventorytype`) USING BTREE ,
INDEX `packageid` (`packageId`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `mtstransfer`
-- ----------------------------
DROP TABLE IF EXISTS `mtstransfer`;
CREATE TABLE `mtstransfer` (
`inventoryitemid`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NULL DEFAULT NULL ,
`accountid`  int(11) UNSIGNED NULL DEFAULT NULL ,
`packageid`  int(11) NULL DEFAULT NULL ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
`inventorytype`  int(11) NOT NULL DEFAULT 0 ,
`position`  int(11) NOT NULL DEFAULT 0 ,
`quantity`  int(11) NOT NULL DEFAULT 0 ,
`owner`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`GM_Log`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`uniqueid`  int(11) NOT NULL DEFAULT '-1' ,
`flag`  int(2) NOT NULL DEFAULT 0 ,
`expiredate`  bigint(20) NOT NULL DEFAULT '-1' ,
`type`  tinyint(1) NOT NULL DEFAULT 0 ,
`sender`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
PRIMARY KEY (`inventoryitemid`),
INDEX `inventoryitems_ibfk_1` (`characterid`) USING BTREE ,
INDEX `characterid` (`characterid`) USING BTREE ,
INDEX `inventorytype` (`inventorytype`) USING BTREE ,
INDEX `accountid` (`accountid`) USING BTREE ,
INDEX `packageid` (`packageid`) USING BTREE ,
INDEX `characterid_2` (`characterid`, `inventorytype`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `mtstransferequipment`
-- ----------------------------
DROP TABLE IF EXISTS `mtstransferequipment`;
CREATE TABLE `mtstransferequipment` (
`inventoryequipmentid`  bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT ,
`inventoryitemid`  bigint(20) UNSIGNED NOT NULL DEFAULT 0 ,
`upgradeslots`  int(11) NOT NULL DEFAULT 0 ,
`level`  int(11) NOT NULL DEFAULT 0 ,
`str`  int(11) NOT NULL DEFAULT 0 ,
`dex`  int(11) NOT NULL DEFAULT 0 ,
`int`  int(11) NOT NULL DEFAULT 0 ,
`luk`  int(11) NOT NULL DEFAULT 0 ,
`hp`  int(11) NOT NULL DEFAULT 0 ,
`mp`  int(11) NOT NULL DEFAULT 0 ,
`watk`  int(11) NOT NULL DEFAULT 0 ,
`matk`  int(11) NOT NULL DEFAULT 0 ,
`wdef`  int(11) NOT NULL DEFAULT 0 ,
`mdef`  int(11) NOT NULL DEFAULT 0 ,
`acc`  int(11) NOT NULL DEFAULT 0 ,
`avoid`  int(11) NOT NULL DEFAULT 0 ,
`hands`  int(11) NOT NULL DEFAULT 0 ,
`speed`  int(11) NOT NULL DEFAULT 0 ,
`jump`  int(11) NOT NULL DEFAULT 0 ,
`ViciousHammer`  tinyint(2) NOT NULL DEFAULT 0 ,
`itemEXP`  int(11) NOT NULL DEFAULT 0 ,
`durability`  int(11) NOT NULL DEFAULT '-1' ,
`enhance`  tinyint(3) NOT NULL DEFAULT 0 ,
`potential1`  int(5) NOT NULL DEFAULT 0 ,
`potential2`  int(5) NOT NULL DEFAULT 0 ,
`potential3`  int(5) NOT NULL DEFAULT 0 ,
`potential4`  int(5) NOT NULL DEFAULT 0 ,
`potential5`  int(5) NOT NULL DEFAULT 0 ,
`socket1`  int(5) NOT NULL DEFAULT '-1' ,
`socket2`  int(5) NOT NULL DEFAULT '-1' ,
`socket3`  int(5) NOT NULL DEFAULT '-1' ,
`incSkill`  int(11) NOT NULL DEFAULT '-1' ,
`charmEXP`  smallint(6) NOT NULL DEFAULT '-1' ,
`pvpDamage`  smallint(6) NOT NULL DEFAULT 0 ,
`extrascroll`  int(11) NOT NULL DEFAULT 0 ,
`addi_str`  int(11) NOT NULL DEFAULT 0 ,
`addi_dex`  int(11) NOT NULL DEFAULT 0 ,
`addi_int`  int(11) NOT NULL DEFAULT 0 ,
`addi_luk`  int(11) NOT NULL DEFAULT 0 ,
`addi_watk`  int(11) NOT NULL DEFAULT 0 ,
`addi_matk`  int(11) NOT NULL DEFAULT 0 ,
`break_dmg`  int(11) NOT NULL ,
PRIMARY KEY (`inventoryequipmentid`),
FOREIGN KEY (`inventoryitemid`) REFERENCES `mtstransfer` (`inventoryitemid`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `inventoryitemid` (`inventoryitemid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `myinfo`
-- ----------------------------
DROP TABLE IF EXISTS `myinfo`;
CREATE TABLE `myinfo` (
`infoid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`cid`  int(11) UNSIGNED NOT NULL ,
`location`  int(11) NOT NULL ,
`todo`  int(11) UNSIGNED NOT NULL ,
`birthday`  int(11) UNSIGNED NOT NULL ,
`found`  int(11) UNSIGNED NOT NULL ,
PRIMARY KEY (`infoid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `notes`
-- ----------------------------
DROP TABLE IF EXISTS `notes`;
CREATE TABLE `notes` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`to`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`from`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`message`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`timestamp`  bigint(20) UNSIGNED NOT NULL ,
`gift`  tinyint(1) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
INDEX `to` (`to`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=22

;

-- ----------------------------
-- Table structure for `nxcode`
-- ----------------------------
DROP TABLE IF EXISTS `nxcode`;
CREATE TABLE `nxcode` (
`code`  varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`valid`  int(11) NOT NULL DEFAULT 1 ,
`user`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`type`  int(11) NOT NULL DEFAULT 0 ,
`item`  int(11) NOT NULL DEFAULT 10000 ,
PRIMARY KEY (`code`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `paybill_bills`
-- ----------------------------
DROP TABLE IF EXISTS `paybill_bills`;
CREATE TABLE `paybill_bills` (
`BillID`  int(11) NOT NULL AUTO_INCREMENT ,
`money`  int(11) NOT NULL ,
`account`  char(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL ,
`accountID`  int(11) NOT NULL ,
`characterID`  int(11) NOT NULL ,
`Date`  timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP ,
`isSent`  tinyint(10) NOT NULL DEFAULT '-1' ,
`TradeNo`  char(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`url`  char(30) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL ,
PRIMARY KEY (`BillID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=latin1 COLLATE=latin1_swedish_ci
AUTO_INCREMENT=502

;

-- ----------------------------
-- Table structure for `paybill_paylog`
-- ----------------------------
DROP TABLE IF EXISTS `paybill_paylog`;
CREATE TABLE `paybill_paylog` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`account`  varchar(30) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL ,
`money`  int(11) NOT NULL ,
`dps`  int(11) NOT NULL ,
`paytime`  date NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=latin1 COLLATE=latin1_swedish_ci
AUTO_INCREMENT=160

;

-- ----------------------------
-- Table structure for `pets`
-- ----------------------------
DROP TABLE IF EXISTS `pets`;
CREATE TABLE `pets` (
`petid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`name`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`level`  int(3) UNSIGNED NOT NULL ,
`closeness`  int(6) UNSIGNED NOT NULL ,
`fullness`  int(3) UNSIGNED NOT NULL ,
`seconds`  int(11) NOT NULL DEFAULT 0 ,
`flags`  smallint(5) NOT NULL DEFAULT 0 ,
`skill`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`petid`),
INDEX `petid` (`petid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=157230

;

-- ----------------------------
-- Table structure for `playernpcs`
-- ----------------------------
DROP TABLE IF EXISTS `playernpcs`;
CREATE TABLE `playernpcs` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`name`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`hair`  int(11) NOT NULL ,
`face`  int(11) NOT NULL ,
`skin`  int(11) NOT NULL ,
`x`  int(11) NOT NULL DEFAULT 0 ,
`y`  int(11) NOT NULL DEFAULT 0 ,
`map`  int(11) NOT NULL ,
`charid`  int(11) UNSIGNED NOT NULL ,
`scriptid`  int(11) NOT NULL ,
`foothold`  int(11) NOT NULL ,
`dir`  tinyint(1) NOT NULL DEFAULT 0 ,
`gender`  tinyint(1) NOT NULL DEFAULT 0 ,
`pets`  varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0,0,0' ,
PRIMARY KEY (`id`),
FOREIGN KEY (`charid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `scriptid` (`scriptid`) USING BTREE ,
INDEX `playernpcs_ibfk_1` (`charid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `playernpcs_equip`
-- ----------------------------
DROP TABLE IF EXISTS `playernpcs_equip`;
CREATE TABLE `playernpcs_equip` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`npcid`  int(11) NOT NULL ,
`equipid`  int(11) NOT NULL ,
`equippos`  int(11) NOT NULL ,
`charid`  int(11) UNSIGNED NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`charid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
FOREIGN KEY (`npcid`) REFERENCES `playernpcs` (`scriptid`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `playernpcs_equip_ibfk_1` (`charid`) USING BTREE ,
INDEX `playernpcs_equip_ibfk_2` (`npcid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `pokemon`
-- ----------------------------
DROP TABLE IF EXISTS `pokemon`;
CREATE TABLE `pokemon` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`monsterid`  int(11) NOT NULL DEFAULT 0 ,
`characterid`  int(11) NOT NULL DEFAULT 0 ,
`level`  smallint(3) NOT NULL DEFAULT 1 ,
`exp`  int(11) NOT NULL DEFAULT 0 ,
`name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`nature`  tinyint(3) NOT NULL DEFAULT 0 ,
`active`  tinyint(1) NOT NULL DEFAULT 0 ,
`accountid`  int(11) NOT NULL DEFAULT 0 ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
`gender`  tinyint(2) NOT NULL DEFAULT '-1' ,
`hpiv`  tinyint(3) NOT NULL DEFAULT '-1' ,
`atkiv`  tinyint(3) NOT NULL DEFAULT '-1' ,
`defiv`  tinyint(3) NOT NULL DEFAULT '-1' ,
`spatkiv`  tinyint(3) NOT NULL DEFAULT '-1' ,
`spdefiv`  tinyint(3) NOT NULL DEFAULT '-1' ,
`speediv`  tinyint(3) NOT NULL DEFAULT '-1' ,
`evaiv`  tinyint(3) NOT NULL DEFAULT '-1' ,
`acciv`  tinyint(3) NOT NULL DEFAULT '-1' ,
`ability`  tinyint(2) NOT NULL DEFAULT '-1' ,
PRIMARY KEY (`id`),
INDEX `id` (`id`) USING BTREE ,
INDEX `characterid` (`characterid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `proxyip`
-- ----------------------------
DROP TABLE IF EXISTS `proxyip`;
CREATE TABLE `proxyip` (
`proxyid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`proxyip`  varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`proxyid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=10

;

-- ----------------------------
-- Table structure for `pwreset`
-- ----------------------------
DROP TABLE IF EXISTS `pwreset`;
CREATE TABLE `pwreset` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`name`  varchar(14) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`email`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`confirmkey`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`status`  tinyint(1) UNSIGNED NOT NULL DEFAULT 0 ,
`timestamp`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `questinfo`
-- ----------------------------
DROP TABLE IF EXISTS `questinfo`;
CREATE TABLE `questinfo` (
`questinfoid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`quest`  int(6) NOT NULL DEFAULT 0 ,
`customData`  varchar(555) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`questinfoid`),
FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `characterid` (`characterid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=16525

;

-- ----------------------------
-- Table structure for `queststatus`
-- ----------------------------
DROP TABLE IF EXISTS `queststatus`;
CREATE TABLE `queststatus` (
`queststatusid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`quest`  int(6) NOT NULL DEFAULT 0 ,
`status`  tinyint(4) NOT NULL DEFAULT 0 ,
`time`  int(11) NOT NULL DEFAULT 0 ,
`forfeited`  int(11) NOT NULL DEFAULT 0 ,
`customData`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`queststatusid`),
FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `characterid` (`characterid`) USING BTREE ,
INDEX `queststatusid` (`queststatusid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=195529068

;

-- ----------------------------
-- Table structure for `queststatusmobs`
-- ----------------------------
DROP TABLE IF EXISTS `queststatusmobs`;
CREATE TABLE `queststatusmobs` (
`queststatusmobid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`queststatusid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`mob`  int(11) NOT NULL DEFAULT 0 ,
`count`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`queststatusmobid`),
FOREIGN KEY (`queststatusid`) REFERENCES `queststatus` (`queststatusid`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `queststatusid` (`queststatusid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1451130

;

-- ----------------------------
-- Table structure for `reactordrops`
-- ----------------------------
DROP TABLE IF EXISTS `reactordrops`;
CREATE TABLE `reactordrops` (
`reactordropid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`reactorid`  int(11) NOT NULL ,
`itemid`  int(11) NOT NULL ,
`chance`  int(11) NOT NULL ,
`questid`  int(5) NOT NULL DEFAULT '-1' ,
PRIMARY KEY (`reactordropid`),
INDEX `reactorid` (`reactorid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=841

;

-- ----------------------------
-- Table structure for `regrocklocations`
-- ----------------------------
DROP TABLE IF EXISTS `regrocklocations`;
CREATE TABLE `regrocklocations` (
`trockid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) NULL DEFAULT NULL ,
`mapid`  int(11) NULL DEFAULT NULL ,
PRIMARY KEY (`trockid`),
INDEX `characterid` (`characterid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `reports`
-- ----------------------------
DROP TABLE IF EXISTS `reports`;
CREATE TABLE `reports` (
`reportid`  int(9) NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) NOT NULL DEFAULT 0 ,
`type`  tinyint(2) NOT NULL DEFAULT 0 ,
`count`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`reportid`, `characterid`),
INDEX `characterid` (`characterid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `rings`
-- ----------------------------
DROP TABLE IF EXISTS `rings`;
CREATE TABLE `rings` (
`ringid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`partnerRingId`  int(11) NOT NULL DEFAULT 0 ,
`partnerChrId`  int(11) NOT NULL DEFAULT 0 ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
`partnername`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`ringid`),
INDEX `ringid` (`ringid`) USING BTREE ,
INDEX `partnerChrId` (`partnerChrId`) USING BTREE ,
INDEX `partnerRingId` (`partnerRingId`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `savedlocations`
-- ----------------------------
DROP TABLE IF EXISTS `savedlocations`;
CREATE TABLE `savedlocations` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) UNSIGNED NOT NULL ,
`locationtype`  int(11) NOT NULL DEFAULT 0 ,
`map`  int(11) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `savedlocations_ibfk_1` (`characterid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=20748

;

-- ----------------------------
-- Table structure for `scroll_log`
-- ----------------------------
DROP TABLE IF EXISTS `scroll_log`;
CREATE TABLE `scroll_log` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`accId`  int(11) NOT NULL DEFAULT 0 ,
`chrId`  int(11) NOT NULL DEFAULT 0 ,
`scrollId`  int(11) NOT NULL DEFAULT 0 ,
`itemId`  int(11) NOT NULL DEFAULT 0 ,
`oldSlots`  tinyint(4) NOT NULL DEFAULT 0 ,
`newSlots`  tinyint(4) NOT NULL DEFAULT 0 ,
`hammer`  tinyint(4) NOT NULL DEFAULT 0 ,
`result`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`whiteScroll`  tinyint(1) NOT NULL DEFAULT 0 ,
`legendarySpirit`  tinyint(1) NOT NULL DEFAULT 0 ,
`vegaId`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `shopitems`
-- ----------------------------
DROP TABLE IF EXISTS `shopitems`;
CREATE TABLE `shopitems` (
`shopitemid`  int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
`shopid`  int(10) UNSIGNED NOT NULL DEFAULT 0 ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
`price`  int(11) NOT NULL DEFAULT 0 ,
`position`  int(11) NOT NULL DEFAULT 0 ,
`reqitem`  int(11) NOT NULL DEFAULT 0 ,
`reqitemq`  int(11) NOT NULL DEFAULT 0 ,
`pointtype`  int(11) NOT NULL DEFAULT 0 ,
`period`  int(11) NOT NULL DEFAULT 0 ,
`state`  int(11) NOT NULL DEFAULT 0 ,
`category`  tinyint(3) NOT NULL DEFAULT 0 ,
`minLevel`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`shopitemid`),
INDEX `shopid` (`shopid`) USING BTREE 
)
ENGINE=MyISAM
DEFAULT CHARACTER SET=gbk COLLATE=gbk_chinese_ci
AUTO_INCREMENT=9511

;

-- ----------------------------
-- Table structure for `shopranks`
-- ----------------------------
DROP TABLE IF EXISTS `shopranks`;
CREATE TABLE `shopranks` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`shopid`  int(11) NOT NULL DEFAULT 0 ,
`rank`  int(11) NOT NULL DEFAULT 0 ,
`name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=13

;

-- ----------------------------
-- Table structure for `shops`
-- ----------------------------
DROP TABLE IF EXISTS `shops`;
CREATE TABLE `shops` (
`shopid`  int(10) UNSIGNED NOT NULL AUTO_INCREMENT ,
`npcid`  int(11) NULL DEFAULT 0 ,
`shopname`  text CHARACTER SET gbk COLLATE gbk_chinese_ci NULL ,
PRIMARY KEY (`shopid`)
)
ENGINE=MyISAM
DEFAULT CHARACTER SET=gbk COLLATE=gbk_chinese_ci
AUTO_INCREMENT=9310123

;

-- ----------------------------
-- Table structure for `sidekicks`
-- ----------------------------
DROP TABLE IF EXISTS `sidekicks`;
CREATE TABLE `sidekicks` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`firstid`  int(11) NOT NULL DEFAULT 0 ,
`secondid`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `skillmacros`
-- ----------------------------
DROP TABLE IF EXISTS `skillmacros`;
CREATE TABLE `skillmacros` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) NOT NULL DEFAULT 0 ,
`position`  tinyint(1) NOT NULL DEFAULT 0 ,
`skill1`  int(11) NOT NULL DEFAULT 0 ,
`skill2`  int(11) NOT NULL DEFAULT 0 ,
`skill3`  int(11) NOT NULL DEFAULT 0 ,
`name`  varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`shout`  tinyint(1) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
INDEX `characterid` (`characterid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=3376

;

-- ----------------------------
-- Table structure for `skills`
-- ----------------------------
DROP TABLE IF EXISTS `skills`;
CREATE TABLE `skills` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`skillid`  int(11) NOT NULL DEFAULT 0 ,
`characterid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`skilllevel`  int(11) NOT NULL DEFAULT 0 ,
`masterlevel`  tinyint(4) NOT NULL DEFAULT 0 ,
`expiration`  bigint(20) NOT NULL DEFAULT '-1' ,
`victimid`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `skills_ibfk_1` (`characterid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=12891030

;

-- ----------------------------
-- Table structure for `skills_cooldowns`
-- ----------------------------
DROP TABLE IF EXISTS `skills_cooldowns`;
CREATE TABLE `skills_cooldowns` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`charid`  int(11) NOT NULL ,
`SkillID`  int(11) NOT NULL ,
`length`  bigint(20) NOT NULL ,
`StartTime`  bigint(20) UNSIGNED NOT NULL ,
PRIMARY KEY (`id`),
INDEX `charid` (`charid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=7198

;

-- ----------------------------
-- Table structure for `speedruns`
-- ----------------------------
DROP TABLE IF EXISTS `speedruns`;
CREATE TABLE `speedruns` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`type`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`leader`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`timestring`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`time`  bigint(20) NOT NULL DEFAULT 0 ,
`members`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=4408

;

-- ----------------------------
-- Table structure for `stolen`
-- ----------------------------
DROP TABLE IF EXISTS `stolen`;
CREATE TABLE `stolen` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`skillid`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`chosen`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `storages`
-- ----------------------------
DROP TABLE IF EXISTS `storages`;
CREATE TABLE `storages` (
`storageid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`accountid`  int(11) UNSIGNED NOT NULL DEFAULT 0 ,
`slots`  int(11) NOT NULL DEFAULT 0 ,
`meso`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`storageid`),
FOREIGN KEY (`accountid`) REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
INDEX `accountid` (`accountid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=727

;

-- ----------------------------
-- Table structure for `tournamentlog`
-- ----------------------------
DROP TABLE IF EXISTS `tournamentlog`;
CREATE TABLE `tournamentlog` (
`logid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`winnerid`  int(11) NOT NULL DEFAULT 0 ,
`numContestants`  int(11) NOT NULL DEFAULT 0 ,
`when`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
PRIMARY KEY (`logid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `trocklocations`
-- ----------------------------
DROP TABLE IF EXISTS `trocklocations`;
CREATE TABLE `trocklocations` (
`trockid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`characterid`  int(11) NULL DEFAULT NULL ,
`mapid`  int(11) NULL DEFAULT NULL ,
PRIMARY KEY (`trockid`),
INDEX `characterid` (`characterid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=2

;

-- ----------------------------
-- Table structure for `trolled`
-- ----------------------------
DROP TABLE IF EXISTS `trolled`;
CREATE TABLE `trolled` (
`trolledid`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`trolledid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `vote_log`
-- ----------------------------
DROP TABLE IF EXISTS `vote_log`;
CREATE TABLE `vote_log` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`account`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`ip`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`date`  int(11) UNSIGNED NOT NULL ,
`times`  int(11) UNSIGNED NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `votecontrol`
-- ----------------------------
DROP TABLE IF EXISTS `votecontrol`;
CREATE TABLE `votecontrol` (
`name`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`time`  int(11) NOT NULL ,
PRIMARY KEY (`name`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `votecontrolgtop`
-- ----------------------------
DROP TABLE IF EXISTS `votecontrolgtop`;
CREATE TABLE `votecontrolgtop` (
`name`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`time`  int(11) NOT NULL ,
PRIMARY KEY (`name`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `voteipcontrol`
-- ----------------------------
DROP TABLE IF EXISTS `voteipcontrol`;
CREATE TABLE `voteipcontrol` (
`ip`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`time`  int(11) NOT NULL ,
PRIMARY KEY (`ip`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `voteipcontrolgtop`
-- ----------------------------
DROP TABLE IF EXISTS `voteipcontrolgtop`;
CREATE TABLE `voteipcontrolgtop` (
`ip`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`time`  int(11) NOT NULL ,
PRIMARY KEY (`ip`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `voterewards`
-- ----------------------------
DROP TABLE IF EXISTS `voterewards`;
CREATE TABLE `voterewards` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`name`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`claimed`  tinyint(3) UNSIGNED NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `voterewardsgtop`
-- ----------------------------
DROP TABLE IF EXISTS `voterewardsgtop`;
CREATE TABLE `voterewardsgtop` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`name`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`claimed`  tinyint(3) UNSIGNED NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `votingrecords`
-- ----------------------------
DROP TABLE IF EXISTS `votingrecords`;
CREATE TABLE `votingrecords` (
`account`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' ,
`ip`  varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' ,
`date`  int(11) NOT NULL DEFAULT 0 ,
`times`  bigint(20) UNSIGNED NOT NULL DEFAULT 0 ,
PRIMARY KEY (`account`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `web_logs`
-- ----------------------------
DROP TABLE IF EXISTS `web_logs`;
CREATE TABLE `web_logs` (
`idweb_logs`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
PRIMARY KEY (`idweb_logs`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `wishlist`
-- ----------------------------
DROP TABLE IF EXISTS `wishlist`;
CREATE TABLE `wishlist` (
`characterid`  int(11) NOT NULL ,
`sn`  int(11) NOT NULL ,
INDEX `characterid` (`characterid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `wz_clearedlife`
-- ----------------------------
DROP TABLE IF EXISTS `wz_clearedlife`;
CREATE TABLE `wz_clearedlife` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`mapid`  int(11) NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `wz_customlife`
-- ----------------------------
DROP TABLE IF EXISTS `wz_customlife`;
CREATE TABLE `wz_customlife` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`idd`  int(11) NOT NULL ,
`f`  int(11) NOT NULL ,
`fh`  int(11) NOT NULL ,
`type`  varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`cy`  int(11) NOT NULL ,
`rx0`  int(11) NOT NULL ,
`rx1`  int(11) NOT NULL ,
`x`  int(11) NOT NULL ,
`y`  int(11) NOT NULL ,
`mobtime`  int(11) NULL DEFAULT 1000 ,
`mid`  int(11) NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `wz_itemadddata`
-- ----------------------------
DROP TABLE IF EXISTS `wz_itemadddata`;
CREATE TABLE `wz_itemadddata` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`itemid`  int(11) NOT NULL ,
`key`  varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`value1`  int(11) NOT NULL DEFAULT 0 ,
`value2`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=4074

;

-- ----------------------------
-- Table structure for `wz_itemdata`
-- ----------------------------
DROP TABLE IF EXISTS `wz_itemdata`;
CREATE TABLE `wz_itemdata` (
`itemid`  int(11) NOT NULL ,
`name`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`msg`  varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`desc`  varchar(4096) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`slotMax`  smallint(5) NOT NULL DEFAULT 1 ,
`price`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '-1.0' ,
`wholePrice`  int(11) NOT NULL DEFAULT '-1' ,
`stateChange`  int(11) NOT NULL DEFAULT 0 ,
`flags`  smallint(4) NOT NULL DEFAULT 0 ,
`karma`  tinyint(1) NOT NULL DEFAULT 0 ,
`meso`  int(11) NOT NULL DEFAULT 0 ,
`monsterBook`  int(11) NOT NULL DEFAULT 0 ,
`itemMakeLevel`  smallint(6) NOT NULL DEFAULT 0 ,
`questId`  int(11) NOT NULL DEFAULT 0 ,
`scrollReqs`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`consumeItem`  tinytext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`totalprob`  int(11) NOT NULL DEFAULT 0 ,
`incSkill`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`replaceid`  int(11) NOT NULL DEFAULT 0 ,
`replacemsg`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`create`  int(11) NOT NULL DEFAULT 0 ,
`afterImage`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
PRIMARY KEY (`itemid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `wz_itemequipdata`
-- ----------------------------
DROP TABLE IF EXISTS `wz_itemequipdata`;
CREATE TABLE `wz_itemequipdata` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`itemid`  int(11) NOT NULL ,
`itemLevel`  int(11) NOT NULL DEFAULT '-1' ,
`key`  varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`value`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1336893

;

-- ----------------------------
-- Table structure for `wz_itemrewarddata`
-- ----------------------------
DROP TABLE IF EXISTS `wz_itemrewarddata`;
CREATE TABLE `wz_itemrewarddata` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`itemid`  int(11) NOT NULL ,
`item`  int(11) NOT NULL ,
`prob`  int(11) NOT NULL DEFAULT 0 ,
`quantity`  smallint(5) NOT NULL DEFAULT 0 ,
`period`  int(11) NOT NULL DEFAULT '-1' ,
`worldMsg`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`effect`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=56983

;

-- ----------------------------
-- Table structure for `wz_mobskilldata`
-- ----------------------------
DROP TABLE IF EXISTS `wz_mobskilldata`;
CREATE TABLE `wz_mobskilldata` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`skillid`  int(11) NOT NULL ,
`level`  int(11) NOT NULL ,
`hp`  int(11) NOT NULL DEFAULT 100 ,
`mpcon`  int(11) NOT NULL DEFAULT 0 ,
`x`  int(11) NOT NULL DEFAULT 1 ,
`y`  int(11) NOT NULL DEFAULT 1 ,
`time`  int(11) NOT NULL DEFAULT 0 ,
`prop`  int(11) NOT NULL DEFAULT 100 ,
`limit`  int(11) NOT NULL DEFAULT 0 ,
`spawneffect`  int(11) NOT NULL DEFAULT 0 ,
`interval`  int(11) NOT NULL DEFAULT 0 ,
`summons`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`ltx`  int(11) NOT NULL DEFAULT 0 ,
`lty`  int(11) NOT NULL DEFAULT 0 ,
`rbx`  int(11) NOT NULL DEFAULT 0 ,
`rby`  int(11) NOT NULL DEFAULT 0 ,
`once`  tinyint(1) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1528

;

-- ----------------------------
-- Table structure for `wz_oxdata`
-- ----------------------------
DROP TABLE IF EXISTS `wz_oxdata`;
CREATE TABLE `wz_oxdata` (
`questionset`  smallint(6) NOT NULL DEFAULT 0 ,
`questionid`  smallint(6) NOT NULL DEFAULT 0 ,
`question`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`display`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`answer`  enum('o','x') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`questionset`, `questionid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `wz_questactdata`
-- ----------------------------
DROP TABLE IF EXISTS `wz_questactdata`;
CREATE TABLE `wz_questactdata` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`questid`  int(11) NOT NULL DEFAULT 0 ,
`name`  varchar(127) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`type`  tinyint(1) NOT NULL DEFAULT 0 ,
`intStore`  int(11) NOT NULL DEFAULT 0 ,
`applicableJobs`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`uniqueid`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
INDEX `quests_ibfk_2` (`questid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=13718

;

-- ----------------------------
-- Table structure for `wz_questactitemdata`
-- ----------------------------
DROP TABLE IF EXISTS `wz_questactitemdata`;
CREATE TABLE `wz_questactitemdata` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`itemid`  int(11) NOT NULL DEFAULT 0 ,
`count`  smallint(5) NOT NULL DEFAULT 0 ,
`period`  int(11) NOT NULL DEFAULT 0 ,
`gender`  tinyint(1) NOT NULL DEFAULT 2 ,
`job`  int(11) NOT NULL DEFAULT '-1' ,
`jobEx`  int(11) NOT NULL DEFAULT '-1' ,
`prop`  int(11) NOT NULL DEFAULT '-1' ,
`uniqueid`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=16175

;

-- ----------------------------
-- Table structure for `wz_questactquestdata`
-- ----------------------------
DROP TABLE IF EXISTS `wz_questactquestdata`;
CREATE TABLE `wz_questactquestdata` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`quest`  int(11) NOT NULL DEFAULT 0 ,
`state`  tinyint(1) NOT NULL DEFAULT 2 ,
`uniqueid`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=38

;

-- ----------------------------
-- Table structure for `wz_questactskilldata`
-- ----------------------------
DROP TABLE IF EXISTS `wz_questactskilldata`;
CREATE TABLE `wz_questactskilldata` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`skillid`  int(11) NOT NULL DEFAULT 0 ,
`skillLevel`  int(11) NOT NULL DEFAULT '-1' ,
`masterLevel`  int(11) NOT NULL DEFAULT '-1' ,
`uniqueid`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=258

;

-- ----------------------------
-- Table structure for `wz_questdata`
-- ----------------------------
DROP TABLE IF EXISTS `wz_questdata`;
CREATE TABLE `wz_questdata` (
`questid`  int(11) NOT NULL ,
`name`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`autoStart`  tinyint(1) NOT NULL DEFAULT 0 ,
`autoPreComplete`  tinyint(1) NOT NULL DEFAULT 0 ,
`viewMedalItem`  int(11) NOT NULL DEFAULT 0 ,
`selectedSkillID`  int(11) NOT NULL DEFAULT 0 ,
`blocked`  tinyint(1) NOT NULL DEFAULT 0 ,
`autoAccept`  tinyint(1) NOT NULL DEFAULT 0 ,
`autoComplete`  tinyint(1) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`questid`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Table structure for `wz_questpartydata`
-- ----------------------------
DROP TABLE IF EXISTS `wz_questpartydata`;
CREATE TABLE `wz_questpartydata` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`questid`  int(11) NOT NULL DEFAULT 0 ,
`rank`  varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`mode`  varchar(13) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`property`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`value`  int(11) NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
INDEX `quests_ibfk_7` (`questid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=81

;

-- ----------------------------
-- Table structure for `wz_questreqdata`
-- ----------------------------
DROP TABLE IF EXISTS `wz_questreqdata`;
CREATE TABLE `wz_questreqdata` (
`id`  int(11) UNSIGNED NOT NULL AUTO_INCREMENT ,
`questid`  int(11) NOT NULL DEFAULT 0 ,
`name`  varchar(127) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`type`  tinyint(1) NOT NULL DEFAULT 0 ,
`stringStore`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`intStoresFirst`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
`intStoresSecond`  varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' ,
PRIMARY KEY (`id`),
INDEX `quests_ibfk_1` (`questid`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=59629

;

-- ----------------------------
-- Auto increment value for `accounts`
-- ----------------------------
ALTER TABLE `accounts` AUTO_INCREMENT=869;

-- ----------------------------
-- Auto increment value for `accounts_event`
-- ----------------------------
ALTER TABLE `accounts_event` AUTO_INCREMENT=41569;

-- ----------------------------
-- Auto increment value for `achievements`
-- ----------------------------
ALTER TABLE `achievements` AUTO_INCREMENT=71368101;

-- ----------------------------
-- Auto increment value for `alliances`
-- ----------------------------
ALTER TABLE `alliances` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `androids`
-- ----------------------------
ALTER TABLE `androids` AUTO_INCREMENT=156466;

-- ----------------------------
-- Auto increment value for `bbs_replies`
-- ----------------------------
ALTER TABLE `bbs_replies` AUTO_INCREMENT=26;

-- ----------------------------
-- Auto increment value for `bbs_threads`
-- ----------------------------
ALTER TABLE `bbs_threads` AUTO_INCREMENT=168;

-- ----------------------------
-- Auto increment value for `bosslog`
-- ----------------------------
ALTER TABLE `bosslog` AUTO_INCREMENT=37393;

-- ----------------------------
-- Auto increment value for `buddies`
-- ----------------------------
ALTER TABLE `buddies` AUTO_INCREMENT=133817;

-- ----------------------------
-- Auto increment value for `character_cards`
-- ----------------------------
ALTER TABLE `character_cards` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `character_slots`
-- ----------------------------
ALTER TABLE `character_slots` AUTO_INCREMENT=741;

-- ----------------------------
-- Auto increment value for `characters`
-- ----------------------------
ALTER TABLE `characters` AUTO_INCREMENT=2857;

-- ----------------------------
-- Auto increment value for `cheatlog`
-- ----------------------------
ALTER TABLE `cheatlog` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `csequipment`
-- ----------------------------
ALTER TABLE `csequipment` AUTO_INCREMENT=60836765;

-- ----------------------------
-- Auto increment value for `csitems`
-- ----------------------------
ALTER TABLE `csitems` AUTO_INCREMENT=80931564;

-- ----------------------------
-- Auto increment value for `dojo_ranks`
-- ----------------------------
ALTER TABLE `dojo_ranks` AUTO_INCREMENT=42;

-- ----------------------------
-- Auto increment value for `dojo_ranks_month`
-- ----------------------------
ALTER TABLE `dojo_ranks_month` AUTO_INCREMENT=18;

-- ----------------------------
-- Auto increment value for `donation`
-- ----------------------------
ALTER TABLE `donation` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `donorlog`
-- ----------------------------
ALTER TABLE `donorlog` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `drop_data`
-- ----------------------------
ALTER TABLE `drop_data` AUTO_INCREMENT=45503;

-- ----------------------------
-- Auto increment value for `drop_data_global`
-- ----------------------------
ALTER TABLE `drop_data_global` AUTO_INCREMENT=8;

-- ----------------------------
-- Auto increment value for `dueyequipment`
-- ----------------------------
ALTER TABLE `dueyequipment` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `dueyitems`
-- ----------------------------
ALTER TABLE `dueyitems` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `dueypackages`
-- ----------------------------
ALTER TABLE `dueypackages` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `equipgrave`
-- ----------------------------
ALTER TABLE `equipgrave` AUTO_INCREMENT=1660;

-- ----------------------------
-- Auto increment value for `extendedslots`
-- ----------------------------
ALTER TABLE `extendedslots` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `famelog`
-- ----------------------------
ALTER TABLE `famelog` AUTO_INCREMENT=723;

-- ----------------------------
-- Auto increment value for `familiars`
-- ----------------------------
ALTER TABLE `familiars` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `families`
-- ----------------------------
ALTER TABLE `families` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `gifts`
-- ----------------------------
ALTER TABLE `gifts` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `giftsender`
-- ----------------------------
ALTER TABLE `giftsender` AUTO_INCREMENT=5450;

-- ----------------------------
-- Auto increment value for `gmlog`
-- ----------------------------
ALTER TABLE `gmlog` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `guilds`
-- ----------------------------
ALTER TABLE `guilds` AUTO_INCREMENT=27;

-- ----------------------------
-- Auto increment value for `guildskills`
-- ----------------------------
ALTER TABLE `guildskills` AUTO_INCREMENT=269;

-- ----------------------------
-- Auto increment value for `hidelog`
-- ----------------------------
ALTER TABLE `hidelog` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `hiredmerch`
-- ----------------------------
ALTER TABLE `hiredmerch` AUTO_INCREMENT=5897;

-- ----------------------------
-- Auto increment value for `hiredmerchequipment`
-- ----------------------------
ALTER TABLE `hiredmerchequipment` AUTO_INCREMENT=11572;

-- ----------------------------
-- Auto increment value for `hiredmerchitems`
-- ----------------------------
ALTER TABLE `hiredmerchitems` AUTO_INCREMENT=31018;

-- ----------------------------
-- Auto increment value for `hyperrocklocations`
-- ----------------------------
ALTER TABLE `hyperrocklocations` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `imps`
-- ----------------------------
ALTER TABLE `imps` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `inner_ability_skills`
-- ----------------------------
ALTER TABLE `inner_ability_skills` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `internlog`
-- ----------------------------
ALTER TABLE `internlog` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `inventoryequipment`
-- ----------------------------
ALTER TABLE `inventoryequipment` AUTO_INCREMENT=254641182;

-- ----------------------------
-- Auto increment value for `inventoryitems`
-- ----------------------------
ALTER TABLE `inventoryitems` AUTO_INCREMENT=851575849;

-- ----------------------------
-- Auto increment value for `inventorylog`
-- ----------------------------
ALTER TABLE `inventorylog` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `inventoryslot`
-- ----------------------------
ALTER TABLE `inventoryslot` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `ipbans`
-- ----------------------------
ALTER TABLE `ipbans` AUTO_INCREMENT=2;

-- ----------------------------
-- Auto increment value for `iplog`
-- ----------------------------
ALTER TABLE `iplog` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `ipvotelog`
-- ----------------------------
ALTER TABLE `ipvotelog` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `ipvotes`
-- ----------------------------
ALTER TABLE `ipvotes` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `keymap`
-- ----------------------------
ALTER TABLE `keymap` AUTO_INCREMENT=43194530;

-- ----------------------------
-- Auto increment value for `lottery`
-- ----------------------------
ALTER TABLE `lottery` AUTO_INCREMENT=1606;

-- ----------------------------
-- Auto increment value for `macbans`
-- ----------------------------
ALTER TABLE `macbans` AUTO_INCREMENT=3;

-- ----------------------------
-- Auto increment value for `macfilters`
-- ----------------------------
ALTER TABLE `macfilters` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `monsterbook`
-- ----------------------------
ALTER TABLE `monsterbook` AUTO_INCREMENT=24379;

-- ----------------------------
-- Auto increment value for `mountdata`
-- ----------------------------
ALTER TABLE `mountdata` AUTO_INCREMENT=2857;

-- ----------------------------
-- Auto increment value for `mts_cart`
-- ----------------------------
ALTER TABLE `mts_cart` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `mtsequipment`
-- ----------------------------
ALTER TABLE `mtsequipment` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `mtsitems`
-- ----------------------------
ALTER TABLE `mtsitems` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `mtstransfer`
-- ----------------------------
ALTER TABLE `mtstransfer` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `mtstransferequipment`
-- ----------------------------
ALTER TABLE `mtstransferequipment` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `myinfo`
-- ----------------------------
ALTER TABLE `myinfo` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `notes`
-- ----------------------------
ALTER TABLE `notes` AUTO_INCREMENT=22;

-- ----------------------------
-- Auto increment value for `paybill_bills`
-- ----------------------------
ALTER TABLE `paybill_bills` AUTO_INCREMENT=502;

-- ----------------------------
-- Auto increment value for `paybill_paylog`
-- ----------------------------
ALTER TABLE `paybill_paylog` AUTO_INCREMENT=160;

-- ----------------------------
-- Auto increment value for `pets`
-- ----------------------------
ALTER TABLE `pets` AUTO_INCREMENT=157230;

-- ----------------------------
-- Auto increment value for `playernpcs`
-- ----------------------------
ALTER TABLE `playernpcs` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `playernpcs_equip`
-- ----------------------------
ALTER TABLE `playernpcs_equip` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `pokemon`
-- ----------------------------
ALTER TABLE `pokemon` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `proxyip`
-- ----------------------------
ALTER TABLE `proxyip` AUTO_INCREMENT=10;

-- ----------------------------
-- Auto increment value for `pwreset`
-- ----------------------------
ALTER TABLE `pwreset` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `questinfo`
-- ----------------------------
ALTER TABLE `questinfo` AUTO_INCREMENT=16525;

-- ----------------------------
-- Auto increment value for `queststatus`
-- ----------------------------
ALTER TABLE `queststatus` AUTO_INCREMENT=195529068;

-- ----------------------------
-- Auto increment value for `queststatusmobs`
-- ----------------------------
ALTER TABLE `queststatusmobs` AUTO_INCREMENT=1451130;

-- ----------------------------
-- Auto increment value for `reactordrops`
-- ----------------------------
ALTER TABLE `reactordrops` AUTO_INCREMENT=841;

-- ----------------------------
-- Auto increment value for `regrocklocations`
-- ----------------------------
ALTER TABLE `regrocklocations` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `reports`
-- ----------------------------
ALTER TABLE `reports` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `rings`
-- ----------------------------
ALTER TABLE `rings` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `savedlocations`
-- ----------------------------
ALTER TABLE `savedlocations` AUTO_INCREMENT=20748;

-- ----------------------------
-- Auto increment value for `scroll_log`
-- ----------------------------
ALTER TABLE `scroll_log` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `shopitems`
-- ----------------------------
ALTER TABLE `shopitems` AUTO_INCREMENT=9511;

-- ----------------------------
-- Auto increment value for `shopranks`
-- ----------------------------
ALTER TABLE `shopranks` AUTO_INCREMENT=13;

-- ----------------------------
-- Auto increment value for `shops`
-- ----------------------------
ALTER TABLE `shops` AUTO_INCREMENT=9310123;

-- ----------------------------
-- Auto increment value for `sidekicks`
-- ----------------------------
ALTER TABLE `sidekicks` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `skillmacros`
-- ----------------------------
ALTER TABLE `skillmacros` AUTO_INCREMENT=3376;

-- ----------------------------
-- Auto increment value for `skills`
-- ----------------------------
ALTER TABLE `skills` AUTO_INCREMENT=12891030;

-- ----------------------------
-- Auto increment value for `skills_cooldowns`
-- ----------------------------
ALTER TABLE `skills_cooldowns` AUTO_INCREMENT=7198;

-- ----------------------------
-- Auto increment value for `speedruns`
-- ----------------------------
ALTER TABLE `speedruns` AUTO_INCREMENT=4408;

-- ----------------------------
-- Auto increment value for `stolen`
-- ----------------------------
ALTER TABLE `stolen` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `storages`
-- ----------------------------
ALTER TABLE `storages` AUTO_INCREMENT=727;

-- ----------------------------
-- Auto increment value for `tournamentlog`
-- ----------------------------
ALTER TABLE `tournamentlog` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `trocklocations`
-- ----------------------------
ALTER TABLE `trocklocations` AUTO_INCREMENT=2;

-- ----------------------------
-- Auto increment value for `trolled`
-- ----------------------------
ALTER TABLE `trolled` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `vote_log`
-- ----------------------------
ALTER TABLE `vote_log` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `voterewards`
-- ----------------------------
ALTER TABLE `voterewards` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `voterewardsgtop`
-- ----------------------------
ALTER TABLE `voterewardsgtop` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `web_logs`
-- ----------------------------
ALTER TABLE `web_logs` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `wz_clearedlife`
-- ----------------------------
ALTER TABLE `wz_clearedlife` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `wz_customlife`
-- ----------------------------
ALTER TABLE `wz_customlife` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `wz_itemadddata`
-- ----------------------------
ALTER TABLE `wz_itemadddata` AUTO_INCREMENT=4074;

-- ----------------------------
-- Auto increment value for `wz_itemequipdata`
-- ----------------------------
ALTER TABLE `wz_itemequipdata` AUTO_INCREMENT=1336893;

-- ----------------------------
-- Auto increment value for `wz_itemrewarddata`
-- ----------------------------
ALTER TABLE `wz_itemrewarddata` AUTO_INCREMENT=56983;

-- ----------------------------
-- Auto increment value for `wz_mobskilldata`
-- ----------------------------
ALTER TABLE `wz_mobskilldata` AUTO_INCREMENT=1528;

-- ----------------------------
-- Auto increment value for `wz_questactdata`
-- ----------------------------
ALTER TABLE `wz_questactdata` AUTO_INCREMENT=13718;

-- ----------------------------
-- Auto increment value for `wz_questactitemdata`
-- ----------------------------
ALTER TABLE `wz_questactitemdata` AUTO_INCREMENT=16175;

-- ----------------------------
-- Auto increment value for `wz_questactquestdata`
-- ----------------------------
ALTER TABLE `wz_questactquestdata` AUTO_INCREMENT=38;

-- ----------------------------
-- Auto increment value for `wz_questactskilldata`
-- ----------------------------
ALTER TABLE `wz_questactskilldata` AUTO_INCREMENT=258;

-- ----------------------------
-- Auto increment value for `wz_questpartydata`
-- ----------------------------
ALTER TABLE `wz_questpartydata` AUTO_INCREMENT=81;

-- ----------------------------
-- Auto increment value for `wz_questreqdata`
-- ----------------------------
ALTER TABLE `wz_questreqdata` AUTO_INCREMENT=59629;
