import info as inf
import functions as fun
import os
import matplotlib.pyplot as plt
import matplotlib.lines as mlines
from matplotlib.ticker import FixedFormatter
import numpy as np
import math


def getTemp(x,F):
    return 1/(8.62e-5*(x-np.log(F**1.5)))

def read():
    p = inf.getInputParameters()
    d = inf.readAverages()
    return p, d
    

def plot(ax, ax2, data, i):
    marker = ["o", "s","H","D","^","d","h","p","o"]
    cm = plt.get_cmap("Accent")
    alpha = 0.5
    mew = 0
    data = np.array(data)
    flux = data[0,0]
    x = 1/kb/data[:,1]+np.log(flux**1.5)
    if ax2 != None:
        ax2.plot(x, data[:,5], label="isld",
                        color=cm(i/8))
        ax2.plot(x, fun.power(np.exp(x), 2, 0.0333))
    lg1, = ax.plot(x, data[:,3], label=r"$F=$"+fun.base10(flux),
            marker=marker[i], ls="", mew=mew, ms=8, alpha=alpha, markerfacecolor=cm(i/8))
    ax.plot(x, data[:,4], label=r"$N_h$"+fun.base10(flux),
            marker="+", ls="--", mew=1, color=cm(i/8), markeredgecolor=cm(i/8), ms=7, alpha=1)
    if i == 4:
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.3)
        label = r"$\theta=30\%$"
        ax.annotate(r"asymptote", xy=(140,3e2),
                    bbox=bbox_props)
        ax.annotate(label, xy=(0.2,0.9), xycoords="axes fraction",
                    bbox=bbox_props)
        ax.annotate("", xy=(44,1e-5), xycoords='data', xytext=(44, 1e4),
                    arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
        ax.annotate("", xy=(86,1e-5), xycoords='data', xytext=(86, 1e4),
                    arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
    return lg1

workingPath = os.getcwd()
fluxes = inf.getFluxes()
i = 0
coverage1 = 30
kb = 8.617332e-5
fig = plt.figure(num=None, figsize=(6,5))
ax = fig.gca()
ax2 = ax.twinx()
ax.set_yscale("log")
ax2.set_yscale("log")
ax2.set_ylabel(r"$N_{isl}$")
ax.set_xlabel(r"$1/k_BT + ln(F^{1.5})$")
ax.set_xlim(0,250)
ax3 = ax.twiny()
ax3.set_xlim(0,250)
ax3.set_xlabel(r"Temperature at $F=5\cdot10^4$")
x2labels = [int(getTemp(i,1e4)) for i in np.arange(0,260,50)]
x2labels = [r"$\infty$" if i<0 else i for i in x2labels]
ax3.get_xaxis().set_major_formatter(FixedFormatter(x2labels))
legends = []
for i,f in enumerate(fluxes):
    if i<-1:
        continue
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    data1 = []
    for t in inf.getTemperatures():
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            p, d = read()
            flux, cove, diff, hops, isld = inf.getAvgDataCoverage(p, d, coverage1)
            data1.append([flux, t, cove, diff, hops, isld])
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    ### Plot
    if i == 0:
        rLg = mlines.Line2D([], [], color='white', marker='o',
                            markersize=10, label=r"$\frac{1}{2dN_a} \; \frac{\langle R^2\rangle}{t F^{0.7}}$")
        nLg = mlines.Line2D([], [], color='white', marker='+', markeredgecolor="black", markeredgewidth=1,
                            markersize=8, label=r"$\frac{l^2}{2dN_a} \; \frac{\langle N_h\rangle}{t F^{0.7}}$")
        iLg = mlines.Line2D([], [], color='black', marker='',
                            markersize=8, label=r"$N_{isl}$")
        legends.append(rLg)
        legends.append(nLg)
        legends.append(iLg)
    lg1 = plot(ax, ax2, data1, i)
    legends.append(lg1)
    ax.legend(handles=legends, bbox_to_anchor=(0.75, 0.32),
              bbox_transform=plt.gcf().transFigure, numpoints=1, ncol=2, prop={'size':8}, markerscale=1)
    fig.savefig("../diffusivityTime.pdf", bbox_inches='tight')
    os.chdir(workingPath)
