package net.technolords.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyKafkaProperties {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String PATH_SOURCE = "config-ref";
    private static final String PATH_TARGET = "config";

    public Path copyPropertiesFromRef() throws IOException {
        Path pathToSource = FileSystems.getDefault().getPath(PATH_SOURCE);
        Path pathToTarget = FileSystems.getDefault().getPath(PATH_TARGET);
        return this.copyPropertiesFromRef(pathToSource, pathToTarget);
    }

    protected Path copyPropertiesFromRef(Path source, final Path target) throws IOException {
        LOGGER.info("About to copy property files from {} to {}", source, target);
        Path result;

        // Verify source folder
        LOGGER.info("Source folder exist: {}", Files.exists(source));
        if (!Files.exists(source)) {
            throw new IllegalStateException("Source folder should exist but doesn't");
        }
        if (!Files.isDirectory(source)) {
            throw new IllegalStateException("Source folder should be a directory but isn't");
        }
        // Verify target folder
        LOGGER.info("Target folder exist: {}", Files.exists(target));
        if (!Files.exists(target)) {
            LOGGER.info("Creating target folder...");
            result = Files.createDirectory(target);
        } else {
            result = target;
        }

        // Copy property files
        Files.walk(source, 1)
                .filter(path -> path.toString().endsWith(".properties"))
                .forEach(path -> {
                    LOGGER.info("About to copy: {}", path.toString());
                    Path newTarget = Paths.get(target.toAbsolutePath().toString(), path.getFileName().toString());
                    try {
                        Files.copy(path, newTarget);
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                });

        return result;
    }
}
