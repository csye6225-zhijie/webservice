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
  default = "./target/webservice-0.0.1-SNAPSHOT.jar"
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

    provisioner "file" { 
        source = "./application.service"
        destination = "/tmp/"
}

    provisioner "file" { 
        source = "./src/main/resources/application.properties"
        destination = "/tmp/"
}

    provisioner "shell" {
        inline = [
            "sudo yum update -y", 
            "sudo yum -y install java-1.8.0-openjdk-devel.x86_64",
            "sleep 30",

            "export JAVA_HOME=/usr/lib/jvm/jre-1.8.0-openjdk",
            "export PATH=$JAVA_HOME/bin:$PATH",
            "export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar",
            
            "sudo yum install mariadb -y",
           
            "sudo su",
            "mkdir csye/",
            "cd csye/",
            "mv /tmp/webservice-0.0.1-SNAPSHOT.jar .",
            "sudo chmod +x webservice-0.0.1-SNAPSHOT.jar",
            "sudo mv /tmp/application.service /etc/systemd/system",

            "touch application.properties",
            "cat >application.properties <<EOF",
            "cloud.aws.region.static=${var.aws_region}",
            "cloud.aws.region.auto=false",
            "cloud.aws.credentials.access-key=${var.aws_access_key}",
            "cloud.aws.credentials.secret-key=${var.aws_secret_key}",

            "spring.jpa.hibernate.ddl-auto=update",
            "spring.jpa.show-sql=true",
            "EOF"
        ]
    }

}