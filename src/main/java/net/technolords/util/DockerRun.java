package net.technolords.util;

import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerRun {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerRun.class);
    private CopyKafkaProperties copyKafkaProperties = new CopyKafkaProperties();
    private AugmentProperties augmentProperties = new AugmentProperties();

    public void initAndOrParseEnvironmentVariables() {
        try {
            // Initialize property files
            Path propertiesFolder = this.copyKafkaProperties.copyPropertiesFromRef();
            // Parse environment variables
            this.augmentProperties.augmentServerProperties(propertiesFolder);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        DockerRun dockerRunUtil = new DockerRun();
        dockerRunUtil.initAndOrParseEnvironmentVariables();
    }
}
