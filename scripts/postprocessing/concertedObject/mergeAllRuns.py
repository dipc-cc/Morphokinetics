#!/usr/bin/env python3

import os
import glob
import pdb
import Info as inf
import numpy as np
import shutil


oFolder = "runs"
fileBase = ["dataAe", "dataAeInstantaneousDiscrete", "dataAeMultiplicity", "dataAePossibleDiscrete", "dataAePossibleFromList", "dataAeRatioTimesPossible", "dataAeSuccess"]


mIter = np.array([i for i in inf.getTemperatures() if i <= 1000]) #filter temperatures higher than 500

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
        #os.chdir(oFolder)
        index = 0
        for i,f in enumerate(runFolder):
            os.chdir(cwd+"/"+str(t)+"/results/"+f)
            dataFiles = glob.glob("dataAe???.txt")
            dataFiles.sort()
            for j,cfile in enumerate(dataFiles):
                number = str(index).zfill(3)
                print("\t",number)
                for k in range(0,10):
                    os.symlink(os.getcwd()+"/surface10"+str(k)+"0.mko", os.getcwd()+"/../runs/surface"+str(index)+"0"+str(k)+"0.mko")
                for d in fileBase:
                    filename = d
                    #os.symlink(os.getcwd()+"/../"+f+filename+"000.txt", filename+number+".txt")
                    os.symlink(os.getcwd()+"/"+filename+str(j).zfill(3)+".txt", os.getcwd()+"/../runs/"+filename+number+".txt")
                index += 1
    os.chdir(cwd)
