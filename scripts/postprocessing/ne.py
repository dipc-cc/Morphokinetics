import functions as f
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import results
import sys
import traceback
from scipy.optimize import curve_fit

def inprimatu(vector):
    for i in vector:
        print(i, end=" ")
    print()

def fit(x, y, initI, finishI):
    indexes = np.array(range(initI,finishI))
    x1 = x[indexes]
    y1 = y[indexes]
    popt = curve_fit(f.exp, x1, y1, p0=[1e10,-0.10])
    a = popt[0][0]
    b = popt[0][1]
    return list([a,b])

hex = len(sys.argv) > 1
if hex:
    temperatures = np.array(list(range(70,100,5))+list(range(100,150,10))+list(range(150,1100,50)))
    initFlux = 2
    endFlux = 5
else:
    temperatures = np.array(list(range(130,351,5)))
    temperatures = np.array(list(range(120,254,5)))
    initFlux = -3
    endFlux = -2


label = r''
plt.ylabel(label)
label = r'$R/F^{0.79}$'
plt.xlabel(label)
plt.figure(num=None, figsize=(6,6), dpi=80, facecolor='w', edgecolor='k')
plt.grid(True)
plt.title("Global activation energy")

workingPath = os.getcwd()
kb = 8.6173324e-5
for i in range(initFlux,endFlux):
#for j in [30]:#,20,15,10,5,3,2,1]:
    j = 30
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        meanValues = mk.getIslandDistribution(temperatures, sqrt=False, interval=False, growth=True, verbose = False, flux=-1, maxCoverage=j)
    except OSError:
        print ("error changing to flux {}".format(folder))

    os.chdir(workingPath)
    ne = meanValues.getNumberOfEvents()
    time = meanValues.getTimes()
    T1 = 1/(kb * temperatures)
    command = "ne/time"
    y = eval(command)
    plt.ylabel(command)
    command = "1/kb/temperatures"#+np.log(flux**2.5)"
    x = eval(command)
    plt.xlabel(command)
    try:
        plt.semilogy(x, y, ".", label=folder+" "+str(j))

        if hex:
            a, b = fit(x, y, 0, 12)
            plt.semilogy(x, f.exp(x, a, b), label="fit low "+str(b))
            a, b = fit(x, y, 12, 17)
            plt.semilogy(x, f.exp(x, a, b), label="fit middle "+str(b))
            a, b = fit(x, y, 17, 30)
            plt.semilogy(x, f.exp(x, a, b), label="fit middle "+str(b))
            plt.ylim(1e9,1e14)
        else:
            a, b = fit(x, y, 0, 8)
            plt.semilogy(x, f.exp(x, a, b), label="fit low "+str(b))
            a, b = fit(x, y, 8, 16)
            plt.semilogy(x, f.exp(x, a, b), label="fit middle "+str(b))
            a, b = fit(x, y, 17, 27)
            plt.semilogy(x, f.exp(x, a, b), label="fit high "+str(b))
        plt.legend(loc='lower left', prop={'size':6})
        plt.savefig("ne.png")
    except ValueError:
        plt.close()
        traceback.print_exc(file=sys.stdout)
        print("error plotting")
        print(x)
        print(y)
    
plt.close()


print("Good bye!")
          
