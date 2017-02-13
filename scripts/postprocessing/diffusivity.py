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
    popt = curve_fit(mk.expFunc, x1, y1, p0=[1e10,-0.10])
    a = popt[0][0]
    b = popt[0][1]
    return list([a,b])


def hexagonal():
    temperatures = np.array(list(range(70,100,5))+list(range(100,150,10))+list(range(150,1100,50)))
    initFlux = 2
    endFlux = 7
    folderBase = "5e"
    return temperatures, initFlux, endFlux, folderBase

def twoEnergies():
    temperatures = np.array(list(range(120,300,10))+list(range(300,501,50)))
    initFlux = 0
    endFlux = 1
    folderBase = "3.5e"
    return temperatures, initFlux, endFlux, folderBase

def basic():
    temperatures = np.array(list(range(120,326,5)))
    initFlux = -3
    endFlux = 1
    folderBase = "3.5e"
    return temperatures, initFlux, endFlux, folderBase

def graphene():
    temperatures = np.array([200, 225, 250, 275, 300, 350, 400, 500, 600, 800, 1000, 1200, 1500])
    initFlux = -5
    endFlux = -4
    folderBase = "3.5e"
    return temperatures, initFlux, endFlux, folderBase

def gaillard():
    temperatures = np.array([200, 225, 250, 275, 300, 350, 400, 500, 600, 800, 1000, 1200, 1500])
    initFlux = -4
    endFlux = -3
    folderBase = "5e"
    return temperatures, initFlux, endFlux, folderBase
    

defaultValues = {'h': hexagonal,
                 '2': twoEnergies,
                 'b': basic,
                 'g': graphene,
                 'l': gaillard,}
    
simulationType = sys.argv[1]
coverage = int(sys.argv[2])
temperatures, initFlux, endFlux, folderBase = defaultValues[simulationType]()

hex = False
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
    j = coverage
    folder = "flux"+folderBase+str(i)
    flux = float(folderBase+str(i))
    print(folder)
    try:
        os.chdir(folder)
        meanValues = mk.getIslandDistribution(temperatures, sqrt=False, interval=False, growth=False, verbose = False, flux=-1, maxCoverage=j)
    except OSError:
        print ("error changing to flux {}".format(folder))

    os.chdir(workingPath)
    ne = meanValues.getNumberOfEvents()
    hp = meanValues.getNumberOfHops()
    time = meanValues.getTimes()
    vd = meanValues.getDiffusivitySlope()
    d = meanValues.getDiffusivity()
    m = meanValues.getDiffusivityLarge()
    rg = meanValues.getLastGyradius()
    vg = meanValues.getGyradiusSlope()
    n = meanValues.getIslandsAmount()
    T1 = 1/(kb * temperatures)
    print(d)
    command = "1/vd*1e12"
    command = "d"
    y = eval(command)
    plt.ylabel(command)
    command = "1/kb/temperatures"#+np.log(flux**2.5)"
    x = eval(command)
    plt.xlabel(command)
    try:
        plt.semilogy(x, y, "3-", label=folder+" "+str(j))
        plt.semilogy(x, ne, ".", label=folder+" ne")
        plt.semilogy(x, hp, "1", label=folder+" hops")

        if hex:
            a, b = fit(x, y, 0, 12)
            plt.semilogy(x, mk.expFunc(x, a, b), label="fit low "+str(b))
            a, b = fit(x, y, 12, 17)
            plt.semilogy(x, mk.expFunc(x, a, b), label="fit middle "+str(b))
            a, b = fit(x, y, 15, 22)
            plt.semilogy(x, mk.expFunc(x, a, b), label="fit middle "+str(b))
            plt.ylim(1e9,1e14)
        if hex:
            a, b = fit(x, y, 0, 8)
            plt.semilogy(x, mk.expFunc(x, a, b), label="fit low "+str(b))
            a, b = fit(x, y, 8, 16)
            plt.semilogy(x, mk.expFunc(x, a, b), label="fit middle "+str(b))
            a, b = fit(x, y, 17, 27)
            plt.semilogy(x, mk.expFunc(x, a, b), label="fit high "+str(b))
            #plt.ylim(1e5,1e8)
        plt.legend(loc='lower left', prop={'size':6})
        plt.savefig("diffusivity.png")
    except ValueError:
        plt.close()
        traceback.print_exc(file=sys.stdout)
        print("error plotting")
        print(x)
        print(y)
    
plt.close()


print("Good bye!")
          
