import functions as f
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
#plt.ylim(1e2,1e6)
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
    s = meanValues.getSizes()
    s2 = meanValues.getSizes2()
    sPrime = np.array(s2 - s**2)
    sPrime = np.array([i if i > 10 else 0 for i in sPrime ])
    print(s, s2, sPrime)
    vg = meanValues.getGyradiusSlope()
    rtt = mk.getRtt(temperatures)
    d = f.fractalD(rtt/flux)
    rg = meanValues.getLastGyradius()
    print(len(d),len(s), len(rg))
    y = (d * (sPrime**(1/2)/rg))**(7/3)
    x = meanValues.getTotalRatio()
    print(len(x),len(y))
    plt.loglog(x, y, label=folder)
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("dS2RgVsRate.png")
    
    
plt.close()


print("Good bye!")
          
