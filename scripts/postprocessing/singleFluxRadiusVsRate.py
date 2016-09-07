#
# Compares in a graph the average island size (or its square root)
# growth with the total rate.
#
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk

def radiusVsRate(flux, folder="."):
    yPower=2/3
    xPower=0
    
    plt.title("Average island growth")
    label=r'Average island radius growth rate / flux $\frac{{ \sqrt{{ \dot{{r}} }} }}{{F^{{ {} }} }} }}$'.format(yPower)
    plt.ylabel(label)
    label=r'Time-averaged total rate $\frac{{ < R >_t }}{{ F ^{{ {} }} }}$'.format(xPower)
    plt.xlabel(label)
    plt.legend(loc='upper left',prop={'size':6})
    plt.grid(True)

    results=[]

    print(folder)
    try:
        os.chdir(folder)
        oneResult=mk.getIslandDistribution()
        results.append(oneResult)
    except OSError:
        print ("error changing to {}".format(folder))
        a=0 #do nothing
    plt.loglog(np.array(results[-1][1])/flux**xPower, np.array(results[-1][0])/(flux**yPower),  label=folder)
    plt.legend(loc='upper left',prop={'size':6})
    plt.loglog(np.array(results[-1][1])/flux**xPower, np.array(results[-1][2])/flux**yPower, label="gyradius")


if __name__ == '__main__':
    print("Why am I here?")
workingPath = os.getcwd()
flux=3.5e-3
radiusVsRate(flux)
plt.savefig("singleFluxRadiusVsRate.png")

print("Good bye!")
          
