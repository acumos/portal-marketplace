

CREATE DATABASE IF NOT EXISTS acumos_comment;

create user 'CCDS_USER'@'localhost' identified by 'CCDS_PASS';
grant all on acumos_comment.* to 'CCDS_USER'@'localhost';
create user 'CCDS_USER'@'%' identified by 'CCDS_PASS';
grant all on acumos_comment.* to 'CCDS_USER'@'%';

use acumos_comment;

-- tables

CREATE TABLE thread (
  id    BIGINT PRIMARY KEY AUTO_INCREMENT,
  url   VARCHAR(256) NOT NULL,
  title VARCHAR(512) NULL
);

CREATE TABLE comment (
  id                     BIGINT PRIMARY KEY AUTO_INCREMENT,
  thread_id              BIGINT       NOT NULL,
  parent_id              BIGINT       NULL,
  creation_date          TIMESTAMP    NOT NULL,
  last_modification_date TIMESTAMP    NOT NULL,
  status                 VARCHAR(32)  NOT NULL,
  text                   TEXT         NOT NULL,
  author                 VARCHAR(128) NULL,
  name                   VARCHAR(128) NULL,
  email_hash             CHAR(32)     NULL,
  url                    VARCHAR(256) NULL,
  FOREIGN KEY (thread_id) REFERENCES thread (id),
  FOREIGN KEY (parent_id) REFERENCES comment (id)
);

CREATE TABLE property (
  _key   VARCHAR(32) PRIMARY KEY,
  value VARCHAR(256) NOT NULL
);

-- indexes
CREATE INDEX thread_id_idx
  ON thread (id);
CREATE INDEX thread_url_idx
  ON thread (url);

CREATE INDEX comment_id_idx
  ON comment (id);
CREATE INDEX comment_thread_id_idx
  ON comment (thread_id);
CREATE INDEX comment_creation_date_idx
  ON comment (creation_date);
CREATE INDEX comment_status_idx
  ON comment (status);