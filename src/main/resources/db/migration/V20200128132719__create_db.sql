create table tblroles(
    Role_ID int IDENTITY(1, 1) NOT NULL PRIMARY KEY,
    Role_Desc varchar(36) NOT NULL
);

--The word "password" may not work, as it is utilized keyword according to Visual Studio Code SQL Extension.
create table tblusers(
    User_ID int IDENTITY(1, 1) NOT NULL PRIMARY KEY,
    Username varchar(128) NOT NULL UNIQUE,
    Password varchar(128) NOT NULL,
    Role_id int FOREIGN KEY REFERENCES tblroles(Role_ID)
);

create table tblday(
    Day_id int IDENTITY(1, 1) NOT NULL PRIMARY KEY,
    Day_Desc varchar(25)
);

--The word "Availability" may not work, as it is a utilized keyword according to Visual Studio Code SQL Extension.
--The column "assigned" is using the datatype "bit" due to it's nature of only storing the values 1, 0, or  null.
create table tblavailability(
    time_begin time NOT NULL,
    time_end time NOT NULL,
    assigned bit NOT NULL,
    availability_id int NOT NULL,
    User_ID int FOREIGN KEY REFERENCES tblusers(User_ID),
    Day_ID int FOREIGN KEY REFERENCES tblday(Day_ID)
);

create table tblemployee(
    id int IDENTITY(1, 1) NOT NULL PRIMARY KEY,
    name varchar(50) NOT NULL,
    email varchar(50) NOT NULL,
    address varchar(50) NOT NULL,
    phone   varchar(50) NOT NULL
);

INSERT INTO tblroles VALUES ('Manager');
INSERT INTO tblroles VALUES ('Employee');

INSERT INTO tblusers VALUES ('kevin', 'kevin', 1);
INSERT INTO tblusers VALUES ('rachel', 'rachel', 1);
INSERT INTO tblusers VALUES ('sim', 'sim', 1);
INSERT INTO tblusers VALUES ('shail', 'shail', 1);
INSERT INTO tblusers VALUES ('hue', 'hue', 2);
INSERT INTO tblusers VALUES ('ivan', 'ivan', 2);
INSERT INTO tblusers VALUES ('thien', 'thien', 2);
INSERT INTO tblusers VALUES ('sy', 'sy', 2);
INSERT INTO tblusers VALUES ('marvin', 'marvin', 2);