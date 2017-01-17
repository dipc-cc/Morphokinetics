# Computes all possible measurements (averages them if needed) and
# prints them to CSV like files.
#
#Author: J. Alberdi-Rodriguez

import os
import sys
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import csv
from scipy.optimize import curve_fit
import results

label = r'total rate and gyradius'
plt.ylabel(label)
label = r'1/kbT'
plt.xlabel(label)
plt.grid(True)

workingPath = os.getcwd()
hex = len(sys.argv) > 1
if hex:
    temperatures = np.array(list(range(50,100,5))+list(range(100,150,10))+list(range(150,1100,50)))
    initFlux = 2
    endFlux = 5
else:
    temperatures = list(range(120,501,5))
    initFlux = -6
    endFlux = 5

kb = 8.6173324e-5
maxCoverage = 50

for i in range(initFlux,endFlux):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    sqrt = False
    interval = False
    growth = True
    verbose = False
    print(folder)
    try:
        os.chdir(folder)
        meanValues = mk.getIslandDistribution(temperatures, sqrt, interval, growth, verbose, flux, maxCoverage)
    except OSError:
        print ("error changing to {}".format(folder))
        a = 0 #do nothing
    os.chdir(workingPath)

    rtt = mk.getRtt(temperatures)
    growthSlope = meanValues.getGrowthSlope()
    totalRatio = meanValues.getTotalRatio()
    fractalD = mk.fractalDFunc(rtt/flux**0.5)
    shapeF = mk.shapeFactorFunc(rtt/flux**0.5)
    gyradius = meanValues.getGyradiusSlope()
    n = meanValues.getIslandsAmount()
    perimeterSlopes= meanValues.getInnerPerimeterSlope()
    numberOfMonomers= meanValues.getMonomersAmount()
    simulatedTimes= meanValues.getTimes()

    filename = "outputFile"+'{:E}'.format(flux)+".txt"
    with open(filename, 'w', newline='') as csvfile:
        outwriter = csv.writer(csvfile, delimiter=' ', quotechar='|', quoting=csv.QUOTE_MINIMAL)
        outwriter.writerow(["%index", "temp", "flux", "rtt[index]", "fractalD[index]", "shapeF[index]", "totalRatio[index]", "growthSlope[index]", "simulatedTimes[index]"])
        for index, temp in enumerate(temperatures):
            outwriter.writerow([index, temp, flux, rtt[index], fractalD[index], shapeF[index], totalRatio[index], growthSlope[index], simulatedTimes[index]])




print("Good bye!")
          
