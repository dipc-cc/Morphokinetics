# 
# Compares in a graph the average island size growth and total rate
# with the temperature.
#
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import results

plt.title("Average island growth")
plt.ylabel("Average area growth rate")
plt.xlabel("1/$k_B $temperature")
plt.ylim(1e3,1e8)
plt.xlim(50,100)
plt.grid(True)

workingPath = os.getcwd()
results = results.Results()
temperatures = list(range(120, 221, 5))
for i in range(-6,1):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        results.append(mk.getIslandDistribution(temperatures, False))
    except OSError:
        print ("error changing to {}".format(folder))
        a = 0 #do nothing
    os.chdir(workingPath)
    plt.loglog(1/(np.array(temperatures)*8.62e-5), 4e4*results.growthSlope(), label="growth "+str(i))
    plt.loglog(1/(np.array(temperatures)*8.62e-5), results.totalRatio(), "--", label="t. ratio "+str(i))
    plt.legend(loc='lower left', prop={'size':6})
    plt.savefig("rateRadiusVsTemperature.png")
    
plt.close()


print("Good bye!")
          
