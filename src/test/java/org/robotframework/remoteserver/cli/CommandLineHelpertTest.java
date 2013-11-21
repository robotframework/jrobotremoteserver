package org.robotframework.remoteserver.cli;

import java.util.HashMap;
import java.util.Map;

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
        Map<String, Class<?>> expected = new HashMap<String, Class<?>>();
        expected.put("/one", String.class);
        expected.put("/two", Object.class);
        Assert.assertEquals(clh.getLibraryMap(), expected);
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
    public void samePath() {
        CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "java.lang.String", "--library",
                "java.lang.Object" });
        Assert.assertEquals(clh.getError(), "Duplicate path [/]");
    }

    @Test
    public void badClassName() {
        CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "BadClassName" });
        Assert.assertEquals(clh.getError(),
                "Failed to load class with name BadClassName: java.lang.ClassNotFoundException: BadClassName");
    }

}
