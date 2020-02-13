# springproject
Uses JDK 13, SQL Server, flyway, see pom.xml

After cloning, import the maven changes and go to exec:java and make a new run configuration using 
"compile exec:java -f pom.xml" in the Command Line box (this probably isn't needed, the program should also run using Main)

## Updated SQL Scripts 2/13/2020 (edit file to see with correct formatting):

create table tblRoles(
    Role_ID int NOT NULL identity(1,1) PRIMARY KEY,
    Role_Desc varchar(36) NOT NULL
);

create table tblUsers(
    User_ID int NOT NULL identity(1,1) PRIMARY KEY,
    Username varchar(128) NOT NULL UNIQUE,
    Password varchar(128) NOT NULL,
    Role_id int FOREIGN KEY REFERENCES tblRoles(Role_ID)
);

create table tblDay(
    Day_id int NOT NULL identity(1,1) PRIMARY KEY,
    Day_Desc varchar(25)
);

create table tblAvailability(
	availability_id int not null identity(1,1) primary key,
    time_begin time NOT NULL,
    time_end time NOT NULL,
    assigned bit NOT NULL,
    User_ID int not null FOREIGN KEY REFERENCES  tblUsers(User_ID),
    Day_ID int not null FOREIGN KEY REFERENCES tblDay(Day_ID)
);

CREATE TABLE tblEmployee (
id int not null identity(1,1) primary key,
name varchar(128) not null,
email varchar(128) not null,
address varchar(128) not null,
phone varchar(128) not null

);

CREATE TABLE tblTimeOff(
	time_off_id int not null identity(1,1) primary key,
	time_off_date date not null,
	approved bit NOT NULL,
	User_ID int not null FOREIGN KEY REFERENCES tblUsers(User_ID)
);
