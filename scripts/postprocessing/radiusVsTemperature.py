# 
# Compares in a graph the average island size (or its square root)
# growth with the temperature.
#
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk

yPower = 0.3

fig1 = plt.figure(1)
plt.title("Average island growth")
plt.ylabel("Average radius growth rate/flux^{}".format(yPower))
plt.xlabel("1/$k_B $temperature")
plt.ylim(10,100)
plt.xlim(50,100)
plt.grid(True)

workingPath = os.getcwd()
results = []
temperatures=list(range(120, 221, 5))
for i in range(-6,5):
    fig1 = plt.figure(1)
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        results.append(mk.getIslandDistribution(temperatures, True, False))
    except OSError:
        print ("error changing to {}".format(folder))
        a = 0 #do nothing
    while(len(temperatures) > len(results[-1][0])):
        results[-1][0].append(0)
        results[-1][3].append(0)
        
    os.chdir(workingPath)
    plt.loglog(1/(np.array(temperatures)*8.62e-5), np.array(results[-1][0])/(flux**yPower), label=folder)
    plt.legend(loc='upper right', prop={'size':6})
    fig1.savefig("radiusVsTemperature.png")
    fig2 = plt.figure(2)
    plt.loglog(1/(np.array(temperatures)*8.62e-5), results[-1][3])
    fig2.savefig("fig2.png")
    
plt.close()

#plot number of islands per temperature
print("Good bye!")
          
