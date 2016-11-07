# 
# Compares in a graph the average island size (or its square root)
# growth with the total rate. It tries to overlap many flux in a
# single line.
#
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import results

yPower = 0.89
xPower = 0.82

#plt.title("Average island growth")
label = r'Average island radius growth rate / flux $\frac{{ \sqrt{{ \dot{{r}} }} }}{{F^{{ {} }} }} }}$'.format(yPower)
plt.ylabel(label)
label = r'Time-averaged total rate $\frac{{ < R >_t }}{{ F ^{{ {} }} }}$'.format(xPower)
plt.xlabel(label)
plt.grid(True)

sqrt = True
workingPath = os.getcwd()
temperatures = list(range(120,221,5))
results = results.Results()
for i in range(-6,5):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        results.append(mk.getIslandDistribution(temperatures, sqrt))
    except OSError:
        print ("error changing to {}".format(folder))
        a = 0 #do nothing
    os.chdir(workingPath)
    plt.loglog(results.totalRatio()/flux**xPower, results.growthSlope()/flux**yPower, label=folder)
    plt.loglog(results.totalRatio()/flux**xPower, results.gyradius()/flux**yPower, '--', label="gyradius "+str(flux))
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("radiusVsModifiedRate.png")
    
plt.close()


print("Good bye!")
          
