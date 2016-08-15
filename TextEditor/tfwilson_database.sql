DROP DATABASE if exists TEditDatabase2;

CREATE DATABASE TEditDatabase2;

USE TEditDatabase2;
CREATE TABLE UserInfo (
  Username varchar(20) PRIMARY KEY NOT NULL,
  Password varchar(50) NOT NULL
);
CREATE TABLE Files (
	Filepath varchar(50) PRIMARY KEY NOT NULL,
    Owner varchar(50) NOT NULL,
    FOREIGN KEY fk1(Owner) REFERENCES UserInfo(Username)
);
CREATE TABLE FileShare (
	Filepath varchar(50) NOT NULL,
	FOREIGN KEY fk1(FilePath) REFERENCES Files(Filepath),
    Shareduser varchar(50) NOT NULL,
    CONSTRAINT pk_FileShareID PRIMARY KEY (Filepath,Shareduser)
);