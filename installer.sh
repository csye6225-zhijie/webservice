#!/bin/sh

# install mariadb-5.5
sudo yum install mariadb-server

# start mariadb server
sudo systemctl start mariadb
sudo systemctl enable mariadb

#initial mysql and reset the password
mysql_secure_installation -y 


# //  "wget https://dlcdn.apache.org/maven/maven-3/3.8.4/binaries/apache-maven-3.8.4-bin.tar.gz",
# //             "tar xvf apache-maven-3.8.4-bin.tar.gz",
# //             "sudo mv apache-maven-3.8.4  /usr/local/apache-maven",
# //             "rm apache-maven-3.8.4-bin.tar.gz",
# //             "export M2_HOME=/usr/local/apache-maven && export M2=$M2_HOME/bin && export PATH=$M2:$PATH",

# //             "sudo wget https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm",
# //             "sudo rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2022",
# //             "sudo rpm -Uvh mysql80-community-release-el7-3.noarch.rpm",
# //             "sudo yum install mysql-server -y",
# //             "sudo systemctl stop mysqld",
# //             "sudo systemctl set-environment MYSQLD_OPTS='--skip-grant-tables'",
# //             "sudo systemctl start mysqld",
# //             "mysql -u root -Bse \"FLUSH PRIVILEGES;ALTER USER 'root'@'localhost' IDENTIFIED by 'Qa5271335!';CREATE DATABASE csye6225;\"",
# //             "sudo systemctl stop mysqld",
# //             "sudo systemctl unset-environment MYSQLD_OPTS",
# //             "sudo systemctl start mysqld",

# // "mv /tmp/application.properties .",
# // "sudo chmod 770 application.properties",
# //    "mvn clean install -DskipTests",
# //             "sleep 10",
   
# //    "sudo mv application.service /etc/systemd/system",
# //             "sudo systemctl enable application.service",
# //             "sudo systemctl start application.service"
