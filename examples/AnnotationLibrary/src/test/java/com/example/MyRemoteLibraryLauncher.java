package com.example;

// A static API test library to allow starting the remote server when Robot Framework is executing in Jython 
public class MyRemoteLibraryLauncher
{
    public void startRemoteServer() throws Exception {
        MyRemoteLibrary.main(null);
    }
}
