
locals { timestamp = regex_replace(timestamp(), "[- TZ:]", "") }

source "amazon-ebs" "autogenerated_1" {
    access_key      = "${var.aws_access_key}"
    ami_description = "Amazon Linux AMI Kernel 5.10"
    ami_name        = "csye6225_spring2022_${local.timestamp}"
    instance_type   = "t2.micro"
    ami_users       = "${var.ami_share_account}"
    launch_block_dev3899io3ice_mappings {
    delete_on_termination = true
    device_name           = "/dev/xvda"
    volume_size           = 20
    volume_type           = "gp2"
    }
    region       = "${var.aws_region}"
    secret_key   = "${var.aws_secret_key}"
    source_ami   = "${var.source_ami}"
    ssh_username = "${var.ssh_username}"
    subnet_id    = "${var.subnet_id}"
}

build {
    sources = ["source.amazon-ebs.autogenerated_1"]

    provisioner "shell" {
        inline = ["sudo apt-get update", 
            "sudo apt-get install nginx -y",
            "sudo apt install openjdk-8-jdk -y",

            "mysql "



        ]
    }

    provisioner "file" {
        source = "./target/webservice-0.0.1-SNAPSHOT.jar"  
        destination = "/usr/webservice-0.0.1-SNAPSHOT.jar"
    }

}

            // "sudo mkdir -p /usr/webservice",
            // "sudo chmod 777 /usr/webservice"