CREATE TABLE mail (
  id_mail int(11) NOT NULL AUTO_INCREMENT,
  sender varchar(256) NOT NULL,
--  recipient varchar(256) NOT NULL,
--  creation_date timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    mail_date timestamp NOT NULL,
    subject varchar(1024),
--  pnr varchar(6),
--  body TEXT,
  PRIMARY KEY (id_mail)
) ;

CREATE TABLE OTHER (
  id varchar(256) NOT NULL,
  PRIMARY KEY (id)
) ;
