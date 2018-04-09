package net.technolords.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

    /**
     * Auxiliary method to augment the properties. This consists of the following steps:
     * - read properties from file.
     * - check whether the defaults are correct.
     * - check if relevant environment variables are present and update where needed.
     * - if any changes where made, write this back to the file.
     *
     * Note that relevant environment variables mean keys with a specific prefix: kafka.
     * So kafka.whatever is a valid key. Even when this key is not present of the current
     * configuration it is added regardless. Which means this mechanism is future proof
     * as in you can define any key and (future) Kafka can/will use it.
     *
     * Example output (of a change property):
     *
     * 2018-04-08 22:15:16,366 [INFO] [main] [net.technolords.util.AugmentProperties] Updated broker.id -> 5
     *
     * @param propertiesFolder
     *  The folder where the property file(s) are.
     *
     * @throws IOException
     *  When reading or writing fails.
     */
    public void augmentServerProperties(Path propertiesFolder) throws IOException {
        // Fetch server.properties
        Path pathToServerProperties = Paths.get(propertiesFolder.toAbsolutePath().toString(), SERVER_PROPERTIES);
        Properties serverProperties = this.readPropertiesFromPath(pathToServerProperties);
        LOGGER.debug("Total properties found: {}", serverProperties.size());
        // Assert correct defaults
        boolean changed = this.checkDefaults(serverProperties);
        // Assert environment variables
        changed |= this.parseEnvironmentVariables(serverProperties);
        if (changed) {
            this.storePropertiesAsFile(serverProperties, pathToServerProperties);
        }
    }

    /**
     * Auxiliary method to read the properties.
     *
     * @param pathToData
     *  The path to the property file.
     * @return
     *  The properties.
     *
     * @throws IOException
     *  When reading the properties fails.
     */
    public Properties readPropertiesFromPath(Path pathToData) throws IOException {
        LOGGER.debug("File exists: {}", Files.exists(pathToData));
        Properties properties = new Properties();
        properties.load(Files.newInputStream(pathToData, StandardOpenOption.READ));
        return properties;
    }

    /**
     * Check for correct default values and change these where needed.
     *
     * @param properties
     *  The current loaded properties.
     * @return
     *  Whether one or more properties where changed.
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
     * Auxiliary method to check for (relevant) environment variables, and when present check if these
     * are up to date.
     *
     * @param properties
     *  The current properties.
     * @return
     *  Whether the properties where changed.
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
                LOGGER.debug("Found environment key: {} -> real key: {}", key, realKey);
                current = properties.getProperty(realKey);
                desired = environmentMap.get(key);
                if (current == null) {
                    properties.put(realKey, desired);
                    changed = true;
                    LOGGER.info("Added {} -> {}", realKey, desired);
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

    /**
     * Auxiliary method to write the properties to file. Example output:
     *
     *  #Sun Apr 08 22:15:16 UTC 2018
     *  socket.send.buffer.bytes=102400
     *  socket.request.max.bytes=104857600
     *  log.retention.check.interval.ms=300000
     *  log.retention.hours=168
     *  num.io.threads=8
     *  broker.id=5
     *  transaction.state.log.replication.factor=1
     *  group.initial.rebalance.delay.ms=0
     *  log.dirs=/etc/kafka/install/data
     *  offsets.topic.replication.factor=1
     *  num.network.threads=3
     *  socket.receive.buffer.bytes=102400
     *  log.segment.bytes=1073741824
     *  num.recovery.threads.per.data.dir=1
     *  num.partitions=1
     *  transaction.state.log.min.isr=1
     *  zookeeper.connection.timeout.ms=6000
     *  zookeeper.connect=localhost\:2181
     *
     * @param properties
     *  The properties to be written.
     * @param pathToServerProperties
     *  The path of the properties file.
     *
     * @throws IOException
     *  When writing the properties file fails.
     */
    public void storePropertiesAsFile(Properties properties, Path pathToServerProperties) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(pathToServerProperties, StandardCharsets.UTF_8);
        properties.store(writer, "Generated by: DockerRun");
    }
}
