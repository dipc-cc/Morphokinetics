# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import results


plt.title("$ R \propto d ( \\frac{s}{r_g} )^2$")
label = r'$(\frac{s}{r_g} )^2$'
plt.ylabel(label)
label = r'R (total rate)'
plt.xlabel(label)
plt.grid(True)
temperatures = list(range(120,321,5))

workingPath = os.getcwd()
for i in range(-6,1):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        meanValues = mk.getIslandDistribution(temperatures, False, False)
    except OSError:
        print ("error changing to flux {}".format(folder))

    os.chdir(workingPath)
    vs = meanValues.getGrowthSlope()
    s = (0.3*400*400)/meanValues.getIslandsAmount()
    vg = meanValues.getGyradiusSlope()
    rtt = mk.getRtt(temperatures)
    d = mk.fractalDFunc(rtt/flux)
    rg = meanValues.getLastGyradius()
    print(len(d),len(s), len(rg))
    y = d * (s/rg)**2
    x = meanValues.getTotalRatio()
    print(len(x),len(y))
    try:
        plt.loglog(x, y,  label=folder)
        plt.legend(loc='upper left', prop={'size':6})
        plt.savefig("dSRgVsRate.png")
    except ValueError:
        print("error plotting")
    
plt.close()


print("Good bye!")
          
