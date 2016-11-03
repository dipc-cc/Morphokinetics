# Computes all possible measurements (averages them is needed) and
# prints them to CSV like files.
#
#Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import csv
from scipy.optimize import curve_fit

label = r'total rate and gyradius'
plt.ylabel(label)
label = r'1/kbT'
plt.xlabel(label)
plt.grid(True)

workingPath = os.getcwd()
results = []
temperatures = list(range(120,321,5))
kb = 8.6173324e-5

for i in range(-6,1):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    sqrt = False
    interval = False
    growth = True
    verbose = False
    print(folder)
    try:
        os.chdir(folder)
        results.append(mk.getIslandDistribution(temperatures, sqrt, interval, growth, verbose, flux))
    except OSError:
        print ("error changing to {}".format(folder))
        a = 0 #do nothing
    os.chdir(workingPath)
    while(len(temperatures) > len(results[-1][0])):
        results[-1][0].append(float('nan'))
        results[-1][1].append(float('nan'))
        results[-1][2].append(float('nan'))
        results[-1][3].append(float('nan'))
        results[-1][4].append(float('nan'))
        results[-1][5].append(float('nan'))
        results[-1][6].append(float('nan'))
        results[-1][7].append(float('nan'))

    rtt = mk.getRtt(temperatures)
    growthSlope = np.array(results[-1][0])
    totalRatio = np.array(results[-1][1])
    fractalD = mk.fractalDFunc(rtt/flux**0.5)
    shapeF = mk.shapeFactorFunc(rtt/flux**0.5)
    gyradius = np.array(results[-1][2])
    n = np.array(results[-1][3])
    perimeterSlopes= np.array(results[-1][4])
    numberOfMonomers= np.array(results[-1][5])
    aeRatioTimesPossibleList= np.array(results[-1][6])
    simulatedTimes= np.array(results[-1][7])

    filename = "outputFile"+'{:E}'.format(flux)+".txt"
    with open(filename, 'w', newline='') as csvfile:
        outwriter = csv.writer(csvfile, delimiter=' ', quotechar='|', quoting=csv.QUOTE_MINIMAL)
        outwriter.writerow(["%index", "temp", "flux", "rtt[index]", "fractalD[index]", "shapeF[index]", "totalRatio[index]", "growthSlope[index]", "simulatedTimes[index]"])
        for index, temp in enumerate(temperatures):
            outwriter.writerow([index, temp, flux, rtt[index], fractalD[index], shapeF[index], totalRatio[index], growthSlope[index], simulatedTimes[index]])




print("Good bye!")
          
