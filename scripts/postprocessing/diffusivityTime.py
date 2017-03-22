import info as inf
import os
import matplotlib.pyplot as plt
import numpy as np

def read():
    p = inf.getInputParameters()
    d = inf.readAverages()
    return p, d
    

def diffusivityTime(p, d, coverage):
    try:
        divisor = d.cove[100-coverage]*d.time[100-coverage]*p.flux**0.7
        r = p.flux, d.cove[100-coverage]/divisor, d.diff[100-coverage]/divisor, d.hops[100-coverage]/divisor, d.even[100-coverage]/divisor
    except IndexError:
        r = p.flux, None, None, None, None
    return r

def plot(ax, data, i, showLegend):
    cm = plt.get_cmap("Accent")
    alpha = 0.5
    mew = 0
    data = np.array(data)
    flux = data[0,0]
    x = 1/kb/data[:,1]+np.log(flux**1.5)
    lg1, = ax.plot(x, data[:,3], label=r"$R^2$ {:1.0E}".format(flux),
            marker="o", ls="", mew=mew, ms=8, alpha=alpha, markerfacecolor=cm(i/8))
    lg2, = ax.plot(x, data[:,4], label=r"$N_h$ {:1.0E}".format(flux),
            marker="+", ls="", mew=1, markeredgecolor=cm(i/8), ms=7, alpha=1)
    if showLegend:
        ax.legend(handles=[lg1, lg2], loc=1, numpoints=1, prop={'size':8}, markerscale=1.5)

workingPath = os.getcwd()
fluxes = inf.getFluxes()
i = 0
coverage1 = 10
coverage2 = 40
coverage3 = 60
kb = 8.617332e-5
fig = plt.figure(num=None, figsize=(6,5))
ax = fig.gca()
ax.set_yscale("log")
ax.set_title("{} % coverage".format(coverage1))
ax.set_xlabel(r"$1/k_BT + ln(f^{1.5})$")
ax.set_ylabel(r"$R^2/f^{0.7}$,  $N_h/f^{0.7}$")
for i,f in enumerate(fluxes):
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    data1 = []
    data2 = []
    data3 = []
    for t in inf.getTemperatures()[10:]:
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            p, d = read()
            flux, cove, diff, hops, even = diffusivityTime(p, d, coverage1)
            data1.append([flux, t, cove, diff, hops, even])
            flux, cove, diff, hops, even = diffusivityTime(p, d, coverage2)
            data2.append([flux, t, cove, diff, hops, even])
            flux, cove, diff, hops, even = diffusivityTime(p, d, coverage3)
            data3.append([flux, t, cove, diff, hops, even])
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    ### Plot
    plot(ax, data1, i, True)
    plot(ax, data2, i, False)
    plot(ax, data3, i, False)
    fig.savefig("../diff.pdf", bbox_inches='tight')
    os.chdir(workingPath)
