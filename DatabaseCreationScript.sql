SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `SculptyWorldDB` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `SculptyWorldDB` ;

-- -----------------------------------------------------
-- Table `SculptyWorldDB`.`UserTable`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `SculptyWorldDB`.`UserTable` (
  `User_ID` INT NOT NULL ,
  `User_Name` VARCHAR(20) NOT NULL ,
  `User_Password` VARCHAR(20) NOT NULL ,
  PRIMARY KEY (`User_ID`) )
ENGINE = InnoDB
COMMENT = 'Tracks all the account information for the users.' ;


-- -----------------------------------------------------
-- Table `SculptyWorldDB`.`PlayerTable`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `SculptyWorldDB`.`PlayerTable` (
  `User_ID` INT NOT NULL ,
  `Player_Name` VARCHAR(45) NOT NULL ,
  `Player_Data` LONGTEXT NULL ,
  PRIMARY KEY (`Player_Name`) ,
  INDEX `User_ID` (`User_ID` ASC) ,
  CONSTRAINT `User_ID`
    FOREIGN KEY (`User_ID` )
    REFERENCES `SculptyWorldDB`.`UserTable` (`User_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SculptyWorldDB`.`RoomTable`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `SculptyWorldDB`.`RoomTable` (
  `Room_ID` INT NOT NULL ,
  `Room_Data` LONGTEXT NULL COMMENT 'Data stored in XML format.' ,
  PRIMARY KEY (`Room_ID`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SculptyWorldDB`.`CreatureTable`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `SculptyWorldDB`.`CreatureTable` (
  `Creature_ID` INT NOT NULL ,
  `Creature_Data` LONGTEXT NULL COMMENT 'Creature Data stored in XML format.' ,
  PRIMARY KEY (`Creature_ID`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `SculptyWorldDB`.`NumberingTable`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `SculptyWorldDB`.`NumberingTable` (
  `Table_Name` VARCHAR(40) NOT NULL ,
  `Next_Number` INT NOT NULL ,
  PRIMARY KEY (`Table_Name`) )
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;