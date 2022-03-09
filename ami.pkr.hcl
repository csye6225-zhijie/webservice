variable "aws_access_key" {
  type    = string
}

variable "aws_secret_key" {
  type    = string
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}  

variable "source_ami" {
  type    = string
  default = "ami-033b95fb8079dc481"
}

variable "ssh_username" {
  type    = string
  default = "ec2-user"
}

variable "subnet_id" {
  type    = string
  default = "subnet-0501cbf5622302025"
}

variable "ami_share_account" {
    type  = string
    default = "508720203319"
}

variable "webservice" {
  type = string
  default = "../webservice"
}

locals { timestamp = regex_replace(timestamp(), "[- TZ:]", "") }

source "amazon-ebs" "autogenerated_1" {
    access_key      = "${var.aws_access_key}"
    ami_description = "Amazon Linux AMI Kernel 5.10"
    ami_name        = "csye6225_spring2022_${local.timestamp}"
    instance_type   = "t2.micro"
    ami_users       = ["${var.ami_share_account}"]
    region       = "${var.aws_region}"
    secret_key   = "${var.aws_secret_key}"
    source_ami   = "${var.source_ami}"
    ssh_username = "${var.ssh_username}"
    subnet_id    = "${var.subnet_id}"
}

build {
    sources = ["source.amazon-ebs.autogenerated_1"]


    provisioner "file" { 
        source = "${var.webservice}"
        destination = "/tmp/"
}

    provisioner "shell" {
        inline = [
              
            "sudo yum update -y", 
            "sleep 60",
            "sudo yum -y install java-1.8.0-openjdk-devel.x86_64",

            "export JAVA_HOME=/usr/lib/jvm/jre-1.8.0-openjdk",
            "export PATH=$JAVA_HOME/bin:$PATH",
            "export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar",
            
           
            "sudo su",
            "mkdir csye/",
            "cd csye/",
            "mv /tmp/webservice .",
            "sudo chmod 777 webservice/",

            "cd webservice/",
            "sudo chmod +x target/webservice-0.0.1-SNAPSHOT.jar",
            "sudo mv application.service /etc/systemd/system",
            "sudo systemctl enable application.service",
            "sudo systemctl start application.service"

        ]
    }

}

//  "wget https://dlcdn.apache.org/maven/maven-3/3.8.4/binaries/apache-maven-3.8.4-bin.tar.gz",
//             "tar xvf apache-maven-3.8.4-bin.tar.gz",
//             "sudo mv apache-maven-3.8.4  /usr/local/apache-maven",
//             "rm apache-maven-3.8.4-bin.tar.gz",
//             "export M2_HOME=/usr/local/apache-maven && export M2=$M2_HOME/bin && export PATH=$M2:$PATH",

//             "sudo wget https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm",
//             "sudo rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2022",
//             "sudo rpm -Uvh mysql80-community-release-el7-3.noarch.rpm",
//             "sudo yum install mysql-server -y",
//             "sudo systemctl stop mysqld",
//             "sudo systemctl set-environment MYSQLD_OPTS='--skip-grant-tables'",
//             "sudo systemctl start mysqld",
//             "mysql -u root -Bse \"FLUSH PRIVILEGES;ALTER USER 'root'@'localhost' IDENTIFIED by 'Qa5271335!';CREATE DATABASE csye6225;\"",
//             "sudo systemctl stop mysqld",
//             "sudo systemctl unset-environment MYSQLD_OPTS",
//             "sudo systemctl start mysqld",

//    "mvn clean install -DskipTests",
//             "sleep 10",
   