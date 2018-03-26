package net.technolords.jmx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class KafkaHealthCheckTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Test
    public void testKafkaConnection() {
        LOGGER.info("About to test...");
    }
}