# Author: J. Alberdi-Rodriguez

import os
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
temperatures = list(range(120,321,5))
temperatures = np.array(list(range(120,156,5)))
#temperatures = np.array(list(range(165,200,5)))

workingPath = os.getcwd()
print(workingPath)
resultsAe = []
x = np.empty(0)
kb = 8.6173324e-5
energies = np.zeros(shape=(4,4), dtype=float)
energies[0] = 0.2
energies[1][1] = 0.45
energies[1][2] = 0.36
energies[1][3] = 0.35
energies[2][1:] = 0.435

for i in range(-3,-2):
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

    possibles = np.zeros(shape=(len(resultsAe[0]),len(temperatures)), dtype=float)
    percent = np.zeros(shape=(len(resultsAe[0]),len(temperatures)), dtype=float)
    minusEnergy = []
    transition = 0
    sumEnergy = 0.0
    for index in resultsAe[0]:
        temp = 0
        for values in resultsAe:
            possibles[transition][temp] = values[index][1] # read possibles from slot 1
            percent[transition][temp] = values[index][0] # read ratio from slot 0
            temp += 1
        popt = curve_fit(mk.expFunc, 1/kb/temperatures, possibles[transition], p0=[10e5, -0.01])
        a = popt[0][0]
        b = popt[0][1]
        minusEnergy.append(b)
        plt.semilogy(1/kb/temperatures, possibles[transition], "-x", label=index)
        plt.semilogy(1/kb/temperatures, mk.expFunc(1/kb/temperatures, a,b), label="fit {}e^{}".format(a,b))
        percentMean = np.mean(percent[transition])
        # index is the process, from x to y
        x,y = mkl.getXy(index, len(energies))
        if (verbose):
            print("/",index, mkl.getXy(index, len(energies)))
            print(percentMean,energies[x][y],minusEnergy[transition])
            print(percentMean*(energies[x][y]-minusEnergy[transition]))
        sumEnergy += percentMean*(energies[x][y]-minusEnergy[transition])
        transition += 1

    print("Energy from multiplicities is", sumEnergy)
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("aeStudy.png")

print("Good bye!")
          
