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

def radiusVsRate(folder="."):
    
    temperatures = list(range(120,221,5))
    plt.title("Average island growth")
    label = r'Average island radius growth rate  $ \sqrt{{ \dot{{r}} }}$'
    plt.ylabel(label)
    label = r'Time-averaged total rate $< R >_t }}$'
    plt.xlabel(label)
    plt.grid(True)

    currentResults = results.Results()
    sqrt = True
    interval = True

    print(folder)
    try:
        os.chdir(folder)
        oneResult = mk.getIslandDistribution(temperatures, sqrt, interval)
        currentResults.append(oneResult)
    except OSError:
        print ("error changing to {}".format(folder))
        a = 0 #do nothing
    plt.loglog(currentResults.totalRatio(), currentResults.growthSlope(),  label="growth"+folder)
    plt.loglog(currentResults.totalRatio(), currentResults.gyradius(), label="gyradius")
    plt.loglog(currentResults.totalRatio(), 1000/currentResults.perimeter(), label="perimeter")
    plt.legend(loc='upper left', prop={'size':6})


workingPath = os.getcwd()
flux = 3.5e-3
radiusVsRate()
plt.savefig("singleFluxRadiusVsRate.png")

print("Good bye!")
          
