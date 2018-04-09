#!/bin/bash
echo "About to sync properties..."
java -jar docker-run.jar
echo "About to run Kafka..."
./bin/kafka-server-start.sh /etc/kafka/install/config/server.properties
