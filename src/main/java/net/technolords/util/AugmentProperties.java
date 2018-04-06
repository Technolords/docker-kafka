package net.technolords.util;

import java.io.IOException;
import java.nio.file.FileSystems;
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

    public void augmentServerProperties(Path propertiesFolder) throws IOException {
        // Fetch server.properties
        Path pathToServerProperties = Paths.get(propertiesFolder.toAbsolutePath().toString(), SERVER_PROPERTIES);
        Properties serverProperties = this.readPropertiesFromPath(pathToServerProperties);
        LOGGER.info("Total properties found: {}", serverProperties.size());
        // Assert correct defaults
        // TODO: introduce flag to detect changes (no changes = file is ok, no need to (re)write
    }

    public Properties readPropertiesFromPath(Path pathToData) throws IOException {
        LOGGER.info("File exists: {}", Files.exists(pathToData));
        Properties properties = new Properties();
        properties.load(Files.newInputStream(pathToData, StandardOpenOption.READ));
        return properties;
    }

    public void storePropertiesAsFile(Properties properties, Path root) {

    }
}
