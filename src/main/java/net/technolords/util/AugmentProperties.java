package net.technolords.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
    private static final String DEFAULT_ZOOKEEPER_CONNECT = "localhost:2181";   // zookeeper.connect
    private static final String ENV_PROP_PREFIX = "kafka.";


    public void augmentServerProperties(Path propertiesFolder) throws IOException {
        // Fetch server.properties
        Path pathToServerProperties = Paths.get(propertiesFolder.toAbsolutePath().toString(), SERVER_PROPERTIES);
        Properties serverProperties = this.readPropertiesFromPath(pathToServerProperties);
        LOGGER.info("Total properties found: {}", serverProperties.size());
        // Assert correct defaults
        boolean changed = this.checkDefaults(serverProperties);
        // TODO: introduce flag to detect changes (no changes = file is ok, no need to (re)write
        // TODO: change -> mv org to backup
        this.parseEnvironmentVariables(serverProperties);
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
        System.getenv().forEach((key, value) -> {
            if (key.toLowerCase().startsWith(ENV_PROP_PREFIX)) {
                String envKey = key.substring(0, ENV_PROP_PREFIX.length() + 1);
                LOGGER.info("Validating environment property ({} -> {}) -> {}", key, envKey, value);
//                current = properties;
            }
        });
        return changed;
    }

    public void createBackup() {

    }

    public void storePropertiesAsFile(Properties properties, Path root) {

    }
}
