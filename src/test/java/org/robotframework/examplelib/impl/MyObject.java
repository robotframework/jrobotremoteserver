package org.robotframework.examplelib.impl;

public class MyObject {
    
    MyObject(String id) {
        this.id = id;
    }
    
    public String toString() {
        return String.format("<MyObject%s>", id);
    }
    
    private String id;
}
