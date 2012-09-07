package com.example.keywords;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

@RobotKeywords
public class QueueManager
{
    private Queue<Object> queue = new LinkedBlockingQueue<Object>();

    @RobotKeyword("Clears the queue.")
    @ArgumentNames({})
    public void clearQueue() {
        queue.clear();
    }

    @RobotKeyword("Adds the given `element` to tail of the queue.")
    @ArgumentNames({"element"})
    public void addToQueue(Object element) {
        queue.add(element);
    }

    @RobotKeyword("Removes the element at the head of the queue.")
    @ArgumentNames({})
    public Object removeFromQueue() {
        return queue.remove();
    }

    @RobotKeyword("Retrieves the queue.")
    @ArgumentNames({})
    public Queue<Object> getQueue() {
        return queue;
    }

    @RobotKeyword("Logs the elements in the queue.")
    @ArgumentNames({})
    public void logQueue() {
        StringBuilder sb = new StringBuilder();
        sb.append("*HTML* <table border='1'><tr><th>Value</th><th>Type</th></tr>");
        Iterator<Object> iter = queue.iterator();
        while (iter.hasNext()) {
            Object element = iter.next();
            String value = escapeHtml(element.toString());
            sb.append("<tr>");
            sb.append("<td>" + value + "</td>");
            sb.append("<td>" + element.getClass().getName() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        System.out.print(sb.toString());
    }
}
