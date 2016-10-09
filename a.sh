#!/bin/bash

mvn clean package -Dmaven.test.skip=true
scp target/*.jar root@123.207.11.104:/data/workspace/cmb-pay-for-spring-boot/