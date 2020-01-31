create table tblRoles(
    Role_ID int IDENTITY(1, 1) NOT NULL PRIMARY KEY,
    Role_Desc varchar(36) NOT NULL
);

--The word "password" may not work, as it is utilized keyword according to Visual Studio Code SQL Extension.
create table tblUsers(
    User_ID int IDENTITY(1, 1) NOT NULL PRIMARY KEY,
    Username varchar(128) NOT NULL UNIQUE,
    Password varchar(128) NOT NULL,
    Role_id int FOREIGN KEY REFERENCES tblRoles(Role_ID)
);

create table tblDay(
    Day_id int IDENTITY(1, 1) NOT NULL PRIMARY KEY,
    Day_Desc varchar(25)
);

--The word "Availability" may not work, as it is a utilized keyword according to Visual Studio Code SQL Extension.
--The column "assigned" is using the datatype "bit" due to it's nature of only storing the values 1, 0, or  null.
create table tblAvailability(
    time_begin time NOT NULL,
    time_end time NOT NULL,
    assigned bit NOT NULL,
    User_ID int FOREIGN KEY REFERENCES tblUsers(User_ID),
    Day_ID int FOREIGN KEY REFERENCES tblDay(Day_ID)
);