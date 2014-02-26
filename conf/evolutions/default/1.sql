# --- First database schema

# --- !Ups
create table user (
  user_id                   bigint not null primary key AUTO_INCREMENT,
  email                     varchar(255) not null unique,
  password                  varchar(255) not null
);

# --- !Downs
drop table if exists user;
