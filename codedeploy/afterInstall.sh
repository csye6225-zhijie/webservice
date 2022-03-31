#!/bin/bash

sudo systemctl stop application.service
sudo rm -rf csye/webservice-0.0.1-SNAPSHOT.jar

sudo chmod +x csye/webservice-0.0.1-SNAPSHOT.jar

# cleanup log files
