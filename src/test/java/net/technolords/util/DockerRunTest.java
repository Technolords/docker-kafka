package net.technolords.util;

import org.testng.annotations.Test;

public class DockerRunTest {
    private static final boolean INTEGRATION_TEST_ENABLED = false;

    @Test (enabled = INTEGRATION_TEST_ENABLED)
    public void testInitMode() {
        DockerRun dockerRun = new DockerRun();
        dockerRun.executeMode("init");
    }

    @Test (enabled = INTEGRATION_TEST_ENABLED)
    public void testEnvMode() {
        DockerRun dockerRun = new DockerRun();
        dockerRun.executeMode("env");
    }

    @Test (enabled = INTEGRATION_TEST_ENABLED)
    public void testUnsupportedMode() {
        DockerRun dockerRun = new DockerRun();
        dockerRun.executeMode("whatever");
    }

}