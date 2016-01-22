/*
SQLyog Ultimate v8.32 
MySQL - 5.5.27 : Database - tuomatuo
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`tuomatuo` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `tuomatuo`;

/*Table structure for table `dynamiccomment` */

DROP TABLE IF EXISTS `dynamiccomment`;

CREATE TABLE `dynamiccomment` (
  `id` bigint(23) NOT NULL AUTO_INCREMENT,
  `dyId` bigint(23) DEFAULT NULL,
  `userId` bigint(23) DEFAULT NULL,
  `content` varchar(256) DEFAULT NULL COMMENT '内容',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `approve` int(11) DEFAULT NULL COMMENT '赞同数',
  `oppose` int(11) DEFAULT NULL COMMENT '不赞同数',
  `updateTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `dynamiccomment` */

/*Table structure for table `dynamicimg` */

DROP TABLE IF EXISTS `dynamicimg`;

CREATE TABLE `dynamicimg` (
  `id` bigint(23) NOT NULL AUTO_INCREMENT,
  `userId` bigint(23) DEFAULT NULL,
  `height` int(11) DEFAULT NULL,
  `width` int(11) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL COMMENT '图片大小',
  `url` varchar(256) DEFAULT NULL COMMENT '图片存储的url',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `dynamicimg` */

/*Table structure for table `dynamiclove` */

DROP TABLE IF EXISTS `dynamiclove`;

CREATE TABLE `dynamiclove` (
  `id` bigint(23) NOT NULL AUTO_INCREMENT,
  `dyId` bigint(23) DEFAULT NULL COMMENT '用户动态id',
  `loveId` bigint(23) DEFAULT NULL COMMENT '点赞人的id',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `dynamiclove` */

/*Table structure for table `friend` */

DROP TABLE IF EXISTS `friend`;

CREATE TABLE `friend` (
  `id` bigint(23) NOT NULL AUTO_INCREMENT,
  `userId` bigint(23) DEFAULT NULL,
  `friendId` bigint(23) DEFAULT NULL,
  `status` tinyint(3) DEFAULT NULL COMMENT '状态 0 无效, 1 有效',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `friend` */

/*Table structure for table `qqaccount` */

DROP TABLE IF EXISTS `qqaccount`;

CREATE TABLE `qqaccount` (
  `id` bigint(23) NOT NULL AUTO_INCREMENT,
  `qqId` varchar(256) NOT NULL COMMENT 'qq账号唯一id',
  `qqImgUrl` varchar(256) DEFAULT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `qqaccount` */

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint(23) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `mobile` varchar(64) DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `accountType` tinyint(3) DEFAULT NULL COMMENT '账户类型, 0 普通账户, 1 微信, 2 QQ',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `thirdAccountId` bigint(20) DEFAULT NULL COMMENT '第三方账户的id',
  `lastSynMemTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '上次同步数据到内存的时间',
  `status` tinyint(3) DEFAULT NULL COMMENT '用户状态 1 OK， 2 拉黑',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `user` */

/*Table structure for table `userdynamic` */

DROP TABLE IF EXISTS `userdynamic`;

CREATE TABLE `userdynamic` (
  `id` bigint(23) NOT NULL AUTO_INCREMENT,
  `userId` bigint(23) DEFAULT NULL,
  `type` tinyint(3) DEFAULT NULL COMMENT '动态的类别 0 图片, 1, 语音, 2 视频',
  `love` bigint(23) DEFAULT NULL COMMENT '被点赞的次数',
  `fromType` tinyint(3) DEFAULT NULL COMMENT '动态来源 0 自己发, 1 转发',
  `longitude` varchar(256) DEFAULT NULL COMMENT '经度',
  `latitude` varchar(256) DEFAULT NULL COMMENT '纬度',
  `storagePolicy` tinyint(3) DEFAULT NULL COMMENT '存储策略 0 七牛, 1 又拍云',
  `dynamicContentId` bigint(23) DEFAULT NULL COMMENT '用户动态内容的id',
  `dynamicSeeSum` bigint(23) DEFAULT NULL COMMENT '用户动态被观看的次数',
  `dynamicRecommend` bigint(23) DEFAULT NULL COMMENT '用户动态转发推荐次数',
  `createTime` timestamp NULL DEFAULT NULL,
  `updateTime` timestamp NULL DEFAULT NULL,
  `hotValue` bigint(20) DEFAULT NULL COMMENT '用户动态的热力值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `userdynamic` */

/*Table structure for table `userposition` */

DROP TABLE IF EXISTS `userposition`;

CREATE TABLE `userposition` (
  `id` bigint(23) NOT NULL AUTO_INCREMENT,
  `userId` bigint(23) NOT NULL,
  `longitude` varchar(64) DEFAULT NULL COMMENT '经度',
  `latitude` varchar(64) DEFAULT NULL COMMENT '纬度',
  `geohash` varchar(64) DEFAULT NULL COMMENT 'Geohash 值',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `userposition` */

/*Table structure for table `userproperty` */

DROP TABLE IF EXISTS `userproperty`;

CREATE TABLE `userproperty` (
  `id` bigint(23) NOT NULL AUTO_INCREMENT,
  `userId` bigint(23) DEFAULT NULL,
  `sex` tinyint(3) DEFAULT NULL COMMENT ' 性别, 0 女, 1 男',
  `age` int(11) DEFAULT NULL,
  `popular` bigint(23) DEFAULT NULL COMMENT '用户人气指数',
  `fansSum` bigint(23) DEFAULT NULL COMMENT '粉丝数',
  `dynamicSum` bigint(23) DEFAULT NULL COMMENT '动态数',
  `followSum` bigint(23) DEFAULT NULL COMMENT '关注数',
  `dynamicSeeTotal` bigint(23) DEFAULT NULL COMMENT '所有动态被看的总数',
  `loveTotal` bigint(23) DEFAULT NULL COMMENT '被赞的总数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `userproperty` */

/*Table structure for table `weixinaccount` */

DROP TABLE IF EXISTS `weixinaccount`;

CREATE TABLE `weixinaccount` (
  `id` bigint(23) NOT NULL AUTO_INCREMENT,
  `weiXinId` varchar(256) DEFAULT NULL COMMENT '微信账户的唯一id',
  `weiXinImgUrl` varchar(256) DEFAULT NULL COMMENT 'qq账户的唯一id',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `weixinaccount` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
