# This plot is the same as inverseIslandVsRtt (scaled)
#
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
from scipy.optimize import curve_fit
import results

label = r'vs/F^0.77'
plt.ylabel(label)
label = r'r_tt/F^0.33'
plt.xlabel(label)
plt.grid(True)

workingPath = os.getcwd()
temperatures = list(range(120,321,5))

for i in range(-6,1):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        meanValues = mk.getIslandDistribution(temperatures, sqrt=False, interval=False, growth=False)
    except OSError:
        print ("error changing to {}".format(folder))
        a = 0 #do nothing
    os.chdir(workingPath)

    islands = meanValues.getIslandsAmount()
    indexes = np.where(islands > 0)
    islands = islands[indexes]
    v = (0.7*flux*400*400/(islands))/(flux**0.77)
    t = np.array(temperatures)[indexes]
    n = (flux**0.23)/(islands)
    r = np.array(mk.getRtt(t))/(flux**0.33)
    plt.loglog(r, v, ".", label="N "+folder)
    if (i == -1):
        indexes = np.array(range(0,13))
        x = r[indexes]
        y = v[indexes]
        popt = curve_fit(mk.powerFunc, x, y)
        a = popt[0][0]
        b = popt[0][1]
        label = "{}x^{}".format(a, b)
        y = mk.powerFunc(x, a, b)
        plt.loglog(x, y, label=label)

    if (i == -1):
        indexes = np.array(range(len(islands)-15,len(islands)-2))
        x = r[indexes]
        y = v[indexes]
        popt = curve_fit(mk.powerFunc, x, y)
        a = popt[0][0]
        b = popt[0][1]
        label = "{}x^{}".format(a, b)
        y = mk.powerFunc(x, a, b)
        plt.loglog(x, y, label=label)
        
        
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("growthVsRtt.png")
    
plt.close()

print("Good bye!")
          
