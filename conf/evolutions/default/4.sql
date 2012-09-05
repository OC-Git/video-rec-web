# --- !Ups


alter table Client add column usr varchar(255);
alter table Client add column pwd varchar(255);
alter table Client add column yt_token varchar(255);
update Client set usr=ytUser, pwd=ytPwd;
alter table Video alter column publishedId drop not null;

# --- !Downs

alter table Client drop column usr;
alter table Client drop column pwd;
alter table Client drop column yt_token;
