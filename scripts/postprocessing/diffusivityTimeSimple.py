import info as inf
import functions as fun
import os
import sys
import matplotlib.pyplot as plt
import matplotlib.lines as mlines
from matplotlib.ticker import FixedFormatter
import numpy as np
import math
import roman


def read():
    p = inf.getInputParameters()
    d = inf.readAverages()
    return p, d
    

def plot(ax, data, i):
    marker = ["o", "s","H","D","^","d","h","p","o"]
    cm = plt.get_cmap("Accent")
    alpha = 0.5
    mew = 0
    data = np.array(data)
    try:
        flux = data[0,0]
    except IndexError:
        raise

    x = 1/kb/data[:,1]+np.log(flux**1.5)
    lw = 3
    lg1, = ax.plot(x, data[:,3], label=r"$F=$"+fun.base10(flux),
                   lw=lw, marker="", ls="-", mew=mew, ms=12, alpha=alpha, color=cm(i/8))
    ax.plot(x, data[:,4], label=r"$N_h$"+fun.base10(flux),
            lw=lw+1, ls="-.", mew=1, color=cm(i/8), ms=7, alpha=1)
    #ax.text(x[4], data[:,4][4], fun.base10(flux), color=cm(i/8), size=10)
    arrow = dict(arrowstyle="->", connectionstyle="arc3", ls="-", color=cm(i/8))
    ax.annotate(fun.base10(flux), xy=(x[2], data[:,4][2]), color=cm(i/8),
                xytext=(0.85,0.6-0.05*i), textcoords="axes fraction", arrowprops=arrow)
    if i == 3:
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.3)
        label = r"$\theta=0.30$"
        ax.annotate(label, xy=(0.65,0.9), xycoords="axes fraction",
                    bbox=bbox_props)
        ax.annotate("", xy=(44,1e-3), xycoords='data', xytext=(44, 1e4),
                    arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
        ax.annotate("", xy=(86,1e-3), xycoords='data', xytext=(86, 1e4),
                    arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
        #Fit
        rngt = inf.defineRanges("AgUc","simple", data[:,1])
        for i in range(0,len(rngt)-1):
            y = data[:,4]
            a, b = fun.linearFit(x, y, rngt[i], rngt[i+1])
            ax.semilogy(x[rngt[i]:rngt[i+1]+1], np.exp(fun.linear(x[rngt[i]:rngt[i+1]+1], a, b)), ls="-", color="black", zorder=+200)
            xHalf = (x[rngt[i]]+x[rngt[i+1]]+1)/2
            text = "{:03.3f}".format(-b)
            yHalf = np.exp(fun.linear(xHalf, a, b))*5
            text = r"$E_a^{Arrh}="+text+r"$"
            bbox_props = dict(boxstyle="round", fc="w", ec="1", alpha=0.6)
            ax.text(xHalf,yHalf, text, color="black", bbox=bbox_props, ha="center", va="center", size=10)
    return lg1

workingPath = os.getcwd()
fluxes = inf.getFluxes()
i = 0
coverage1 = 30
kb = 8.617332e-5
fig = plt.figure(num=None, figsize=(6,4))
ax = fig.gca()
ax.set_yscale("log")
ax.set_xlabel(r"$1/k_BT + ln(F^{1.5})$")
ax.set_xlim(0,250)
ax.set_ylim(1e-3,1e4)
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
        except (TypeError, FileNotFoundError):
            pass
        os.chdir(fPath)
    ### Plot
    if i == 0:
        rLg = mlines.Line2D([], [], color='black', ls="-",
                            markersize=10, label=r"$g\; \frac{\langle R^2\rangle}{t F^{0.7}}$")
        nLg = mlines.Line2D([], [], color='black', ls="-.",
                            markersize=8, label=r"$gl^2\; \frac{\langle N_h\rangle}{t F^{0.7}}$")
        iLg = mlines.Line2D([], [], color='black', ls='--',
                            markersize=8, label=r"$N_{isl}$")
        legends.append(rLg)
        legends.append(nLg)
        y = 1e-2
        ax.text(120, y, "$I$", color="gray")
        ax.text(60, y, "$II$", color="gray")
        ax.text(25, y, "$III$", color="gray")
    try:
        plot(ax, data1, i)
    except IndexError:
        os.chdir(workingPath)
        continue
    ax.legend(handles=legends, loc="best", #bbox_to_anchor=(0.75, 0.32),
              bbox_transform=plt.gcf().transFigure, numpoints=1, ncol=1, prop={'size':8}, markerscale=1)
    fig.savefig("../diffusivityTimeSimple.pdf", bbox_inches='tight')
    os.chdir(workingPath)
