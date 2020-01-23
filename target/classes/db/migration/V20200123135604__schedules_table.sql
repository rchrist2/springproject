create table schedule
(
  id int not null
    constraint product_components_pk
      primary key,
  availability varchar(50),
  employee_id integer
    constraint employee_id_fk
      references EMPLOYEE(id)
);