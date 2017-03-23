import info as inf
import functions as fun
import os
import matplotlib.pyplot as plt
import matplotlib.lines as mlines
import numpy as np

def read():
    p = inf.getInputParameters()
    d = inf.readAverages()
    return p, d
    

def diffusivityTime(p, d, coverage):
    try:
        divisor = d.time[100-coverage]*(p.flux**0.7)
        r = p.flux, d.cove[100-coverage]/divisor, d.diff[100-coverage]/divisor, d.hops[100-coverage]/divisor, np.max(d.isld[-95:])/(p.flux**0.27)#d.isld[100-coverage]/(p.flux**0.27)#
    except IndexError:
        r = p.flux, None, None, None, None
    return r

def plot(ax, ax2, data, i, showTheta):
    cm = plt.get_cmap("Accent")
    alpha = 0.5
    mew = 0
    data = np.array(data)
    flux = data[0,0]
    x = 1/kb/data[:,1]+np.log(flux**1.5)
    if ax2 != None:
        ax2.plot(x, data[:,5], label=r"$N_{isld}$",
                        color=cm(i/8))
        ax2.plot(x, fun.power(np.exp(x), 2, 0.0333))
    lg1, = ax.plot(x, data[:,3], label=r"$F=$"+fun.base10(flux),
            marker="o", ls="", mew=mew, ms=8, alpha=alpha, markerfacecolor=cm(i/8))
    ax.plot(x, data[:,4], label=r"$N_h$"+fun.base10(flux),
            marker="+", ls="--", mew=1, color=cm(i/8), markeredgecolor=cm(i/8), ms=7, alpha=1)
    if showTheta != None:
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.3)
        label = r"$\theta={}\%$".format(showTheta)
        ax.annotate(label, xy=(60,data[4,3]),
                    bbox=bbox_props)
        ax.annotate(r"$1/3$", xy=(90,4e10),
                    bbox=bbox_props)
        ax.annotate("", xy=(44,1e2), xycoords='data', xytext=(44, 1e12),
                    arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
        ax.annotate("", xy=(86,1e2), xycoords='data', xytext=(86, 1e12),
                    arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
    return lg1

workingPath = os.getcwd()
fluxes = inf.getFluxes()
i = 0
coverage1 = 30
coverage2 = coverage1
coverage3 = coverage1
kb = 8.617332e-5
fig = plt.figure(num=None, figsize=(6,5))
ax = fig.gca()
ax2 = ax.twinx()
ax.set_yscale("log")
ax2.set_yscale("log")
ax.set_xlabel(r"$1/k_BT + ln(F^{1.5})$")
ax.set_ylabel(r"$R^2/(t F^{0.7})$,  $N_h/(t F^{0.7})$")
ax2.set_ylabel(r"$N_{isld}$")
legends = []
showTheta = [coverage1, coverage2, coverage3]
for i,f in enumerate(fluxes):
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    data1 = []
    data2 = []
    data3 = []
    for t in inf.getTemperatures():
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
    if showTheta[0] != None:
        rLg = mlines.Line2D([], [], color='white', marker='o',
                            markersize=10, label=r"$R^2$")
        nLg = mlines.Line2D([], [], color='white', marker='+', markeredgecolor="black", markeredgewidth=1,
                            markersize=8, label=r"$N_h$")
        iLg = mlines.Line2D([], [], color='black', marker='',
                            markersize=8, label=r"$N_{isld}$")
        legends.append(rLg)
        legends.append(nLg)
        legends.append(iLg)
    lg1 = plot(ax, None, data1, i, showTheta[0])
    showTheta = showTheta[1:]
    plot(ax, ax2, data2, i, showTheta[0])
    showTheta = showTheta[1:]
    legends.append(lg1)
    plot(ax, None, data3, i, showTheta[0])
    showTheta = [None, None, None]
    ax.legend(handles=legends, loc=1, numpoints=1, ncol=2, prop={'size':8}, markerscale=1)
    fig.savefig("../diffusivityTime.pdf", bbox_inches='tight')
    os.chdir(workingPath)
