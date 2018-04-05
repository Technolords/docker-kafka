package net.technolords.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesManager {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String SERVER_PROPERTIES = "server.properties";

    public void executeMode() {
        LOGGER.info("Executing init mode...");
        // TODO: find source
        // TODO: use environment variables
        // TODO: copy
    }

    public Properties readPropertiesFromPath(String path) throws IOException {
        Path pathToData = FileSystems.getDefault().getPath(path);
        LOGGER.info("File exists: {}", Files.exists(pathToData));
        Properties properties = new Properties();
        properties.load(Files.newInputStream(pathToData, StandardOpenOption.READ));
        return properties;
    }

    public void storePropertiesAsFile(Properties properties, Path root) {

    }
}
