#
# Compares in a graph the average island size (or its square root)
# growth with the total rate.
#
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk


plt.title("Average island growth")
label = r'Average island radius growth rate $\sqrt{{ \dot{{r}} }} $'
plt.ylabel(label)
label = r'Time-averaged total rate $ < R >_t $'
plt.xlabel(label)
plt.grid(True)
temperatures = list(range(120,221,5))

workingPath = os.getcwd()
results = []
for i in range(-6,5):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        results.append(mk.getIslandDistribution(temperatures, False, False))
    except OSError:
        print ("error changing to {}".format(folder))
        a = 0 #do nothing
    os.chdir(workingPath)
    plt.loglog(np.array(results[-1][1]), np.array(results[-1][0]),  label=folder)
    plt.loglog(np.array(results[-1][1]), np.array(results[-1][2]), '--', label="gyradius "+str(flux))
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("radiusVsRate.png")
    
plt.close()


print("Good bye!")
          
