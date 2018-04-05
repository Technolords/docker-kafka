package net.technolords.util;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class PropertiesManagerTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    public static final String TEST_PROPERTIES = "src/test/resources/source/server.properties";

    @Test
    public void testReadProperties() throws IOException {
        LOGGER.info("About to test loading of properties");
        PropertiesManager propertiesManager = new PropertiesManager();
        Properties properties = propertiesManager.readPropertiesFromPath(TEST_PROPERTIES);
        Assert.assertNotNull(properties);
        Assert.assertTrue(properties.size() > 0);
        LOGGER.info("Total properties: {}", properties.size());
    }
}