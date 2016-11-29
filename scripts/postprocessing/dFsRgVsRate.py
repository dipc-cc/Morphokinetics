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

label = r''
plt.ylabel(label)
label = r'$R/F^{0.79}$'
plt.xlabel(label)
#plt.figure(num=None, figsize=(4,4), dpi=80, facecolor='w', edgecolor='k')
#plt.figure(num=2, figsize=(4,4), dpi=80, facecolor='b', edgecolor='k')
plt.grid(True)
temperatures = np.array(list(range(120,321,5)))

workingPath = os.getcwd()
results = results.Results()
kb = 8.6173324e-5
for i in range(-6,1):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        results.append(mk.getIslandDistribution(temperatures, sqrt=False, interval=False, growth=True, verbose=False, flux=flux))
    except OSError:
        print ("error changing to flux {}".format(folder))

    os.chdir(workingPath)
    vs = results.growthSlope()
    fs = results.fluctuationSizes()
    vsPrime = np.maximum(vs,(1+fs**(4/3))*flux**1.08)
    N = (0.3*400*400)/results.islands()
    N = results.islands()
    N2 = results.islands2()
    n = results.monomers()
    fn = results.fluctuationMonomers()
    s = results.sizes()
    fN = results.fluctuationIslandAmount()
    fn = results.fluctuationMonomers()
    vn = results.monomersSlope()
    vg = results.gyradius()
    vN = results.sizesSlope()
    rtt = mk.getRtt(temperatures)
    d = mk.fractalDFunc(rtt/3.5e-1)
    d0 = mk.fractalDFunc(rtt/flux)
    d = mk.readFractalD(flux)
    rg = results.lastGyradius()
    vpi = results.innerPerimeterSlope()
    vpo = results.outerPerimeterSlope()
    pi = results.lastInnerPerimeter()
    po = results.lastOuterPerimeter()
    fpi = results.fluctuationInnerPerimeter()
    fpo = results.fluctuationOuterPerimeter()
    t = results.times()
    r = np.array([i if i>1 else float('nan') for i in results.totalRatio() ]).astype(float)
    #r = results.totalRatio()
    sigmaS = results.sizesStd()
    sigmaG = results.gyradiusStd()
    T1 = 1/(kb * temperatures)
    cov = 48000
    command = "(1/d)*(fs*rg*flux**0.325)**(d/2*0.8333)"
    #command = "(s**(d/2))"
    y = eval(command)
    plt.title(command)
    command = "r"
    x = eval(command)
    plt.xlabel(command)
    try:
        #fig1 = plt.figure(1, figsize=(4,4), dpi=80, facecolor='w', edgecolor='k')
        plt.loglog(x, 1e5*y, label=folder)
        #plt.loglog(x, 1e5*po, ".", label="out "+folder)
        plt.loglog(x, x, color="black")
        #plt.plot(x, 1e3*d**10, "-", label="d "+folder)
        #plt.plot(x, 1e3*d0**10, "-", label="d0 "+folder)
        #plt.loglog(x, 48000/N, label=flux)
        #plt.loglog(s, rg**d)
        #print(d)
        plt.legend(loc='upper left', prop={'size':6})
        plt.savefig("dFsRgVsRate.png")
        #fig2 = plt.figure(2)
        #plt.xlabel("1/kBT * log(F)^1.78")
        #plt.loglog(T1+np.log(flux**1.78), y)
        #fig2.savefig("dFsRgVsRate2.png")
    except ValueError:
        plt.close()
        traceback.print_exc(file=sys.stdout)
        print("error plotting")
        print(x)
        print(y)
#plt.show()
    
plt.close()


print("Good bye!")
          
