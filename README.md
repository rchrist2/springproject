# springproject
Uses JDK 13, SQL Server, flyway, see pom.xml

After cloning, import the maven changes and go to exec:java and make a new run configuration using 
"compile exec:java -f pom.xml" in the Command Line box (this probably isn't needed, the program should also run using Main)

## Updated SQL Scripts 2/29/2020 (edit file to see with correct formatting):

create table tblclock(
	clock_id int NOT NULL identity(1,1) PRIMARY KEY,
    punch_in time NOT NULL,
	punch_out time NOT NULL,
	date_created datetime NOT NULL,
	schedule_id int not null FOREIGN KEY REFERENCES tblschedule(schedule_id)
);

--same
create table tblroles(
    role_id int NOT NULL identity(1,1) PRIMARY KEY,
    role_desc varchar(36) NOT NULL
);

create table tblusers(
    user_id int NOT NULL identity(1,1) PRIMARY KEY,
    username varchar(128) NOT NULL UNIQUE,
    password varchar(128) NOT NULL,
    employee_id int FOREIGN KEY REFERENCES tblemployee(id)
);

--same
create table tblday(
    day_id int NOT NULL identity(1,1) PRIMARY KEY,
    day_desc varchar(25)
);

create table tblschedule(
	schedule_id int not null identity(1,1) primary key,
    schedule_time_begin time NOT NULL,
    schedule_time_end time NOT NULL,
    schedule_date datetime NOT NULL,
	employee_id int not null FOREIGN KEY REFERENCES tblemployee(id),
    day_id int not null FOREIGN KEY REFERENCES tblday(day_id)
);


CREATE TABLE tblemployee (
	id int not null identity(1,1) primary key,
	name varchar(128) not null,
	email varchar(128) not null,
	address varchar(128) not null,
	phone varchar(128) not null,
	roles_id int not null FOREIGN KEY REFERENCES tblroles(role_id)
);

CREATE TABLE tbltimeoff(
	time_off_id int not null identity(1,1) primary key,
	begin_time_off_date time not null,
	end_time_off_date time not null,
	approved bit NOT NULL,
	reason_desc varchar(128) not null,
	schedule_id int not null FOREIGN KEY REFERENCES tblschedule(schedule_id)
);
