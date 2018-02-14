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

import stat
import os
import itertools 
import json
from pathlib import Path

class Configurator:
    """ All different possibilities to be run """
    coverages = [100]
    #coverages = [10, 20, 30, 100]
    islandDiff = [False, True]
    multiAtomDiff = [False, True]
    matters = ["AgAg", "CuNi", "NiCu", "NiNi", "PdPd"] #"CuCu",
    #matters = ["AgAg", "CuCu", "CuNi", "NiCu", "NiNi", "PdPd"] # 
    temperatures = [25, 50, 75, 100, 150, 200, 350, 500, 1000]

    # constants
    calculationMode = "concerted"
    numberOfSimulations = 10
    justCentralFlake = False
    cartSizeX = 200
    cartSizeY = 200
    outputData = True
    randomSeed = False
    depositionFlux = 1.5e4
    forceNucleation = False
    automaticCollections = True
    withGui = False

    workingPath = ""
    folders = []
    
    def __init__(self):
        self.workingPath = os.getcwd()+"/"
        self.folders = []
        
    def generateParams(self):
        return itertools.product(self.coverages,self.islandDiff,self.multiAtomDiff,self.matters, self.temperatures)


    def createFolder(self,params):
        os.chdir(self.workingPath)
        folderName = self.workingPath+'/'.join(str(e) for e in params)
        os.makedirs(folderName,exist_ok=True)
        os.chdir(folderName)
        self.folders.append(folderName)

    def getTypes(self,coverage):
        array = []
        if coverage < 100:
            outputTypes = list(["svg", "txt", "xyz"])
        else:
            outputTypes = list(["extra", "ae"])
        for i in outputTypes:
            v = {}
            v["type"] = i
            array.append(v)
        return array
    
    def doPsd(self,coverage):
        if coverage < 100:
            return True
        return False
    
    def writeParameters(self,params):
        f = open('parameters', 'w')
        coverage = params[0]
        f.write(json.dumps({"calculationMode": self.calculationMode,
                            "numberOfSimulations": self.numberOfSimulations,
                            "justCentralFlake": self.justCentralFlake,
                            "cartSizeX": self.cartSizeX,
                            "cartSizeY": self.cartSizeY,
                            "outputData": self.outputData,
                            "outputDataFormat": self.getTypes(coverage),
                            "psd": self.doPsd(coverage),
                            "randomSeed": self.randomSeed,
                            "depositionFlux": self.depositionFlux,
                            "forceNucleation": self.forceNucleation,
                            "automaticCollections": self.automaticCollections,
                            "withGui": self.withGui,
                            "coverage": params[0],
                            "doIslandDiffusion": params[1],
                            "doMultiAtomDiffusion": params[1],
                            "ratesLibrary": params[3],
                            "temperature": params[4],
                    }, sort_keys=True, indent=2)) 

    def writeSubmit(self):
        f = open('submit.sh', 'w')
        f.write(u"#!/bin/bash\n")
        f.write("cd "+os.getcwd()+"\n")
        f.write("java -Xss100m -jar /home/jalberdi004/local/mk/morphokinetics.jar > output 2> error\n")
        os.chmod("submit.sh", stat.S_IRUSR | stat.S_IXUSR | stat.S_IWUSR)

    def writeGreasy(self):
        os.chdir(self.workingPath)
        f = open("greasy_run.txt", "w")
        for i in self.folders:
            outputFile = Path(i+"/output")
            if not outputFile.is_file():
                f.write(i+"/submit.sh\n")

