package com.example.keywords;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import java.util.Iterator;
import java.util.Queue;

import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

// keywords that inspect the contents of the queue but do not modify it
@RobotKeywords
public class ViewQueue
{
    @RobotKeyword("Retrieves the queue.")
    @ArgumentNames({})
    public Queue<Object> getQueue() {
        return QueueManager.getQueue();
    }

    @RobotKeyword("Logs the elements in the queue.")
    @ArgumentNames({})
    public void logQueue() {
        StringBuilder sb = new StringBuilder();
        sb.append("*HTML* <table border='1'><tr><th>Value</th><th>Type</th></tr>");
        Iterator<Object> iter = QueueManager.getQueue().iterator();
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
