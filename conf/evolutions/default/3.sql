# --- !Ups

alter table Video add column filename varchar(255);
update Video set filename='flv';

# --- !Downs

alter table Video drop column filename;
