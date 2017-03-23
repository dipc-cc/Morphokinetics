import info as inf
import functions as fun
import os
import matplotlib.pyplot as plt
import numpy as np

def read():
    p = inf.getInputParameters()
    d = inf.readAverages()
    return p, d
    

def diffusivityTime(p, d, coverage):
    try:
        divisor = d.time[100-coverage]*p.flux**0.7
        r = p.flux, d.cove[100-coverage]/divisor, d.diff[100-coverage]/divisor, d.hops[100-coverage]/divisor, d.isld[100-coverage]/(p.flux**0.29)#np.max(d.isld)/(p.flux**0.29)
    except IndexError:
        r = p.flux, None, None, None, None
    return r

def plot(ax, ax2, data, i):
    cm = plt.get_cmap("Accent")
    alpha = 0.5
    mew = 0
    data = np.array(data)
    flux = data[0,0]
    x = 1/kb/data[:,1]+np.log(flux**1.5)
    if ax2 != None:
        lg3, = ax2.plot(x, data[:,5], label=r"$N_{isld}$",
                        color=cm(i/8))
    else:
        lg3 = None
    lg1, = ax.plot(x, data[:,3], label=r"$R^2$"+fun.base10(flux),
            marker="o", ls="", mew=mew, ms=8, alpha=alpha, markerfacecolor=cm(i/8))
    lg2, = ax.plot(x, data[:,4], label=r"$N_h$"+fun.base10(flux),
            marker="+", ls="", mew=1, markeredgecolor=cm(i/8), ms=7, alpha=1)
    return lg1, lg2, lg3

workingPath = os.getcwd()
fluxes = inf.getFluxes()
i = 0
coverage1 = 10
coverage2 = 50
coverage3 = 70
kb = 8.617332e-5
fig = plt.figure(num=None, figsize=(6,5))
ax = fig.gca()
ax2 = ax.twinx()
ax.set_yscale("log")
ax2.set_yscale("log")
ax.set_title("{} % coverage".format(coverage1))
ax.set_xlabel(r"$1/k_BT + ln(F^{1.5})$")
ax.set_ylabel(r"$R^2/(t F^{0.7})$,  $N_h/(t F^{0.7})$")
ax2.set_ylabel(r"$N_{isld}$")
legends = []
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
            flux, cove, diff, hops, isld = diffusivityTime(p, d, coverage1)
            data1.append([flux, t, cove, diff, hops, isld])
            flux, cove, diff, hops, isld = diffusivityTime(p, d, coverage2)
            data2.append([flux, t, cove, diff, hops, isld])
            flux, cove, diff, hops, isld = diffusivityTime(p, d, coverage3)
            data3.append([flux, t, cove, diff, hops, isld])
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    ### Plot
    lg1, lg2, g3 = plot(ax, None, data1, i)
    g1, g2, lg3 = plot(ax, ax2, data2, i)
    legends.append(lg1)
    legends.append(lg2)
    legends.append(lg3)
    plot(ax, None, data3, i)
    ax.legend(handles=legends, loc=1, numpoints=1, ncol=2, prop={'size':8}, markerscale=1)
    fig.savefig("../diffusivityTime.pdf", bbox_inches='tight')
    os.chdir(workingPath)
