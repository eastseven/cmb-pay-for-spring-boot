#!/bin/bash

mvn clean package -Dmaven.test.skip=true
scp target/*.jar ec2-user@ec2-54-169-105-196.ap-southeast-1.compute.amazonaws.com:/home/ec2-user/