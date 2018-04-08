package net.technolords.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AugmentProperties {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String SERVER_PROPERTIES = "server.properties";
    private static final String KEY_BROKER_ID = "broker.id";
    private static final String KEY_LOG_DIRS = "log.dirs";
    private static final String KEY_ZOOKEEPER_CONNECT = "zookeeper.connect";
    private static final String DEFAULT_BROKER_ID = "0";
    private static final String DEFAULT_LOG_DIRS = "/etc/kafka/install/data";
    private static final String DEFAULT_ZOOKEEPER_CONNECT = "localhost:2181";
    private static final String ENV_PROP_PREFIX = "kafka.";

    /*
        2018-04-08 22:15:16,314 [INFO] [main] [net.technolords.util.CopyKafkaProperties] About to copy property files from config-ref to config
        2018-04-08 22:15:16,315 [INFO] [main] [net.technolords.util.CopyKafkaProperties] Source folder exist: true
        2018-04-08 22:15:16,316 [INFO] [main] [net.technolords.util.CopyKafkaProperties] Target folder exist: false
        2018-04-08 22:15:16,316 [INFO] [main] [net.technolords.util.CopyKafkaProperties] Creating target folder...
        2018-04-08 22:15:16,356 [INFO] [main] [net.technolords.util.CopyKafkaProperties] About to copy: config-ref/connect-console-sink.properties
        2018-04-08 22:15:16,358 [INFO] [main] [net.technolords.util.CopyKafkaProperties] About to copy: config-ref/connect-console-source.properties
        2018-04-08 22:15:16,358 [INFO] [main] [net.technolords.util.CopyKafkaProperties] About to copy: config-ref/connect-distributed.properties
        2018-04-08 22:15:16,360 [INFO] [main] [net.technolords.util.CopyKafkaProperties] About to copy: config-ref/connect-file-sink.properties
        2018-04-08 22:15:16,360 [INFO] [main] [net.technolords.util.CopyKafkaProperties] About to copy: config-ref/connect-file-source.properties
        2018-04-08 22:15:16,360 [INFO] [main] [net.technolords.util.CopyKafkaProperties] About to copy: config-ref/connect-log4j.properties
        2018-04-08 22:15:16,361 [INFO] [main] [net.technolords.util.CopyKafkaProperties] About to copy: config-ref/connect-standalone.properties
        2018-04-08 22:15:16,361 [INFO] [main] [net.technolords.util.CopyKafkaProperties] About to copy: config-ref/consumer.properties
        2018-04-08 22:15:16,361 [INFO] [main] [net.technolords.util.CopyKafkaProperties] About to copy: config-ref/log4j.properties
        2018-04-08 22:15:16,362 [INFO] [main] [net.technolords.util.CopyKafkaProperties] About to copy: config-ref/producer.properties
        2018-04-08 22:15:16,362 [INFO] [main] [net.technolords.util.CopyKafkaProperties] About to copy: config-ref/server.properties
        2018-04-08 22:15:16,362 [INFO] [main] [net.technolords.util.CopyKafkaProperties] About to copy: config-ref/tools-log4j.properties
        2018-04-08 22:15:16,362 [INFO] [main] [net.technolords.util.CopyKafkaProperties] About to copy: config-ref/zookeeper.properties
        2018-04-08 22:15:16,363 [INFO] [main] [net.technolords.util.AugmentProperties] File exists: true
        2018-04-08 22:15:16,364 [INFO] [main] [net.technolords.util.AugmentProperties] Total properties found: 18
        2018-04-08 22:15:16,365 [INFO] [main] [net.technolords.util.AugmentProperties] Validating default properties...
        2018-04-08 22:15:16,365 [INFO] [main] [net.technolords.util.AugmentProperties] Updated log.dirs -> /etc/kafka/install/data
        2018-04-08 22:15:16,365 [INFO] [main] [net.technolords.util.AugmentProperties] Validating environment properties
        2018-04-08 22:15:16,365 [INFO] [main] [net.technolords.util.AugmentProperties] Found environment key: KAFKA.BROKER.ID -> real key: broker.id
        2018-04-08 22:15:16,366 [INFO] [main] [net.technolords.util.AugmentProperties] Updated broker.id -> 5

        vs

        2018-04-08 22:17:32,410 [INFO] [main] [net.technolords.util.CopyKafkaProperties] Config folder present, assuming property files are there also (skipping initialization)
        2018-04-08 22:17:32,412 [INFO] [main] [net.technolords.util.AugmentProperties] File exists: true
        2018-04-08 22:17:32,413 [INFO] [main] [net.technolords.util.AugmentProperties] Total properties found: 18
        2018-04-08 22:17:32,414 [INFO] [main] [net.technolords.util.AugmentProperties] Validating default properties...
        2018-04-08 22:17:32,414 [INFO] [main] [net.technolords.util.AugmentProperties] Validating environment properties
        2018-04-08 22:17:32,414 [INFO] [main] [net.technolords.util.AugmentProperties] Found environment key: KAFKA.BROKER.ID -> real key: broker.id
     */

    public void augmentServerProperties(Path propertiesFolder) throws IOException {
        // Fetch server.properties
        Path pathToServerProperties = Paths.get(propertiesFolder.toAbsolutePath().toString(), SERVER_PROPERTIES);
        Properties serverProperties = this.readPropertiesFromPath(pathToServerProperties);
        LOGGER.info("Total properties found: {}", serverProperties.size());
        // Assert correct defaults
        boolean changed = this.checkDefaults(serverProperties);
        // Assert environment variables
        changed |= this.parseEnvironmentVariables(serverProperties);
        if (changed) {
            this.storePropertiesAsFile(serverProperties, pathToServerProperties);
        }
    }

    public Properties readPropertiesFromPath(Path pathToData) throws IOException {
        LOGGER.info("File exists: {}", Files.exists(pathToData));
        Properties properties = new Properties();
        properties.load(Files.newInputStream(pathToData, StandardOpenOption.READ));
        return properties;
    }

    /**
     * Check for correct default values and change these where needed.
     *
     * @param properties
     *  The current loaded properties
     * @return
     *  Whether one or more properties where changed
     */
    protected boolean checkDefaults(Properties properties) {
        LOGGER.info("Validating default properties...");
        boolean changed = false;
        String value;

        // broker.id
        value = properties.getProperty(KEY_BROKER_ID);
        if (value == null || value.isEmpty()) {
            properties.put(KEY_BROKER_ID, DEFAULT_BROKER_ID);
            LOGGER.info("Updated {} -> {}", KEY_BROKER_ID, DEFAULT_BROKER_ID);
            changed = true;
        }

        // log.dirs
        value = properties.getProperty(KEY_LOG_DIRS);
        if (!DEFAULT_LOG_DIRS.equals(value)) {
            properties.put(KEY_LOG_DIRS, DEFAULT_LOG_DIRS);
            LOGGER.info("Updated {} -> {}", KEY_LOG_DIRS, DEFAULT_LOG_DIRS);
            changed = true;
        }
        // zookeeper.connect
        value = properties.getProperty(KEY_ZOOKEEPER_CONNECT);
        if (value == null || value.isEmpty()) {
            properties.put(KEY_ZOOKEEPER_CONNECT, DEFAULT_ZOOKEEPER_CONNECT);
            LOGGER.info("Updated {} -> {}", KEY_ZOOKEEPER_CONNECT, DEFAULT_ZOOKEEPER_CONNECT);
            changed = true;
        }

        return changed;
    }

    /**
     * Auxiliary method to check for (relevant) environment variables, and when present
     * @param properties
     * @return
     */
    protected boolean parseEnvironmentVariables(Properties properties) {
        LOGGER.info("Validating environment properties");
        String current, desired;
        boolean changed = false;
        Map<String, String> environmentMap = System.getenv();
        for (String key : environmentMap.keySet()) {
            // Filter keys by kafka prefix
            if (key.toLowerCase().startsWith(ENV_PROP_PREFIX)) {
                String realKey = key.substring(ENV_PROP_PREFIX.length(), key.length()).toLowerCase();
                LOGGER.info("Found environment key: {} -> real key: {}", key, realKey);
                current = properties.getProperty(realKey);
                desired = environmentMap.get(key);
                if (current == null) {
                    properties.put(realKey, desired);
                    changed = true;
                    LOGGER.info("Updated {} -> {}", realKey, desired);
                } else {
                    if (!current.equals(desired)) {
                        properties.put(realKey, desired);
                        changed = true;
                        LOGGER.info("Updated {} -> {}", realKey, desired);
                    }
                }
            }
        }
        return changed;
    }

    /*
        #Generated at: Sun Apr 08 22:15:16 UTC 2018
        #Sun Apr 08 22:15:16 UTC 2018
        socket.send.buffer.bytes=102400
        socket.request.max.bytes=104857600
        log.retention.check.interval.ms=300000
        log.retention.hours=168
        num.io.threads=8
        broker.id=5
        transaction.state.log.replication.factor=1
        group.initial.rebalance.delay.ms=0
        log.dirs=/etc/kafka/install/data
        offsets.topic.replication.factor=1
        num.network.threads=3
        socket.receive.buffer.bytes=102400
        log.segment.bytes=1073741824
        num.recovery.threads.per.data.dir=1
        num.partitions=1
        transaction.state.log.min.isr=1
        zookeeper.connection.timeout.ms=6000
        zookeeper.connect=localhost\:2181
     */

    public void storePropertiesAsFile(Properties properties, Path pathToServerProperties) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(pathToServerProperties, StandardCharsets.UTF_8);
        properties.store(writer, String.format("Generated at: %s", new Date().toString()));
    }
}
