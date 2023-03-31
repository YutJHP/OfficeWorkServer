DROP DATABASE IF EXISTS `ChatApp`;
CREATE DATABASE IF NOT EXISTS `ChatApp`;
USE `ChatApp`;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;

CREATE TABLE `User` (
	`User_ID` bigint(10) NOT NULL,
    `Username` varchar(25) DEFAULT NULL,
    `Password` varchar(25) DEFAULT NULL,
    `Fname` varchar(30) DEFAULT NULL,
    `Lname` varchar(30) DEFAULT NULL,
    `Email` varchar(40) DEFAULT NULL,
    `Active_Status` enum('online', 'offline', 'busy', 'away') DEFAULT NULL,
    `Position` varchar(20) DEFAULT NULL,
    PRIMARY KEY (`User_ID`),
    UNIQUE (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `Message`
--

DROP TABLE IF EXISTS `Message`;

CREATE TABLE `Message` (
	`Message_ID` bigint(10) NOT NULL,
    `MessageText` varchar(5000) DEFAULT NULL,
    `Date_Created` datetime DEFAULT NULL,
    PRIMARY KEY (`Message_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `messageOwner`
--

DROP TABLE IF EXISTS `messageOwner`;

CREATE TABLE `messageOwner` (
	`User_Message_ID` varchar(10) NOT NULL,
    `Archived_Status` enum('Archived', 'Inboxed', 'Deleted') DEFAULT NULL,
    `Sender` bool DEFAULT NULL,
    `Receiver` bool DEFAULT NULL,
    `User_ID` int(10) DEFAULT NULL,
    `Message_ID` int(10) DEFAULT NULL,
    PRIMARY KEY (`User_Message_ID`),
    KEY `User_ID` (`User_ID`),
    KEY `Message_ID` (`Message_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `Group`
--

DROP TABLE IF EXISTS `Group`;

CREATE TABLE `Group` (
	`Group_ID` bigint(10) NOT NULL,
    `GroupName` varchar(15) DEFAULT NULL,
    PRIMARY KEY (`Group_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `groupMembers`
--

DROP TABLE IF EXISTS `groupMembers`;

CREATE TABLE `groupMembers` (
	`groupMember_ID` bigint(10) NOT NULL,
    `User_ID` int(10) DEFAULT NULL,
    `Group_ID` int(10) DEFAULT NULL,
    PRIMARY KEY (`groupMember_ID`),
    KEY `User_ID` (`User_ID`),
    KEY `Group_ID` (`Group_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;