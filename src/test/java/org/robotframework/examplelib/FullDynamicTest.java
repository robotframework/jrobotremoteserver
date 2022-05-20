package org.robotframework.examplelib;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import org.robotframework.remoteserver.servlet.ServerMethods;
import org.testng.Assert;
import org.testng.annotations.Test;
public class FullDynamicTest {

	
	@Test
	public void testCallingTwice() throws IOException, ServletException {
		FullDynamic dynamic = new FullDynamic();
		SimpleRemoteServerServlet servlet = new SimpleRemoteServerServlet(dynamic.getLib());
		ServerMethods methods = new ServerMethods(servlet);
				
		Map<String, Object> map1 = methods.get_library_information();
		Map<String, Object> map2 = methods.get_library_information();
		// The exact count we don't care about.
		// We just want the information to stay consistent.
		Assert.assertEquals(map1.size(), map2.size());
	}
}
