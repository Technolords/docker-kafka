package net.technolords.util;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerRunUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerRunUtil.class);

    public void parseProperties(String path) {
        LOGGER.info("Parse properties called");
        Path pathToData = FileSystems.getDefault().getPath(path);
        LOGGER.info("File exists: {}", Files.exists(pathToData));
    }

    public static void main(String[] args) {
        DockerRunUtil dockerRunUtil = new DockerRunUtil();
        if (args.length != 1) {
            LOGGER.info("Expect 1 argument (representing a path to the server.properties");
            System.exit(1);
        }
        dockerRunUtil.parseProperties(args[0]);
    }
}
