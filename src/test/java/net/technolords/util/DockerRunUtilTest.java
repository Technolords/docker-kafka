package net.technolords.util;

import org.testng.annotations.Test;

public class DockerRunUtilTest {
    public static final String TEST_PROPERTIES = "src/test/resources/test.properties";

    @Test
    public void testParsingProperties() {
        DockerRunUtil kafkaProperties = new DockerRunUtil();
        kafkaProperties.parseProperties(TEST_PROPERTIES);
    }

}