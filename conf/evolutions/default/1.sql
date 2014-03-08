# --- First database schema

# --- !Ups
create table user (
  user_id                   bigint not null primary key AUTO_INCREMENT,
  email                     varchar(255) not null unique,
  password                  varchar(255) not null
);


create table challenge (
  challenge_id              bigint not null primary key AUTO_INCREMENT,
  challenge                 varchar(256) not null unique,
  motivation                varchar(1024) not null,
  added_by_user_id          bigint not null 
);

# --- !Downs
drop table if exists user;
drop table if exists challenge;
