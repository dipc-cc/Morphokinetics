#!/usr/bin/env python3

import os
import glob
import info as inf
import shutil as sh

temperatures = inf.getTemperatures("float") 
surfaces = ' '.join("s"+str(t)+".svg" for t in temperatures)
workingPath = os.getcwd()
for i,t in enumerate(temperatures):
        print(t)
        os.chdir(workingPath)
        try:
            os.chdir(str(t)+"/results")
            runFolder = glob.glob("*/");
            runFolder.sort()
            os.chdir(runFolder[-1])
        except FileNotFoundError:
            continue

        os.system("grep -v white surface000.svg > s"+str(t)+".svg")
        os.system("sed -i -e 's/blue/black/g' s"+str(t)+".svg")
        os.system("sed -i -e 's/red/black/g' s"+str(t)+".svg")
        sh.copy("s"+str(t)+".svg",workingPath)

os.chdir(workingPath)
#os.system("montage $(ls *svg | sort -n) allSurfaces.png")
os.system("montage "+surfaces+" allSurfaces.png")
