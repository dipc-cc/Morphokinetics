# Morphokinetics

This is Morphokinetics software developed at the Donostia International Physics Center. Morphokinetics is a software to simulate kinetics Monte Carlo (KMC) processes. It can simulate  etching, CVD growing and catalysis processes. To specify simulation mode "parameters" file must be present in the current working directory (more details in {@link Parser}).


## COMPILATION

Simple instructions to "compile" the code: 
```bash 
cd src/
javac -source 8 -target 8 -encoding "UTF-8" -cp .:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar:../lib/colt.jar graphicInterfaces/growth/GrowthKmcFrame.java main/PcConfigurator.java main/Morphokinetics.java
mkdir ../perimeterData
cp ../scripts/perimeterStatistics/*txt ../perimeterData/
```

## RUN

Run the code:
In order to be able do a simulation with Morphokinetics, a file called "parameter" have to exits in the current directory. If there is not any file, default simulation is carried out.
```bash
java -Xss100M -cp  .:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar:../lib/colt.jar main.Morphokinetics
```

## TESTING
```bash
ant -Dnb.internal.action.name=test -Dignore.failing.tests=true test
```
or
```bash
cd test/
javac -encoding "UTF-8" -cp .:../lib/colt.jar:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar:$JUNIT_DIR/junit-4.12.jar:../src TestRunner.java

java -cp .:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar:$JUNIT_DIR/junit-4.12.jar:$HAMCREST_DIR/hamcrest-core-1.3.jar:../src TestRunner
```

## JAR

Beforehand creating a jar file, you have to compile with the above instruction. 
To create a portable JAR file, follow this instructions:
```bash
cd src
jar cfm morphokinetics.jar manifest.txt . ../lib/jtransforms-2.4.jar ../lib/j3dcore.jar  ../lib/j3dutils.jar ../lib/vecmath.jar ../lib/colt.jar  ../lib/json-20141113.jar
```

If you want to move the JAR file to another folder, you have to also move the lib, perimeterData and resources/png/ folders.


## PROFILING

To profile this code within NetBeans 8.1 next line has to be added:
```bash
-XX:+UseLinuxPosixThreadCPUClocks -agentpath:$NETBEANS_DIR/profiler/lib/deployed/jdk16/linux-amd64/libprofilerinterface.so=$NETBEANS_DIR/profiler/lib,5140
```

## TUTORIAL

A tutorial is provided in the following link: https://github.com/dipc-cc/Morphokinetics/wiki/User-guide#5-examples-parameters-files
