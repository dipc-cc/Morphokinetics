#!/usr/bin/env python3

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

import os
import glob
import sys
import info as inf
import shutil as sh

import pdb

temperatures = inf.getTemperatures() 
cov = 10
if len(sys.argv) > 1:
    cov = int(sys.argv[1])

surfaces = ' '.join("s"+str(cov)+str(t)+".svg" for t in temperatures)
workingPath = os.getcwd()
for i,t in enumerate(temperatures):
        print(t)
        ext = str(cov)+str(t)
        os.chdir(workingPath)
        try:
            os.chdir(str(t)+"/results")
            runFolder = glob.glob("*/");
            runFolder.sort()
            os.chdir(runFolder[-1])
        except FileNotFoundError:
            continue
    
        os.system("grep -v white surface10"+str(cov)+".svg > s"+ext+".svg")
        os.system("sed -i -e 's/blueviolet/black/g' s"+ext+".svg")
        os.system("sed -i -e 's/indianred/black/g' s"+ext+".svg")
        os.system("sed -i -e 's/gray/black/g' s"+ext+".svg")
        os.system("sed -i -e 's/cornflowerblue/black/g' s"+ext+".svg")
        os.system("sed -i -e 's/darkblue/black/g' s"+ext+".svg")
        os.system("sed -i -e 's/gold/black/g' s"+ext+".svg")
        sh.copy("s"+ext+".svg",workingPath)

os.chdir(workingPath)
#os.system("montage $(ls *svg | sort -n) allSurfaces.png")
os.system("montage -tile x1 "+surfaces+" allSurfaces"+str(cov)+".png")
os.system("convert allSurfaces"+str(cov)+".png -pointsize 10 -gravity south -annotate 0 "+workingPath+" -resize %70 labelSurfaces.png")
