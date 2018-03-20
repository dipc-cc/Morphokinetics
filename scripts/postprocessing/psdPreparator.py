# Copyright (C) 2018 J. Alberdi-Rodriguez
#
# This file is part of Morphokinetics.
#
# Morphokinetics is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Morphokinetics is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.
import numpy as np
import os
import itertools
import json
import shutil as sh
import glob
from pathlib import Path
import datetime

class PsdPreparator:
    """ Creates simple parameter files"""
    coverages = [10,20,30]#np.arange(1,100)
    cov = 0

    # constants
    calculationType = "psd"
    numberOfSimulations = 10
    outputData = False
    withGui = False

    def __init__(self):
        os.chdir("results")
        runFolder = glob.glob("*/")
        runFolder.sort()
        os.chdir(runFolder[-1])
        self.workingPath = os.getcwd()+"/"
        self.folders = []
        
    def generateParams(self):
        return self.coverages

    def writeParameters(self,params):
        os.chdir(self.workingPath)
        f = open('parameters', 'w')
        self.cov = int(params)
        f.write(json.dumps({"calculationType": self.calculationType,
                            "numberOfSimulations": self.numberOfSimulations,
                            "outputData": self.outputData,
                            "withGui": self.withGui,
                            "coverage": self.cov,
                    }, sort_keys=True, indent=2)) 
    def runPsd(self):
        cmd = "java -jar $HOME/ownCloud/ekmc-project/dist/morphokinetics.jar 2>&1 >/dev/null"
        fname = "results/psd"+str(self.cov).zfill(2)+".txt"
        if self.isNewer(fname, "../../output"): #run only if it is not done
            print("Running Morphokinetics...")
            os.system(cmd)

    def plotPsd(self):
        os.chdir("results")
        sh.copy("psdAvgRaw.txt", "psd"+str(self.cov).zfill(2)+".txt")
        cmd = "plot_psd.sh"
        fname = "psd"+str(self.cov).zfill(2)+".png"
        if self.isNewer(fname,"../../../output"):
            print("Running GNUplot")
            os.system(cmd)
            sh.copy("psd.png", fname)
            
    def isNewer(self, firstFile, secondFile):
        
        try:
            fmtime = os.path.getmtime(firstFile)
            smtime = os.path.getmtime(secondFile)
        except OSError:
            fmtime = 1
            smtime = 0
        return fmtime < smtime
