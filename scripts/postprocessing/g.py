# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import results
import sys
import traceback


plt.title("$ R \propto d ( \\frac{s v_s}{r_g^2} )$")
label = r'$\frac{s v_s}{r_g^2}$'
plt.ylabel(label)
label = r'R (total rate)'
plt.xlabel(label)
plt.grid(True)
temperatures = list(range(125,321,5))

workingPath = os.getcwd()
for i in range(-6,5):
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
    fs = (meanValues.getSizes2()-meanValues.getSizes()**2)*(1/2)
    vsPrime = np.maximum(vs,(1+fs**(4/3))*flux**1.08)
    s = (0.3*100*100)/meanValues.getIslandsAmount()
    vg = meanValues.getGyradiusSlope()
    rtt = mk.getRtt(temperatures)
    d = mk.fractalDimensionFunc(rtt/flux)
    print(d)
    rg = meanValues.getLastGyradius()
    print(len(d),len(s), len(rg))
    y = d * ((s*vsPrime)/vg**2)
    x = meanValues.getTotalRatio()
    print(len(x),len(y))
    try:
        plt.loglog(x, y,  label=folder)
        plt.legend(loc='upper left', prop={'size':6})
        plt.savefig("g.png")
    except ValueError:
        plt.close()
        traceback.print_exc(file=sys.stdout)
        print("error plotting")
        print(x)
        print(y)
    
plt.close()


print("Good bye!")
          
