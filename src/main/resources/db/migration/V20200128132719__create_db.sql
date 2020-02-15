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
    availability_id int identity(1, 1) NOT NULL,
    time_begin time NOT NULL,
    time_end time NOT NULL,
    assigned bit NOT NULL,
    User_ID int FOREIGN KEY REFERENCES tblusers(User_ID),
    Day_ID int FOREIGN KEY REFERENCES tblday(Day_ID)
);

create table tblemployee(
    id int IDENTITY(1, 1) NOT NULL PRIMARY KEY,
    name varchar(128) NOT NULL,
    email varchar(128) NOT NULL,
    address varchar(128) NOT NULL,
    phone   varchar(128) NOT NULL
);

CREATE TABLE tbltimeoff(
    time_off_id int not null identity(1,1) primary key,
    time_off_date date not null,
    approved bit NOT NULL,
    User_ID int not null FOREIGN KEY REFERENCES tblUsers(User_ID)
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

INSERT INTO tblday VALUES ('Sunday');
INSERT INTO tblday VALUES ('Monday');
INSERT INTO tblday VALUES ('Tuesday');
INSERT INTO tblday VALUES ('Wednesday');
INSERT INTO tblday VALUES ('Thursday');
INSERT INTO tblday VALUES ('Friday');
INSERT INTO tblday VALUES ('Saturday');
