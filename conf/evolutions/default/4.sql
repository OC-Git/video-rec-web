# --- !Ups

alter table Client add column user varchar(255);
alter table Client add column pwd varchar(255);
alter table Client add column yt_token varchar(255);
update Client set user=ytUser, pwd=ytPwd;
alter table Video alter publishedId varchar(255) null;	

# --- !Downs

alter table Client drop column user;
alter table Client drop column pwd;
alter table Client drop column yt_token;
