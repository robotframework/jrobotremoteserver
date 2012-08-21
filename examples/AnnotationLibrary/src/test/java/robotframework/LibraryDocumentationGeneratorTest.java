package robotframework;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.robotframework.RobotFramework;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LibraryDocumentationGeneratorTest {   
    /*
     * Generates the library documentation, which is not really a test. It would be more desirable to do this with
     * robotframework-maven-plugin, but it does not support dynamic API libraries at this time.
     */
    @Test
    public void generate()
    {
        Properties props = getMavenProperties();
        String name = props.getProperty("project.name");
        String version = props.getProperty("project.version");
        String testLibraryClass = props.getProperty("testLibraryClass");
        new File("target/robotframework").mkdirs();
        for (String type : new String[] {"xml", "html"}) {
            List<String> args = new ArrayList<String>();
            args.add("libdoc");
            args.add("--name");
            args.add(name);
            args.add("--version");
            args.add(version);
            args.add("--format");
            args.add(type);
            args.add(testLibraryClass);
            args.add(String.format("target/robotframework/%s-%s.%s", name, version, type));
            int exitCode = RobotFramework.run(args.toArray(new String[] {}));
            Assert.assertEquals(exitCode, 0, String.format("Failed to generate %s library documentation", type));
        }
    }
    
    public Properties getMavenProperties()
    {
        Properties props = new Properties();
        try {
            InputStream in = this.getClass().getResourceAsStream("maven.properties");
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return props;
    }
}
