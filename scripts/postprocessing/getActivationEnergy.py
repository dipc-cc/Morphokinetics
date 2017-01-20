# Author: J. Alberdi-Rodriguez

import os
import sys
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import morphokineticsLow as mkl
import scipy
import csv
from scipy.optimize import curve_fit
    
label = r'Activation energy'
plt.ylabel(label)
label = r'$\frac{1}{K_bT}$'
plt.xlabel(label)
plt.grid(True)
axes = plt.gca()
hex = len(sys.argv) > 1
if hex:
    temperatures = np.array(list(range(90,100,5))+list(range(100,151,10)))
    #temperatures = np.array(list(range(200,401,50)))
    #temperatures = np.array(list(range(500,801,50)))
    initFlux = 4
    endFlux = 5
    energies = np.zeros(shape=(7,7), dtype=float)
    energies[0][0:4] = 0.10
    energies[1][1:5] = 0.25
    energies[2][1:6] = 0.33
    energies[3][3:6] = 0.42
else:
    temperatures = list(range(120,321,5))
    temperatures = np.array(list(range(120,156,5)))
    #temperatures = np.array(list(range(165,200,5)))
    #temperatures = np.array(list(range(205,300,5)))
    energies = np.zeros(shape=(4,4), dtype=float)
    energies[0] = 0.2
    energies[1][1] = 0.45
    energies[1][2] = 0.36
    energies[1][3] = 0.35
    energies[2][1:] = 0.435
    initFlux = -3
    endFlux = -2
                                   
workingPath = os.getcwd()
print(workingPath)
resultsAe = []
x = np.empty(0)
kb = 8.6173324e-5

for i in range(initFlux,endFlux):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    verbose = True
    try:
        os.chdir(folder)
        resultsAe = mk.getAllAeStudy(temperatures, verbose)
        if (verbose):
            print(resultsAe)
    except OSError:
        print ("ERROR changing to {}".format(folder))
        continue
    os.chdir(workingPath)

    #possibles = np.zeros(shape=(len(resultsAe[0]),len(temperatures)), dtype=float)
    #percent = np.zeros(shape=(len(resultsAe[0]),len(temperatures)), dtype=float)
    minusEnergy = []
    sumEnergy = 0.0
    # store all possible transitions
    allTransitions = set()
    for values in resultsAe:
        for value in values:
            allTransitions.add(value)

    for index in allTransitions: # for example 0,1,2,5,6,9
        possibles = []
        percent   = []
        temp      = []
        for values in resultsAe:
            try:
                possibles.append(values[index][1]) # read possibles from slot 1
                percent.append(values[index][0]) # read ratio from slot 0
                temp.append(values[index][2]) # read temperature from slot 2
            except KeyError:
                pass
        kbT = 1/kb/np.array(temp)
        plt.semilogy(kbT, possibles, "-x", label=index)

        # try to fit
        if len(possibles) > 1:
            try:
                popt = curve_fit(mk.expFunc, kbT, possibles, p0=[10e5, -0.01])
                a = popt[0][0]
                b = popt[0][1]
                minusEnergy = b
                plt.semilogy(kbT, mk.expFunc(kbT, a,b), label="fit {0:.4g}e^{1:.4f}".format(a,b))
                percentMean = np.mean(percent)
                # index is the process, from x to y
                x,y = mkl.getXy(index, len(energies))
                if (verbose):
                    print("/",index, mkl.getXy(index, len(energies)))
                    print(percentMean,energies[x][y],minusEnergy)
                    print(percentMean*(energies[x][y]-minusEnergy))
                sumEnergy += percentMean*(energies[x][y]-minusEnergy)
            except RuntimeError:
                pass

    print("Energy from multiplicities is", sumEnergy)
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("aeStudy.png")

print("Good bye!")
          
