package com.example.keywords;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

// this class owns the queue
@RobotKeywords
public class QueueManager
{
    private static Queue<Object> queue = new LinkedBlockingQueue<Object>();
    
    /*
     * This singleton-like design means that you could only serve one instance of this test library per virtual
     * machine. A more flexible approach would be to maintain a Map containing instances of this class keyed by port.
     * The constructor could insert the instance into the Map. To determine the port, get the request with
     * {@link org.robotframework.remoteserver.servlet.RemoteServerServlet#getRequest()}.
     */
    static Queue<Object> getQueue() {
        return queue;
    }
    
    @RobotKeyword("Clears the queue.")
    @ArgumentNames({})
    public void clearQueue() {
        queue.clear();
    }
}
