#!/bin/bash

git fetch -p
git checkout main
git merge origin/main

mvn clean package -DskipTests
mv target/aliyun-ddns-client-1.0.jar  ~/workspace/ddns/
