package net.technolords.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerRun {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerRun.class);
    private static final String MODE_INIT = "init";
    private static final String MODE_ENV = "env";
    private CopyKafkaProperties copyKafkaProperties = new CopyKafkaProperties();
    private PropertiesManager propertiesManager = new PropertiesManager();
    private EnvironmentManager environmentManager = new EnvironmentManager();

    public void executeMode(String mode) {
        try {
            switch (mode.toLowerCase()) {
                case MODE_INIT:
                    LOGGER.info("Executing init mode...");
                    // Copy properties
                    this.copyKafkaProperties.copyPropertiesFromRef();
                    // TODO: change defaults and process env
                    break;
                case MODE_ENV:
                    LOGGER.info("Executing env mode...");
                    // TODO: process env and more
                    this.environmentManager.executeMode();
                    break;
                default:
                    LOGGER.info("Unsupported mode: {}", mode.toLowerCase());
                    System.exit(1);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        DockerRun dockerRunUtil = new DockerRun();
        if (args.length != 1) {
            LOGGER.info("Expecting 1 argument for mode of operation (init or env)");
            System.exit(1);
        }
        dockerRunUtil.executeMode(args[0]);
    }
}
