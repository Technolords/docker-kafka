package net.technolords.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AugmentPropertiesTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    public static final String TEST_PROPERTIES = "src/test/resources/source/server.properties";

    @Test
    public void testReadProperties() throws IOException {
        LOGGER.info("About to test loading of properties");
        AugmentProperties augmentServerProperties = new AugmentProperties();
        Path pathToData = FileSystems.getDefault().getPath(TEST_PROPERTIES);
        Properties properties = augmentServerProperties.readPropertiesFromPath(pathToData);
        Assert.assertNotNull(properties);
        Assert.assertTrue(properties.size() > 0);
        LOGGER.info("Total properties: {}", properties.size());
    }

    @Test
    public void testParseEnvironmentVariables() throws IOException {
        LOGGER.info("About to test parsing of environment variables");
        this.updateEnvironmentByReflection("KAFKA.BROKER.ID", "5");
        AugmentProperties augmentServerProperties = new AugmentProperties();
        Properties refProperties = new Properties();
        refProperties.put("broker.id", "0");
        boolean changed = augmentServerProperties.parseEnvironmentVariables(refProperties);
        Assert.assertTrue(changed);
    }

    public static void updateEnvironmentByReflection(String key, String value) {
        try {
            Map<String, String> env = System.getenv();
            Class<?> cl = env.getClass();
            Field field = cl.getDeclaredField("m");
            field.setAccessible(true);
            Map<String, String> writableEnv = (Map<String, String>) field.get(env);
            writableEnv.put(key, value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set environment variable", e);
        }
    }

}