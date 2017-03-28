import functions as f
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import results
import sys
import traceback
import info as inf
from scipy.optimize import curve_fit
import glob

coverage = int(sys.argv[1])

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
j = coverage
for f in inf.getFluxes():
    print(f)
    try:
        p = inf.getInputParameters(glob.glob(f+"/*/output*")[0])
        os.chdir(f)
        temperatures = inf.getTemperatures()
        meanValues = mk.getIslandDistribution(temperatures, sqrt=False, interval=False, growth=False, verbose=False, flux=-1, maxCoverage=j)
    except (OSError,IndexError):
        print ("error changing to flux {}".format(f))
        continue

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
    command = "d/p.flux**-0.3"
    y = eval(command)
    plt.ylabel(command)
    command = "1/kb/temperatures+np.log(p.flux**1.5)"
    x = eval(command)
    plt.xlabel(command)
    try:
        plt.semilogy(x, ne/p.flux**-0.3, ".", label=f+" ne")
        plt.semilogy(x, hp/p.flux**-0.3, "h", label=f+" hops")
        plt.semilogy(x, y, "3-", label=f+" $R^2$ "+str(j))

        if hex:
            a, b = f.fit(x, y, 0, 12)
            plt.semilogy(x, f.exp(x, a, b), label="fit low "+str(b))
            a, b = f.fit(x, y, 12, 17)
            plt.semilogy(x, f.exp(x, a, b), label="fit middle "+str(b))
            a, b = f.fit(x, y, 15, 22)
            plt.semilogy(x, f.exp(x, a, b), label="fit middle "+str(b))
            plt.ylim(1e9,1e14)
        if hex:
            a, b = f.fit(x, y, 0, 8)
            plt.semilogy(x, f.exp(x, a, b), label="fit low "+str(b))
            a, b = f.fit(x, y, 8, 16)
            plt.semilogy(x, f.exp(x, a, b), label="fit middle "+str(b))
            a, b = f.fit(x, y, 17, 27)
            plt.semilogy(x, f.exp(x, a, b), label="fit high "+str(b))
            #plt.ylim(1e5,1e8)
        plt.legend(loc='lower left', prop={'size':6})
        plt.savefig("diffusivity.png")
    except ValueError:
        plt.close()
        traceback.print_exc(file=sys.stdout)
        print("error plotting")
        print(x)
        print(y)

print("Good bye!")
          
