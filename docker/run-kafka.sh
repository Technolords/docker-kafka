#!/bin/bash
echo "About to run a Kafka server..."
# Do so magic with environment props -> sed here?
# "$KAFKA_HOME/bin/kafka-server-start.sh" "$KAFKA_HOME/config/server.properties"
while :
do
	echo "Press [CTRL+C] to stop.."
	sleep 100
done
echo "Done using Kafka."
