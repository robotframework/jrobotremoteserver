package org.robotframework.remoteserver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class RemoteLibraryClient {

    public static XmlRpcClient getClient(String path) {
        return getClient(path, 8270);
    }

    public static XmlRpcClient getClient(String path, int port) {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            config.setServerURL(new URL("http://127.0.0.1:" + Integer.toString(port) + path));
        } catch (MalformedURLException e) {
            // ignore
        }
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        return client;
    }

    public static Map runKeyword(String path, String keywordName, Object... params) {
        return runKeyword(8270, path, keywordName, params);
    }

    public static Map runKeyword(int port, String path, String keywordName, Object... params) {
        XmlRpcClient client = getClient(path, port);
        Map result = null;
        try {
            result = (Map) client.execute("run_keyword", new Object[] { keywordName, params });
        } catch (XmlRpcException e) {
            e.printStackTrace();
        }
        return result;
    }

}
