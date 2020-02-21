use [4375db]

create table tblroles
(
    Role_ID   int IDENTITY (1, 1) NOT NULL PRIMARY KEY,
    Role_Desc varchar(36)         NOT NULL
);

create table tblday
(
    Day_id   int IDENTITY (1, 1) NOT NULL PRIMARY KEY,
    Day_Desc varchar(25)
);

CREATE TABLE tblemployee
(
    id       int          not null identity (1,1) primary key,
    name     varchar(128) not null,
    email    varchar(128) not null,
    address  varchar(128) not null,
    phone    varchar(128) not null,
    roles_id int          not null FOREIGN KEY REFERENCES tblroles (role_id)
);

create table tblschedule
(
    schedule_id         int      not null identity (1,1) primary key,
    schedule_time_begin time     NOT NULL,
    schedule_time_end   time     NOT NULL,
    schedule_date       datetime NOT NULL,
    employee_id         int      not null FOREIGN KEY REFERENCES tblemployee (id),
    day_id              int      not null FOREIGN KEY REFERENCES tblday (day_id)
);

create table tblclock
(
    clock_id    int  NOT NULL identity (1,1) PRIMARY KEY,
    punch_in    time NOT NULL,
    punch_out   time NOT NULL,
    schedule_id int  not null FOREIGN KEY REFERENCES tblschedule (schedule_id)
);

create table tblusers
(
    user_id     int          NOT NULL identity (1,1) PRIMARY KEY,
    username    varchar(128) NOT NULL UNIQUE,
    password    varchar(128) NOT NULL,
    employee_id int FOREIGN KEY REFERENCES tblemployee (id)
);

CREATE TABLE tbltimeoff
(
    time_off_id         int          not null identity (1,1) primary key,
    begin_time_off_date datetime     not null,
    end_time_off_date   datetime     not null,
    approved            bit          NOT NULL,
    reason_desc         varchar(128) not null,
    schedule_id         int          not null FOREIGN KEY REFERENCES tblschedule (schedule_id)
);


INSERT INTO tblroles
VALUES ('Manager');
INSERT INTO tblroles
VALUES ('Employee');

INSERT INTO tblemployee
VALUES ('Kevin', 'Kevin@gmail.com', '1111 First St.', '281-487-3829', 1);
INSERT INTO tblemployee
VALUES ('Rachel', 'Rachael@gmail.com', '2222 Second Dr.', '832-543-3331', 1);
INSERT INTO tblemployee
VALUES ('Hue', 'Hue@gmail.com', '3333 Third Ln.', '832-836-7854', 2);
INSERT INTO tblemployee
VALUES ('Sim', 'Sim@gmail.com', '4444 Fourth Blvd.', '713-221-0092', 2);

INSERT INTO tblusers
VALUES ('kevin', 'kevin', 1);
INSERT INTO tblusers
VALUES ('rachel', 'rachel', 2);
INSERT INTO tblusers
VALUES ('hue', 'hue', 3);
INSERT INTO tblusers
VALUES ('sim', 'sim', 4);

INSERT INTO tblday
VALUES ('Sunday');
INSERT INTO tblday
VALUES ('Monday');
INSERT INTO tblday
VALUES ('Tuesday');
INSERT INTO tblday
VALUES ('Wednesday');
INSERT INTO tblday
VALUES ('Thursday');
INSERT INTO tblday
VALUES ('Friday');
INSERT INTO tblday
VALUES ('Saturday');
