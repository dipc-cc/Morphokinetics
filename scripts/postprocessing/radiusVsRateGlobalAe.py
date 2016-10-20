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
fig, ax1 = plt.subplots()
ax2 = ax1.twinx()
ax1.grid(True)

workingPath = os.getcwd()
results = []
temperatures = list(range(120,321,5))

kb = 8.6173324e-5
for i in range(-3,-2):
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
    while(len(temperatures) > len(results[-1][0])):
        results[-1][0].append(np.nan)
        results[-1][1].append(np.nan)
        results[-1][2].append(np.nan)
        results[-1][3].append(np.nan)
        
    axes = plt.gca()
    v = 0.7*flux*400*400/(np.array(results[-1][3]))#+np.array(results[-1][5]))
    v0 = np.array(results[-1][0])
    lng3 = ax1.semilogy(1/(kb*np.array(temperatures)), v,  "-r", label="islands "+folder)
    lng1 = ax1.semilogy(1/(kb*np.array(temperatures)), v0,  "x-", label="growth "+folder)
    lng2 = ax1.semilogy(1/(kb*np.array(temperatures)), np.array(results[-1][1]),  "s-", label="ratio "+folder)
    lng4 = ax1.semilogy(1/(kb*np.array(temperatures)), 1e2*np.array(results[-1][2]), '--', label="gyradius "+folder)
    lng5 = ax2.plot(1/(kb*np.array(temperatures)), np.array(v0/v), 'g-', label="islands*growth "+folder)
    lng = lng1 + lng2 + lng3 + lng4 + lng5
    labs = [l.get_label() for l in lng]
    plt.legend(lng, labs, loc='upper right', prop={'size':6})
    
    plt.savefig("radiusVsRateGlobalAe.png")
    
plt.close()


print("Good bye!")
          
