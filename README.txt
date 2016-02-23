This is Morphokinetics software developed at the Donostia International Physics Center 

==========================================
======   COMPILATION           ===========
==========================================

Simple instructions to "compile" the code 
cd src/
javac -source 7 -target 7 -encoding "UTF-8" -cp .:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar:../lib/colt.jar main/Morphokinetics.java
mkdir ../perimeterData
cp ../scripts/perimeterStatistics/reentrancesPerAngleHexagonal* ../perimeterData/
cp ../scripts/perimeterStatistics/hopsPerAngleHexagonal* ../perimeterData/

==========================================
====== RUN                    ============
==========================================

Run the code:
In order to be able do a simulation with Morphokinetics, a file called "parameter" have to exits in the current directory. If there is not any file, default simulation is carried out.
java -Xss100M -cp  .:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar:../lib/colt.jar main.Morphokinetics


==========================================
====== TESTING                ============
==========================================

ant -f /home/jalberdi004/NetBeansProjectsGit/ekmc-project -Dnb.internal.action.name=test -Dignore.failing.tests=true test

cd test/
javac -encoding "UTF-8" -cp .:../lib/colt.jar:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar:/home/jalberdi004/software/netbeans-8.1/platform/modules/ext/junit-4.12.jar:../src TestRunner.java

java -cp .:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar:/home/jalberdi004/software/netbeans-8.1/platform/modules/ext/junit-4.12.jar:/home/jalberdi004/software/netbeans-8.1/platform/modules/ext/hamcrest-core-1.3.jar:../src TestRunner


==========================================
====== JAR                    ============
==========================================

Beforehand creating a jar file, you have to compile with the above instruction. 
To create a portable JAR file, follow this instructions:
cd src
jar cfm morphokinetics.jar manifest.txt . ../lib/jtransforms-2.4.jar ../lib/j3dcore.jar  ../lib/j3dutils.jar ../lib/vecmath.jar ../lib/colt.jar  ../lib/json-20141113.jar

If you want to move the jar to another folder, you have to also move the lib folder.


==========================================
====== PROFILING              ============
==========================================

To profile this code within NetBeans 8.1 next line has to be added:
-XX:+UseLinuxPosixThreadCPUClocks -agentpath:/home/jalberdi004/software/netbeans-8.1/profiler/lib/deployed/jdk16/linux-amd64/libprofilerinterface.so=/home/jalberdi004/software/netbeans-8.1/profiler/lib,5140
