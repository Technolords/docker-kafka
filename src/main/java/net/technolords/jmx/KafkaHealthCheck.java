package net.technolords.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaHealthCheck {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public void createConnection() {
        LOGGER.info("About to connect...");
    }
}
