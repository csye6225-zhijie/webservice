#!/bin/bash

systemctl status application.service

sudo chmod +x /home/ec2-user/csye/webservice-0.0.1-SNAPSHOT.jar

# cleanup log files

# sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
#     -a fetch-config \
#     -m ec2 \
#     -c file:/opt/cloudwatch-config.json \
#     -s
