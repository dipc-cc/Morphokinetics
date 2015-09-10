This is Morphokinetics software developed at the Donostia International Physics Center 

==========================================
======   COMPILATION           ===========
==========================================

Simple instructions to "compile" the code 
cd src/
javac -encoding "UTF-8" -cp .:../lib/ext/json-20141113.jar:../lib/ext/j3dcore.jar:../lib/ext/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/ext/vecmath.jar main/Morphokinetics.java

==========================================
====== RUN                    ============
==========================================

Run the code:
In order to be able do a simulation with Morphokinetics, a file called "parameter" have to exits in the current directory. If there is not any file, default simulation is carried out.
java  -cp  .:../lib/ext/json-20141113.jar:../lib/ext/j3dcore.jar:../lib/ext/j3dutils.jar:../lib/jtransforms-2.4.jar:../lib/ext/vecmath.jar main.Morphokinetics

