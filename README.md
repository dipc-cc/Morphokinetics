# Morphokinetics

This is Morphokinetics software developed at the Donostia International Physics Center. Morphokinetics is a software to simulate kinetics Monte Carlo (KMC) processes. It can simulate  etching, CVD growing and catalysis processes. To specify simulation mode "parameters" file must be present in the current working directory (more details in {@link Parser}).

You can either compile the code automatically using `ant` or manually invoking `javac`.

## Papers using Morphokinetics

* Multiscale Analysis of Phase Transformations in Self-Assembled Layers of 4,4′-Biphenyl Dicarboxylic Acid on the Ag(001) Surface, ACS Nano, 2020. https://doi.org/10.1021/acsnano.0c02491
* Dominant contributions to the apparent activation energy in two-dimensional submonolayer growth: Comparison between Cu/Ni(111) and Ni/Cu(111), Journal of Physics: Condensed Matter, 2020. https://doi.org/10.1088/1361-648X/ab9b50
* Microscopic Origin of the Apparent Activation Energy in Diffusion-Mediated Monolayer Growth of Two-Dimensional Materials, The Journal of Physical Chemistry C, 2017. https://doi.org/10.1021/acs.jpcc.7b05794
* A microscopic perspective on heterogeneous catalysis, arVix, 2018. https://arxiv.org/abs/1812.11398


## ANT (simpler and faster)

Next two steps should be enough to run Morphokinetics:
```bash
ant jar 
java -jar dist/morphokinetics.jar
```

## MANUAL COMPILATION

Alternatively, you can use javac commands to compile the code. Instructions to compile the code manually: 
```bash 
cd src/
javac -source 8 -target 8 -encoding "UTF-8" -cp .:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar:../lib/colt.jar graphicInterfaces/growth/GrowthKmcFrame.java main/PcConfigurator.java main/Morphokinetics.java
mkdir ../perimeterData
cp ../scripts/perimeterStatistics/*txt ../perimeterData/
```

### Run

Run the code:
In order to be able do a simulation with Morphokinetics, a file called "parameter" have to exits in the current directory. If there is not any file, default simulation is carried out.
```bash
java -Xss100M -cp  .:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar:../lib/colt.jar main.Morphokinetics
```

### Jar

Beforehand creating a jar file, java code must be compiled (with the above instruction). 
To manually create a portable JAR file, follow this instructions:
```bash
cd src
jar cfm morphokinetics.jar manifest.txt . ../lib/jtransforms-2.4.jar ../lib/j3dcore.jar  ../lib/j3dutils.jar ../lib/vecmath.jar ../lib/colt.jar  ../lib/json-20141113.jar
```

If you want to move the JAR file to another folder, you have to also move the lib, perimeterData and resources/png/ folders.


## TESTING
```bash
ant test
```
or
```bash
cd test/
javac -encoding "UTF-8" -cp .:../lib/colt.jar:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar:../lib/junit-4.13.jar:../src TestRunner.java

java -cp .:../lib/json-20141113.jar:../lib/j3dcore.jar:../lib/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/vecmath.jar:../lib/junit-4.13.jar:../lib/hamcrest-core-1.3.jar:../src TestRunner
```

## PROFILING

To profile this code within NetBeans 8.1 next line has to be added:
```bash
-XX:+UseLinuxPosixThreadCPUClocks -agentpath:$NETBEANS_DIR/profiler/lib/deployed/jdk16/linux-amd64/libprofilerinterface.so=$NETBEANS_DIR/profiler/lib,5140
```

## TUTORIAL

A tutorial is provided in the following link: https://github.com/dipc-cc/Morphokinetics/wiki/User-guide#5-examples-parameters-files
