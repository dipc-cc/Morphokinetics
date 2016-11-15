# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import results


plt.title("$ R \propto d ( \\frac{s v_s}{r_g^2} )$")
label = r'$\frac{s v_s}{r_g^2}$'
plt.ylabel(label)
label = r'R (total rate)'
plt.xlabel(label)
plt.grid(True)
temperatures = list(range(120,321,5))

workingPath = os.getcwd()
results = results.Results()
for i in range(-6,1):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        results.append(mk.getIslandDistribution(temperatures, False, False))
    except OSError:
        print ("error changing to flux {}".format(folder))

    os.chdir(workingPath)
    vs = results.growthSlope()
    s = (0.3*400*400)/results.islands()
    vg = results.gyradius()
    rtt = mk.getRtt(temperatures)
    d = mk.fractalDFunc(rtt/flux)
    rg = results.lastGyradius()
    print(len(d),len(s), len(rg))
    y = d * ((s*vs)/rg**2)
    x = results.totalRatio()
    print(len(x),len(y))
    try:
        plt.loglog(x, y,  label=folder)
        plt.legend(loc='upper left', prop={'size':6})
        plt.savefig("dSVsRgVsRate.png")
    except ValueError:
        print("error plotting")
    
plt.close()


print("Good bye!")
          
