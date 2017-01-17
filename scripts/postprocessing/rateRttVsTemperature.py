# 
# Compares in a graph the average island size growth and r_tt
# with the temperature.
#
# Author: J. Alberdi-Rodriguez

import os
import sys
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import results

plt.title("Average island growth")
plt.ylabel("Average area growth rate")
plt.xlabel("1/$k_B $temperaturessssssss")
plt.xlim(50,100)
plt.grid(True)

workingPath = os.getcwd()
hex = len(sys.argv) > 1
if hex:
    temperatures = np.array(list(range(50,100,5))+list(range(100,150,10))+list(range(150,1100,50)))
    initFlux = 3
    endFlux = 5
else:
    temperatures = list(range(120,501,5))
    initFlux = -6
    endFlux = 0
    
for i in range(initFlux,endFlux):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        meanValues = mk.getIslandDistribution(temperatures, False)
    except OSError:
        print ("error changing to {}".format(folder))
        a = 0 #do nothing
    os.chdir(workingPath)

    plt.title("Average island growth")
    plt.loglog(1/(np.array(temperatures)*8.62e-5), 4e4*meanValues.getGrowthSlope(), label="4e4*area "+folder)
    plt.loglog(1/(np.array(temperatures)*8.62e-5), np.array(mk.getRtt(temperatures)), "--", label="rate "+folder)
    plt.legend(loc='lower left', prop={'size':6})
    plt.savefig("rateRttVsTemperature.png")
    
plt.close()


print("Good bye!")
