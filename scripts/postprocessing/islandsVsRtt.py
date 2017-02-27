import functions as f
import info as i
import glob
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
from scipy.optimize import curve_fit
import results

label = r'Number of islands (N)/F^0.23'
plt.ylabel(label)
plt.figure(num=None, figsize=(5,5), dpi=80, facecolor='w', edgecolor='k')
label = r'r_tt/F^0.33'
plt.xlabel(label)
plt.grid(True)

workingPath = os.getcwd()

for f in i.getFluxes():
    try:
        p = i.getInputParameters(glob.glob(f+"/*/output*")[0])
        os.chdir(f)
        temperatures = i.getTemperatures()
        meanValues = mk.getIslandDistribution(temperatures, False, False, False)
    except (OSError, IndexError):
        print ("error changing to {}".format(f))
        continue
    os.chdir(workingPath)

    v = 0.82*400*400/meanValues.getIslandsAmount()*(p.flux**0.21)
    n = meanValues.getIslandsAmount()/(p.flux**0.23)
    r = np.array(mk.getRtt(temperatures))/(p.flux**0.33)
    plt.loglog(r, n, "-", label="N "+f)
    if (i == 0):
        x = r
        c = []
        exponent = []
        for i in temperatures:
            if (i > 250):
                c.append(220)
                exponent.append(-0.666)
            else:
                c.append(0.25)
                exponent.append(-0.333)
        y = c * r ** exponent
        plt.loglog(x, y, "*", label="fit")
    if (i == -1000):
        print(r)
        print(n)
        popt = curve_fit(f.power, r, n)
        a = popt[0][0]
        b = popt[0][1]
        label = "{}x^{}".format(a, b)
        x = r
        y = f.power(x, a, b)
        plt.loglog(x, y, label=label)

    if (i == -2000):
        a = 2e7
        b = -(5/7)
        x = r
        y = f.power(x, a, b)
        label = "{}x^{}".format(a, b)
        plt.loglog(x, y, label=label)
        a = 4e7
        b = -(2/3)
        y = f.power(x, a, b)
        label = "{}x^{}".format(a, b)
        plt.loglog(x, y, label=label)
        a = 1e7
        b = -0.5
        y = f.power(x, a, b)
        label = "{}x^{}".format(a, b)
        plt.loglog(x, y, label=label)
        
        
    plt.legend(loc='lower left', prop={'size':6})
    plt.savefig("islandsVsRtt.png")
    
plt.close()

print("Good bye!")
          
