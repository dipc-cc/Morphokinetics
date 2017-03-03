import functions as fun
import info as i
import glob
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

workingPath = os.getcwd()

for f in i.getFluxes():
    folder = "flux3.5e"+str(i)
    print(folder)
    try:
        p = i.getInputParameters(glob.glob(f+"/*/output*")[0])
        os.chdir(f)
        temperatures = i.getTemperatures()
        meanValues = mk.getIslandDistribution(temperatures, False, False)
    except (OSError, IndexError):
        print ("error changing to flux {}".format(f))
        continue
    os.chdir(workingPath)
    vs = meanValues.getGrowthSlope()
    s = (0.3*400*400)/meanValues.getIslandsAmount()
    vg = meanValues.getGyradiusSlope()
    rtt = mk.getRtt(temperatures)
    d = fun.fractalD(rtt/p.flux)
    rg = meanValues.getLastGyradius()
    print(len(d),len(s), len(rg))
    command = "d * ((s*vs)/rg**2)"
    y = eval(command)
    x = meanValues.getTotalRatio()
    print(len(x),len(y))
    try:
        plt.ylabel(command)
        plt.loglog(x, y,  label=f)
        plt.loglog(x, x/6e10,  label="total ratio {}".format(f))
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
          
