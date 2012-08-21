A bare-bones example of how to use jrobotremoteserver would be quite simple. This example instead aims to show how to use jrobotremoteserver together with other Robot Framework-related tools to create a remote test library that is properly documented and easy to develop.

Building the Example
--------------------
At the root directory of the project execute 'mvn clean package'. This will build the project, run tests, and generate library documentation. Maven must be installed and located on the PATH (see http://maven.apache.org/download.html#Installation).

Starting the Remote Library
---------------------------
After building the project, from the project directory execute 'java -jar target/MyRemoteLibrary-1.0-jar-with-dependencies.jar'. Depending on your installation of Java, you may be also able to type just the name of the jar file or double-click on the jar file to start the server.

Project Structure
-----------------
This project follows the Maven conventions for the location of all files. The default locations used by robotframework-maven-plugin are used as well.
The library keywords are implemented in several classes. This is not necessary for a library as simple as this, but the capability to do so is important and something that JavaLib Core provides.

Library Documentation
---------------------
The library documentation is created during a unit test. Ideally this would be done using the libdoc goal of robotframework-maven-plugin, but it does not support dynamic API libraries that have dependencies.

Acceptance Testing
------------------
The testing is executed during the build using robotframework-maven-plugin. The suite is set up to start and stop the remote server only when running as part of the build process. 

Build Artifacts
---------------
target/MyRemoteLibrary-1.0.jar - test library alone
target/MyRemoteLibrary-1.0-jar-with-dependencies.jar - test library including dependencies (this is runnable)
target/robotframework/MyRemoteLibrary-1.0.html - library documentation for humans
target/robotframework/MyRemoteLibrary-1.0.xml - library documentation for RIDE