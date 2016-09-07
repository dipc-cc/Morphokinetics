# 
# Compares in a graph the average island size growth and r_tt
# with the temperature.
#
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk


plt.title("Average island growth")
plt.ylabel("Average area growth rate")
plt.xlabel("1/$k_B $temperaturessssssss")
plt.xlim(50,100)
plt.grid(True)

workingPath = os.getcwd()
results=[]
temperatures=list(range(120, 221, 5))
for i in range(-6,0):
    folder="flux3.5e"+str(i)
    flux=float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        results.append(mk.getIslandDistribution(False))
    except OSError:
        print ("error changing to {}".format(folder))
        a=0 #do nothing
    os.chdir(workingPath)
    plt.title("Average island growth")
    plt.loglog(1/(np.array(temperatures)*8.62e-5), 4e4*np.array(results[-1][0]), label="4e4*area "+folder)
    plt.loglog(1/(np.array(temperatures)*8.62e-5), np.array(mk.getAllRtt()), "--", label="rate "+folder)
    plt.legend(loc='lower left',prop={'size':6})
    plt.savefig("rateRttVsTemperature.png")
    
plt.close()


print("Good bye!")
