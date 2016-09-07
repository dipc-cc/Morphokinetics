# Calculates the island distribution with the given chunk size and
# coverage from the input file
# "dataEvery1percentAndNucleation.txt". The output is "occurrences"
# and the plot "islandDistribution.png" It also calculates the average
# island size (regardless of the chunk size) to the same files.
#
# Additionally, also computes the average simulated time.
#
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk

    
xPower=0.431
yPower=0.75

plt.title("Average island growth")
plt.ylabel("Average radius growth rate/flux^{}".format(yPower))
plt.xlabel("1/$k_B $temperature")
plt.xlim(50,100)
plt.grid(True)

workingPath = os.getcwd()
results=[]
temperatures=list(range(120, 221, 5))
for i in range(-6,5):
    folder="flux3.5e"+str(i)
    flux=float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        results.append(mk.getIslandDistribution())
    except OSError:
        print ("error changing to {}".format(folder))
        a=0 #do nothing
    os.chdir(workingPath)
    plt.loglog(1/(np.array(temperatures)*8.62e-5), np.array(results[-1][0])/(flux**yPower), label=folder)
    plt.legend(loc='upper right',prop={'size':6})
    plt.savefig("radiusVsTemperature.png")
    
plt.close()


print("Good bye!")
          
