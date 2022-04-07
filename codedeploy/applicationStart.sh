#!/bin/bash

sudo systemctl start application.service
systemctl status application.service

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -m ec2 -a status