This is Morphokinetics software developed at the Donostia International Physics Center 

==========================================
======   COMPILATION           ===========
==========================================

Simple instructions to "compile" the code 
cd src/
javac -encoding "UTF-8" -cp .:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar main/Morphokinetics.java

==========================================
====== RUN                    ============
==========================================

Run the code:
In order to be able do a simulation with Morphokinetics, a file called "parameter" have to exits in the current directory. If there is not any file, default simulation is carried out.
java  -cp  .:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar main.Morphokinetics


==========================================
====== TESTING                ============
==========================================

ant -f /home/jalberdi004/NetBeansProjectsGit/ekmc-project -Dnb.internal.action.name=test -Dignore.failing.tests=true test

cd test/
javac -encoding "UTF-8" -cp .:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar:/home/jalberdi004/software/netbeans-8.0.2/platform/modules/junit-4.10.jar:../src TestRunner.java

java -cp .:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar:/home/jalberdi004/software/netbeans-8.0.2/platform/modules/junit-4.10.jar:../src TestRunner

==========================================
====== JAR                    ============
==========================================

Beforehand creating a jar file, you have to compile with the above instruction. 
To create a portable JAR file, follow this instructions:
cd src
jar cfm morphokinetics.jar manifest.txt . ../lib/jtransforms-2.4.jar ../lib/j3dcore.jar  ../lib/j3dutils.jar ../lib/vecmath.jar ../lib/colt.jar  ../lib/json-20141113.jar

If you want to move the jar to another folder, you have to also move the lib folder.
