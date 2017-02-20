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
    d = f.fractalD(rtt/flux)
    rg = meanValues.getLastGyradius()
    print(len(d),len(s), len(rg))
    command = "d * ((s*vs)/rg**2)"
    y = eval(command)
    x = meanValues.getTotalRatio()
    print(len(x),len(y))
    try:
        plt.ylabel(command)
        plt.loglog(x, y,  label=folder)
        plt.legend(loc='upper left', prop={'size':6})
        plt.savefig("dSVsRgVsRate.png")
    except ValueError:
        plt.close()
        traceback.print_exc(file=sys.stdout)
        print("error plotting")
        print(x)
        print(y)
    
plt.close()


print("Good bye!")
          
