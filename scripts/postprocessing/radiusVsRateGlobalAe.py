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
temperatures = list(range(120,221,5))
Rtt = ([16909147819.3565 ,21827140898.7456 ,27627597919.6124 ,34364354949.7052 ,42082771314.0787 ,50819661607.7332 ,60603453969.7332 ,71454522157.9662 ,83385644639.2549 ,96402550521.0296 ,110504518908.249 ,125685004681.771 ,141932269487.491 ,159230001774.093 ,177557913993.664 ,196892308616.069 ,217206607470.615 ,238471841198.484 ,260657097361.911 ,283729927093.541 ,307656711157.28])

Rstep = ([85941.5666677768, 185709.051983031, 378203.30334162, 730700.474287996, 1346871.73342121, 2380119.6620485, 4049377.66119581, 6657170.49755918, 10609590.0115993, 16437717.6779536, 24819933.7510902, 36604495.7633867, 52831748.04825, 74755336.5257832, 103861844.747233, 141888332.294831, 190837338.622147, 252989007.881088, 330910087.306819, 427459648.223626, 545791470.564676])
kb = 8.6173324e-5
for i in range(-3,-2):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        results.append(mk.getIslandDistribution(False, False))
    except OSError:
        print ("error changing to {}".format(folder))
        a = 0 #do nothing
    os.chdir(workingPath)
    axes = plt.gca()
    lng3 = ax2.semilogy(1/(kb*np.array(temperatures)), np.array(results[-1][3]),  "-r", label="islands"+folder)
    lng1 = ax1.semilogy(1/(kb*np.array(temperatures)), 1e5*np.array(results[-1][0]),  "x-", label="growth "+folder)
    lng2 = ax1.semilogy(1/(kb*np.array(temperatures)), np.array(results[-1][1]),  "s-", label="ratio "+folder)
    lng = lng1 + lng2 + lng3
    labs = [l.get_label() for l in lng]
    plt.legend(lng, labs, loc='upper right', prop={'size':6})
    
    plt.savefig("radiusVsRate.png")
    
plt.close()


print("Good bye!")
          
