# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import morphokineticsLow as mkl
import scipy
import csv
from scipy.optimize import curve_fit

def fractalDimensionFit():
    r=np.reshape(resultsFractal, np.shape(resultsFractal)[0]*np.shape(resultsFractal)[1])
    a=0.27
    b=1.73
    c=19
    sigma=2
    plt.close()
    plt.xlabel("log(r_tt/F^0.5)")
    plt.ylabel("d")
    plt.plot(x, r, ".", label="data")
    plt.plot(x, mkl.ourErrFunc(x, a, b, c, sigma), "o", label="fit fermi")
    ####################################################################################################
    z=np.exp(x)
    z0=np.exp(c)
    y=b+a*(z**sigma)/(z**sigma+z0**sigma)
    plt.plot(x, y, ".", label="new")
    
    ####################################################################################################
    a=a/2
    b=b+a
    c=19
    sigma=0.75
    plt.plot(x, mkl.errFunc(x, a, b, c, sigma), "s", label="fit errf")
    plt.legend(loc='upper left', prop={'size':12})
    plt.grid(True)
    plt.savefig("fractalDimensionAll.png")

def factorDimensionFit():
    r=np.reshape(resultsFractal, np.shape(resultsFractal)[0]*np.shape(resultsFractal)[1])
    a=5.5
    b=3.5
    z=np.exp(x)
    c=18
    z0=np.exp(c)
    sigma=1
    plt.close()
    y=b+a*(z**sigma)/(z**sigma+z0**sigma)
    plt.grid(True)
    max = 7.3
    y = [max-(i-max)  if i > max else i for i in y]
    plt.plot(x, y, "o", label="fit")
    plt.plot(x, r, ".", label="data")
    plt.legend(loc='upper left', prop={'size':12})
    plt.xlabel("log(r_tt/F^0.5)")
    plt.ylabel("shape factor")
    plt.savefig("factorAll.png")
    
    
label = r'Fractal dimension'
plt.ylabel(label)
label = r'$\frac{r_{tt}}{F^{0.5}}$'
plt.xlabel(label)
plt.grid(True)
axes = plt.gca()
#axes.set_ylim([1.5,2.1])
temperatures = list(range(120,321,5))

workingPath = os.getcwd()
print(workingPath)
resultsFractal = []
x = np.empty(0)
for i in range(-6,5):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    verbose = False
    sqrt = False
    growth = False
    try:
        os.chdir(folder)
        resultsFractal.append(mk.getAllFractalDimensions(temperatures, verbose))
    except OSError:
        print ("ERROR changing to {}".format(folder))
    os.chdir(workingPath)

    while(len(temperatures) > len(resultsFractal[-1])):
       resultsFractal[-1].append(float('nan'))
    rttFlux = np.log(mk.getRtt(temperatures)/flux**0.5)
    x = np.concatenate((x, rttFlux))
    filename = "fractalD"+'{:E}'.format(flux)+".txt"
    with open(filename, 'w', newline='') as csvfile:
        outwriter = csv.writer(csvfile, delimiter=' ', quotechar='|', quoting=csv.QUOTE_MINIMAL)
        outwriter.writerow(["%index", "temp", "flux", "shapeF"])
        for index, temp in enumerate(temperatures):
            outwriter.writerow([index, temp, flux, resultsFractal[-1][index]])
            
    plt.plot(np.log(rttFlux),np.array(resultsFractal[-1]), ".-", label=folder)
    #plt.ylim(1,2.1)
    #plt.plot(temperatures, np.array(resultsFractal[-1]), ".-", label=folder)
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("fractalDimension.png")

fractalDimensionFit()
factorDimensionFit()
print("Good bye!")
          
