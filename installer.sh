#!/bin/sh

# install mariadb-5.5
sudo yum install mariadb-server

# start mariadb server
sudo systemctl start mariadb
sudo systemctl enable mariadb

#initial mysql and reset the password
mysql_secure_installation -y 

create schema csye6225 authorization root;

SHOW CREATE DATABASE csye6225;
