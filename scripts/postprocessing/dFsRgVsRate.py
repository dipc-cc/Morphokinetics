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
kb = 8.6173324e-5
for i in range(-3,1):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        meanValues = mk.getIslandDistribution(temperatures, sqrt=False, interval=False, growth=True, verbose=False, flux=flux)
    except OSError:
        print ("error changing to flux {}".format(folder))

    os.chdir(workingPath)
    vs = meanValues.getGrowthSlope()
    vsM = meanValues.growthSlopes
    fs = meanValues.getFluctuationSizes()
    vsPrime = np.maximum(vs,(1+fs**(4/3))*flux**1.08)
    N = (0.3*400*400)/meanValues.getIslandsAmount()
    N = meanValues.getIslandsAmount()
    N2 = meanValues.getIslandsAmount2()
    n = meanValues.getMonomersAmount()
    fn = meanValues.getFluctuationMonomers()
    s = meanValues.getSizes()
    fN = meanValues.getFluctuationIslandAmount()
    fn = meanValues.getFluctuationMonomers()
    vn = meanValues.getMonomersSlope()
    vg = meanValues.getGyradiusSlope()
    vN = meanValues.getIslandsAmountSlope()
    rtt = mk.getRtt(temperatures)
    d = mk.fractalDFunc(rtt/3.5e-1)
    d0 = mk.fractalDFunc(rtt/flux)
    
    #d = mk.readFractalD(flux)
    rg = meanValues.getLastGyradius()
    vpi = meanValues.getInnerPerimeterSlope()
    vpo = meanValues.getOuterPerimeterSlope()
    pi = meanValues.getLastInnerPerimeter()
    po = meanValues.getLastOuterPerimeter()
    fpi = meanValues.getFluctuationInnerPerimeter()
    fpo = meanValues.getFluctuationOuterPerimeter()
    t = meanValues.getTimes()
    r = np.array([i if i>1 else float('nan') for i in meanValues.getTotalRatio() ]).astype(float)
    #r = meanValues.totalRatio()
    sigmaS = meanValues.getSizesStd()
    sigmaG = meanValues.getGyradiusStd()
    T1 = 1/(kb * temperatures)
    cov = 48000
    x= 0.5

    command = "(1/d)*(fs*rg*flux**0.325)**(d/2*0.8333)"
    command = "s**d0"
    command = "s"
    y = eval(command)
    plt.title(command)
    command = "r/flux**0.8"
    command = "T1"
    x = eval(command)
    plt.xlabel(command)
    try:
        #fig1 = plt.figure(1, figsize=(4,4), dpi=80, facecolor='w', edgecolor='k')
        plt.semilogy(x, y, "-x", label=folder)
        #print(vsM-vs)
        #plt.loglog(x, np.array(vsM).astype(float), ".", label="out "+folder)
        #plt.loglog(x, x, color="black")
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
          
