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

yPower=0.89
xPower=0.82

#plt.title("Average island growth")
label=r'Average island radius growth rate / flux $\frac{{ \sqrt{{ \dot{{r}} }} }}{{F^{{ {} }} }} }}$'.format(yPower)
plt.ylabel(label)
label=r'Time-averaged total rate $\frac{{ < R >_t }}{{ F ^{{ {} }} }}$'.format(xPower)
plt.xlabel(label)
plt.legend(loc='upper left',prop={'size':6})
plt.grid(True)

workingPath = os.getcwd()
results=[]
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
    plt.loglog(np.array(results[-1][1])/flux**xPower, np.array(results[-1][0])/flux**yPower, label=folder)
    plt.legend(loc='upper left',prop={'size':6})
    plt.savefig("radiusVsModifiedRate.png")
    
plt.close()


print("Good bye!")
          
