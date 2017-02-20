import functions as f
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import results
import sys
import traceback

def inprimatu(vector):
    for i in vector:
        print(i, end=" ")
    print()

plt.title("$ \\frac{s(f_s F^{0.215})^{4/3}}{r_g}$")
label = r''
plt.ylabel(label)
label = r'$R/F^{0.79}$'
plt.xlabel(label)
plt.ylim(1e2,1e6)
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
        print ("error changing to flux {}".format(folder))

    os.chdir(workingPath)
    vs = results.growthSlope()
    fs = results.fluctuationSizes()
    vsPrime = np.maximum(vs,(1+fs**(4/3))*flux**1.08)
    N = (0.3*400*400)/results.islands()
    s = results.sizes()
    inprimatu(fs)
    inprimatu(s**2)
    inprimatu(results.sizes2())
    vg = results.gyradius()
    rtt = mk.getRtt(temperatures)
    d = f.fractalDimension(rtt/flux)
    rg = results.lastGyradius()
    y = (s*(fs*flux**0.215)**(4/3))/rg**2
    r = np.array([i if i>1 else float('nan') for i in results.totalRatio() ])
    x = r/flux**0.79
    try:
        plt.loglog(x, y,  label=folder)
        plt.loglog(x, x/1e4, "--")
        plt.legend(loc='upper left', prop={'size':6})
        plt.savefig("sFsRgVsRate.png")
    except ValueError:
        plt.close()
        traceback.print_exc(file=sys.stdout)
        print("error plotting")
        print(x)
        print(y)
    
plt.close()


print("Good bye!")
          
