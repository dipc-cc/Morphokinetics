#
# Compares in a graph the average island size (or its square root)
# growth with the total rate.
#
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import results


plt.title("Average island growth")
label = r'Average island radius growth rate $\sqrt{{ \dot{{r}} }} $'
plt.ylabel(label)
label = r'Time-averaged total rate $ < R >_t $'
plt.xlabel(label)
plt.grid(True)
temperatures = list(range(120,321,5))

workingPath = os.getcwd()
for i in range(-6,5):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        meanValues = mk.getIslandDistribution(temperatures, False, False)
    except OSError:
        print ("error changing to {}".format(folder))
    os.chdir(workingPath)
    plt.loglog(meanValues.getTotalRatio(), meanValues.getGrowthSlope(),  label=folder)
    plt.loglog(meanValues.getTotalRatio(), meanValues.getGyradiusSlope(), '--', label="gyradius "+str(flux))
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("radiusVsRate.png")
    
plt.close()


print("Good bye!")
          
