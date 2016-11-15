# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import results


plt.title("Average island growth")
label = r'Average island radius growth rate / $(v_g / F^{0.11})$'
plt.ylabel(label)
label = r'Time-averaged total rate $ < R >_t/F^{0.79} $'
plt.xlabel(label)
plt.grid(True)
temperatures = list(range(120,321,5))

workingPath = os.getcwd()
results = results.Results()
for i in range(-6,1):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        results.append(mk.getIslandDistribution(temperatures, False, False))
    except OSError:
        print ("error changing to {}".format(folder))
    os.chdir(workingPath)
    vs = results.growthSlope()
    vg = results.gyradius()
    y = vs / (vg/ flux**0.11)
    x = results.totalRatio()  / flux**0.79
    plt.loglog(x, y,  label=folder)
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("vsVgVsRate.png")
    
plt.close()


print("Good bye!")
          
