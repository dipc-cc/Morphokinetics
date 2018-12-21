#!/usr/bin/env python3

import os
import glob
import pdb
import Info as inf
import numpy as np
import shutil


oFolder = "runs"
fileBase = ["dataAe", "dataAeInstantaneousDiscrete", "dataAeMultiplicity", "dataAePossibleDiscrete", "dataAePossibleFromList", "dataAeRatioTimesPossible", "dataAeSuccess"]


mIter = np.array([i for i in inf.getTemperatures() if i <= 900]) #filter temperatures higher than 500

cwd = os.getcwd()
for t in mIter:
    os.chdir(str(t))
    if len(glob.glob("output*")) > 1:
        print(t)
        os.chdir("results")
        # create output folder
        shutil.rmtree(oFolder, ignore_errors=True)
        os.mkdir(oFolder)
        #except FileExistsError:
        #    print("merge folder was already there")
        runFolder = glob.glob("run1*/");
        runFolder.sort()
        os.chdir(oFolder)
        for i,f in enumerate(runFolder):
            number = str(i).zfill(3)
            dataFiles = glob.glob("dataAe*")
            print("\t",number)
            for d in fileBase:
                filename = d
                os.symlink(os.getcwd()+"/../"+f+filename+"000.txt", filename+number+".txt")
    os.chdir(cwd)
