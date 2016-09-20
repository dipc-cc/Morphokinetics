#
# Compares in a graph the average island size (or its square root)
# growth with the total rate.
#
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk

def radiusVsRate(folder="."):
    
    plt.title("Average island growth")
    label=r'Average island radius growth rate  $ \sqrt{{ \dot{{r}} }}$'
    plt.ylabel(label)
    label=r'Time-averaged total rate $< R >_t }}$'
    plt.xlabel(label)
    plt.legend(loc='upper left',prop={'size':6})
    plt.grid(True)

    results=[]
    sqrt = True

    print(folder)
    try:
        os.chdir(folder)
        oneResult=mk.getIslandDistribution(sqrt)
        results.append(oneResult)
    except OSError:
        print ("error changing to {}".format(folder))
        a=0 #do nothing
    print(results)
    plt.loglog(np.array(results[-1][1]), np.array(results[-1][0]),  label="growth"+folder)
    plt.loglog(np.array(results[-1][1]), np.array(results[-1][2]), label="gyradius")
    plt.legend(loc='upper left',prop={'size':6})


if __name__ == '__main__':
    print("Why am I here?")
workingPath = os.getcwd()
flux=3.5e-3
radiusVsRate(flux)
plt.savefig("singleFluxRadiusVsRate.png")

print("Good bye!")
          
