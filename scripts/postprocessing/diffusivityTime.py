import info as inf
import os
import matplotlib.pyplot as plt
import numpy as np


def diffusivityTime(coverage):
    p = inf.getInputParameters()
    d = inf.readAverages()
    divisor = d.cove[100-coverage]*d.time[100-coverage]*p.flux**0.7
    return p.flux, d.cove[100-coverage]/divisor, d.diff[100-coverage]/divisor, d.hops[100-coverage]/divisor, d.even[100-coverage]/divisor


workingPath = os.getcwd()
fluxes = inf.getFluxes()
i = 0
coverage = 10
kb = 8.617332e-5
for f in fluxes:
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    data = []
    for t in inf.getTemperatures()[10:]:
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            flux, cove, diff, hops, even = diffusivityTime(coverage)
            data.append([flux, t, cove, diff, hops, even])
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    ### Plot
    data = np.array(data)
    fig = plt.figure(num=None, figsize=(6,5))
    ax = fig.gca()
    flux = data[0,0]
    x = 1/kb/data[:,1]+np.log(flux**1.5)
    ax.plot(x, data[:,3], "-o", label=r"$R^2$ {:1.0E}".format(flux))
    ax.plot(x, data[:,4], "-x", label=r"$N_h$ {:1.0E}".format(flux))
    ax.plot(x, data[:,5], "-", label=r"events {:1.0E}".format(flux))
    ax.legend(loc="best")
    ax.set_yscale("log")
    ax.grid()
    ax.set_title("{} % coverage".format(coverage))
    fig.savefig("../diff.png")
    os.chdir(workingPath)
