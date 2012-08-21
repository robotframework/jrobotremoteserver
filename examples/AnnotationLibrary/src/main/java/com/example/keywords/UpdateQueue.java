package com.example.keywords;

import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

// keywords that add or remove contents to the queue
@RobotKeywords
public class UpdateQueue
{
    @RobotKeyword("Adds the given `element` to tail of the queue.")
    @ArgumentNames({"element"})
    public void addToQueue(Object element) {
        QueueManager.getQueue().add(element);
    }

    @RobotKeyword("Removes the element at the head of the queue.")
    @ArgumentNames({})
    public Object removeFromQueue() {
        return QueueManager.getQueue().remove();
    }
}
