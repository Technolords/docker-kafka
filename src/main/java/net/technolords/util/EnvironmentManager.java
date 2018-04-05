package net.technolords.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentManager {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public EnvironmentManager() {
    }

    public void executeMode() {
        LOGGER.info("Executing env mode...");
    }

}
