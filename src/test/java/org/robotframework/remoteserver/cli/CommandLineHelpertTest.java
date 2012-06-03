package org.robotframework.remoteserver.cli;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CommandLineHelpertTest {

    @Test(alwaysRun = true)
    public void libraryRequired() {
	CommandLineHelper clh = new CommandLineHelper(new String[] { "-H", "host" });
	Assert.assertEquals(clh.getError(), "You must specify at least one library");
    }

    @Test(alwaysRun = true)
    public void moreThanOneLibrary() {
	CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "lib1:10", "--library", "lib2:20" });
	Map<Integer, String> expected = new HashMap<Integer, String>();
	expected.put(10, "lib1");
	expected.put(20, "lib2");
	Assert.assertEquals(clh.getLibraryMap(), expected);
    }

    @Test(alwaysRun = true)
    public void missingValues() {
	CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "lib:5", "--host" });
	Assert.assertEquals(clh.getError(), "Missing value for option host");
	clh = new CommandLineHelper(new String[] { "-l", "lib:5", "-a", "-H", "host" });
	Assert.assertEquals(clh.getError(), "Missing value for option allowstop");
    }

    @Test(alwaysRun = true)
    public void badValues() {
	CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "lib" });
	Assert.assertEquals(clh.getError(), "Value for library must be in the format classname:port");
	clh = new CommandLineHelper(new String[] { "--library", "lib:-5" });
	Assert.assertEquals(clh.getError(), "Port must be 1-65535");
	clh = new CommandLineHelper(new String[] { "--allowstop", "yes" });
	Assert.assertEquals(clh.getError(), "Value for option allowstop must be true or false");
    }

    @Test(alwaysRun = true)
    public void samePort() {
	CommandLineHelper clh = new CommandLineHelper(new String[] { "-l", "lib1:8080", "--library", "lib2:8080" });
	Assert.assertEquals(clh.getError(), "Cannot serve more than one library from the same port");
    }
}
