package net.technolords.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class CopyKafkaPropertiesTest {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Test
    public void testCopyOfPropertyFiles() throws IOException {
        LOGGER.info("About to test copy of property files...");
        // Setup of paths
        final String pathToSource = "src/test/resources/source";
        Path pathToSourceFolder = FileSystems.getDefault().getPath(pathToSource);
        Path pathToTmpDirectory = Files.createTempDirectory("unit-test-");
        // Invoke copy
        CopyKafkaProperties copyKafkaProperties = new CopyKafkaProperties();
        Path result = copyKafkaProperties.copyPropertiesFromRef(pathToSourceFolder, pathToTmpDirectory);
        // Minus 1 because the walk includes the parent folder as well
        LOGGER.info("Total files in {}: {}", result.toAbsolutePath().toString(), Files.walk(result, 1).count() - 1);
    }
}