package org.robotframework.remoteserver.cli;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CommandLineHelpertTest {

    @Test
    public void libraryRequired() {
        CommandLineHelper clh = new CommandLineHelper(new String[] { "-H", "host" });
        Assert.assertEquals(clh.getError(), "You must specify at least one library");
    }

    @Test
    public void moreThanOneLibrary() {
        CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "java.lang.String:/one", "--library",
                "java.lang.Object:/two" });
        Assert.assertEquals(clh.getLibraryMap().size(), 2);
        Assert.assertEquals(clh.getLibraryMap().get("/one").getClass(), String.class);
        Assert.assertEquals(clh.getLibraryMap().get("/two").getClass(), Object.class);
    }

    @Test
    public void setPort() {
        CommandLineHelper clh = new CommandLineHelper(new String[] { "--library", "java.lang.String:/one", "--port",
                "4444" });
        Assert.assertEquals(clh.getError(), null);
        Assert.assertEquals(clh.getPort(), 4444);
    }

    @Test
    public void missingValues() {
        CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "java.lang.String:5", "--host" });
        Assert.assertEquals(clh.getError(), "Missing value for option host");
        clh = new CommandLineHelper(new String[] { "-l", "java.lang.String:5", "-a", "-H", "host" });
        Assert.assertEquals(clh.getError(), "Missing value for option allowstop");
    }

    @Test
    public void badValues() {
        CommandLineHelper clh = new CommandLineHelper(new String[] { "--port", "blah" });
        Assert.assertEquals(clh.getError(), "Port must be 1-65535");
        clh = new CommandLineHelper(new String[] { "--allowstop", "yes" });
        Assert.assertEquals(clh.getError(), "Value for option allowstop must be true or false");
    }

    @Test
    public void defaultPath() {
        CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "java.lang.String" });
        Assert.assertEquals(clh.getError(), null);
        Assert.assertEquals(clh.getLibraryMap().size(), 1);
        Assert.assertEquals(clh.getLibraryMap().get("/").getClass(), String.class);
    }

    @Test
    public void samePath() {
        CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "java.lang.String:/5", "--library",
                "java.lang.Object:/5" });
        Assert.assertEquals(clh.getError(), "Duplicate path [/5]");
    }

    @Test
    public void missingPath() {
        CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "java.lang.String:" });
        Assert.assertEquals(clh.getError(), "Missing path for library java.lang.String");
        clh = new CommandLineHelper(new String[] { "-l", "java.lang.String:   " });
        Assert.assertEquals(clh.getError(), "Missing path for library java.lang.String");
    }

    @Test
    public void badClassName() {
        CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "BadClassName" });
        Assert.assertEquals(clh.getError(),
                "Failed to load class with name BadClassName: java.lang.ClassNotFoundException: BadClassName");
    }

    @Test
    public void libraryWithPort() {
        CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "java.lang.String:8270" });
        Assert.assertEquals(clh.getError(), null);
        Assert.assertEquals(clh.getLibraryMap().size(), 1);
        Assert.assertEquals(clh.getLibraryMap().get("/").getClass(), String.class);
        Assert.assertEquals(clh.getPort(), 8270);
    }

    @Test
    public void multipleLibrariesWithPort() {
        CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "java.lang.String:8270", "-l",
                "java.lang.String:8271" });
        Assert.assertEquals(clh.getError(),
                "Cannot use the port option or use multiple libraries when specifying libraries in the form classname:port");
    }

    @Test
    public void setPortAndLibraryWithPort() {
        CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "java.lang.String:8270", "-p",
                "8270" });
        Assert.assertEquals(clh.getError(),
                "Cannot use the port option or use multiple libraries when specifying libraries in the form classname:port");
    }

}
