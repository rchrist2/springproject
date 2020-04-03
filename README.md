# springproject
Uses JDK 13, SQL Server, flyway, see pom.xml

After cloning, import the maven changes and go to exec:java and make a new run configuration using 
"compile exec:java -f pom.xml" in the Command Line box (this probably isn't needed, the program should also run using Main)

## Updated SQL Scripts 4/02/2020 (edit file to see with correct formatting):

create table tblroles(
    role_id int NOT NULL identity(1,1) PRIMARY KEY,
    role_name varchar(100) NOT NULL,
    role_desc varchar(200) NOT NULL
);

CREATE TABLE tblemployee (
	id int not null identity(1,1) primary key,
	name varchar(128) not null,
	email varchar(128) not null,
	address varchar(128) not null,
	phone varchar(128) not null,
	roles_id int not null FOREIGN KEY REFERENCES tblroles(role_id) ON DELETE CASCADE
);

create table tblusers(
    user_id int NOT NULL identity(1,1) PRIMARY KEY,
    username varchar(128) NOT NULL UNIQUE,
	salt_password varbinary(16) NULL,
	hashed_password varchar(255) NULL,
    employee_id int UNIQUE FOREIGN KEY REFERENCES tblemployee(id) ON DELETE CASCADE
);

create table tblday(
    day_id int NOT NULL identity(1,1) PRIMARY KEY,
    day_desc varchar(25)
);

create table tblschedule(
	schedule_id int not null identity(1,1) primary key,
    schedule_time_begin time NOT NULL,
    schedule_time_end time NOT NULL,
    schedule_date datetime NOT NULL,
	employee_id int not null FOREIGN KEY REFERENCES tblemployee(id) ON DELETE CASCADE,
    day_id int not null FOREIGN KEY REFERENCES tblday(day_id)
);

create table tblclock(
  	clock_id int NOT NULL identity(1,1) PRIMARY KEY,
    punch_in time NOT NULL,
  	punch_out time NOT NULL,
  	date_created datetime NOT NULL,
  	day_id int null FOREIGN KEY REFERENCES tblday(day_id),
  	schedule_id int null FOREIGN KEY REFERENCES tblschedule(schedule_id) ON DELETE CASCADE
  );

CREATE TABLE tbltimeoff(
	time_off_id int not null identity(1,1) primary key,
	begin_time_off_date datetime not null,
	end_time_off_date datetime not null,
	approved bit NOT NULL,
	reason_desc varchar(128) not null,
	day_id int null FOREIGN KEY REFERENCES tblday(day_id),
	schedule_id int null FOREIGN KEY REFERENCES tblschedule(schedule_id),
	employee_id int not null FOREIGN KEY REFERENCES tblemployee(id) ON DELETE CASCADE
);

INSERT INTO tblroles VALUES('Owner','Is the owner');
INSERT INTO tblroles VALUES('Manager','Is a manager');
INSERT INTO tblroles VALUES('Employee','Is an employee');

INSERT INTO tblday VALUES('Sunday');
INSERT INTO tblday VALUES('Monday');
INSERT INTO tblday VALUES('Tuesday');
INSERT INTO tblday VALUES('Wednesday');
INSERT INTO tblday VALUES('Thursday');
INSERT INTO tblday VALUES('Friday');
INSERT INTO tblday VALUES('Saturday');

## Check/Other Constraints as of 3/18/2020
alter table tblusers add constraint username_is_email check (username like '%_@__%.__%');
CREATE UNIQUE NONCLUSTERED INDEX idx_time_off_schedule_id_notnull
ON tbltimeoff(schedule_id)
WHERE schedule_id IS NOT NULL;

## References
https://stackoverflow.com/questions/50569330/how-to-reset-combobox-and-display-prompttext
https://stackoverflow.com/questions/42909287/how-do-i-use-radio-buttons-groups-in-javafx
https://stackoverflow.com/questions/275711/add-leading-zeroes-to-number-in-java
https://stackoverflow.com/questions/33027549/sql-max-only-returns-1-row-if-column-has-several-max-values
https://stackoverflow.com/questions/24578517/javafx-multithreading-joining-threads-wont-update-the-ui
https://www.tutorialspoint.com/javaexamples/thread_check.htm
https://stackoverflow.com/questions/46197243/how-to-retrieve-data-for-a-javafx-table-from-a-jpa-entity-in-relation-with-anoth
https://stackoverflow.com/questions/32435096/mysql-date-format-in-javafx-tableview
https://www.geeksforgeeks.org/generate-random-string-of-given-size-in-java/
https://stackoverflow.com/questions/47771296/how-to-reset-a-counter-column-to-zero-in-a-table-every-month
https://stackoverflow.com/questions/538739/best-way-to-store-time-hhmm-in-a-database
https://www.baeldung.com/spring-email
https://loading.io/license/
https://stackoverflow.com/questions/45778386/in-javafx-how-to-remove-a-specific-node-from-a-gridpane-with-the-coordinate-of
https://www.javaguides.net/2020/02/java-sha-512-hash-with-salt-example.html
https://stackoverflow.com/questions/9375938/javafx-filechooser
https://stackoverflow.com/questions/46017483/javafx-export-tableview-to-excel-with-name-of-columns
https://stackoverflow.com/questions/5175728/how-to-get-the-current-date-time-in-java
https://stackoverflow.com/questions/10828492/convert-time-datatype-into-am-pm-format
