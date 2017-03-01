#!/bin/bash
#Start h2 data base server.
baseDir=/home/holweb/server/db-java-h2-server
echo $baseDir
su - holweb -c "java -cp $baseDir/h2-1.3.176.jar org.h2.tools.Server -tcpAllowOthers -baseDir $baseDir &"
ps axf|grep java
