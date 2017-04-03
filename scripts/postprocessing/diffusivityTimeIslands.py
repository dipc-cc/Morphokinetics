import info as inf
import functions as fun
import os
import matplotlib.pyplot as plt
import matplotlib.lines as mlines
import matplotlib.ticker as plticker
from matplotlib.ticker import LogLocator
from matplotlib.patches import Ellipse
import numpy as np

def read():
    p = inf.getInputParameters()
    d = inf.readAverages()
    return p, d

def addEllipses():
    alpha = 0.05
    position = [0.16, 0.5, 0.3, 0.3]
    newax = plt.gcf().add_axes(position, zorder=+100)
    newax.patch.set_alpha(0)
    newax.yaxis.set_major_locator(plticker.NullLocator())
    newax.xaxis.set_major_locator(plticker.NullLocator())
    newax.axis('off')
    ellipse = Ellipse((0.3, 0.6), 0.3, 0.6,
                      edgecolor='None', fc='green', alpha=alpha)
    newax.add_patch(ellipse)

    position = [0.16, 0.15, 0.3, 0.3]
    newax = plt.gcf().add_axes(position, zorder=+100)
    newax.patch.set_alpha(0)
    newax.yaxis.set_major_locator(plticker.NullLocator())
    newax.xaxis.set_major_locator(plticker.NullLocator())
    newax.axis('off')
    ellipse = Ellipse((0.3, 0.6), 0.3, 0.6,
                      edgecolor='None', fc='blue', alpha=alpha)
    newax.add_patch(ellipse)
    
    position = [0.61, 0.28, 0.3, 0.3]
    newax = plt.gcf().add_axes(position, zorder=+100)
    newax.patch.set_alpha(0)
    newax.yaxis.set_major_locator(plticker.NullLocator())
    newax.xaxis.set_major_locator(plticker.NullLocator())
    newax.axis('off')
    ellipse = Ellipse((0.3, 0.6), 0.3, 0.45,
                      edgecolor='None', fc='red', alpha=alpha, angle=45)
    newax.add_patch(ellipse)
    
    position = [0.78, 0.78, 0.1, 0.1]
    newax = plt.gcf().add_axes(position, zorder=+100)
    newax.patch.set_alpha(0)
    newax.yaxis.set_major_locator(plticker.NullLocator())
    newax.xaxis.set_major_locator(plticker.NullLocator())
    newax.axis('off')
    ellipse = Ellipse((0.5, 0.5), 0.3, 0.5,
                      edgecolor='None', fc='brown', alpha=alpha)
    newax.add_patch(ellipse)
    

def plot(ax, ax2, data, i):
    marker = ["o", "s","H","D","^","d","h","p","o"]
    cm = plt.get_cmap("Accent")
    alpha = 0.5
    mew = 0
    lw = 3
    data = np.array(data)
    flux = data[0,0]
    x = data[:,6]/flux**0.17
    if ax2 != None:
        ax2.plot(x, data[:,5], label="isld",
                        lw=lw, ls = "--", color=cm(i/8))
        ax2.text(x[0]/8, data[:,5][0], fun.base10(flux), color=cm(i/8), size=10)
    lg1, = ax.plot(x, data[:,3], label=r"$F=$"+fun.base10(flux),
                   lw=lw, marker="", ls="-", mew=mew, ms=8, alpha=alpha, color=cm(i/8))
    ax.plot(x, data[:,4], label=r"$N_h$"+fun.base10(flux),
            lw=lw+1, marker="", ls="-.", mew=1, color=cm(i/8), markeredgecolor=cm(i/8), ms=7, alpha=1)
    ax.text(x[0]/8, data[:,4][0], fun.base10(flux), color=cm(i/8), size=10)
    if i == 0:
        ax.annotate("",xytext=(x[1],data[:,4][1]*1.5), xy=(x[1]/80,data[:,4][1]*3),
                     arrowprops=dict(arrowstyle="->", connectionstyle="angle3", ls="-", color="gray"), ha="center", va="center")
    if i == 4:
        xFit = np.array([1e2, 1e12])
        ax2.plot(xFit, fun.power(xFit, 4e4, -0.3333), ls=":", color="black")
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.3)
        label = r"$\theta=0.30$"
        ax.annotate(r"$1/3$", xy=(1e5,3e2),
                    bbox=bbox_props)
        ax.annotate(label, xy=(0.5,0.93), xycoords="axes fraction",
                    bbox=bbox_props)
        ax.annotate("", xy=(1e9,1e-5), xycoords='data', xytext=(1e9, 1e4),
                    arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
        ax.annotate("", xy=(8e10,1e-5), xycoords='data', xytext=(8e10, 1e4),
                    arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
        addEllipses()
        alpha=0.2
        arrow = dict(arrowstyle="-[",  ls="-", color="gray", alpha=0)
        ax.annotate("A", xy=(7e2,3), xytext=(7e2,1), ha="center", va="center",
                    arrowprops=arrow, color="green", alpha=alpha)
        ax.annotate("B", xy=(7e2,1e-4), xytext=(7e2,1e-4), ha="center", va="center",
                    arrowprops=arrow, color="blue", alpha=alpha)
        ax.annotate("C", xy=(8e9,1), xytext=(8e9,5e-1), ha="center", va="center",
                    arrowprops=arrow, color="red", alpha=alpha)
        ax.annotate("D", xy=(8e11,2e2), xytext=(8e11,7e2), ha="center", va="center",
                    arrowprops=arrow, color="brown", alpha=alpha)
        y = 3e-5
        ax.text(2e7, y, "$I$", color="gray")
        ax.text(5e9, y, "$II$", color="gray")
        ax.text(2e11, y, "$III$", color="gray")

        ax2.annotate("",xy=(x[-1]*18,data[:,5][-1]), xytext=(x[-1]*2,data[:,5][-1]/1.5),
                     arrowprops=dict(arrowstyle="->", connectionstyle="angle3", ls="-", color="gray"), ha="center", va="center")


workingPath = os.getcwd()
fluxes = inf.getFluxes()
i = 0
coverage1 = 30
kb = 8.617332e-5
fig = plt.figure(num=None, figsize=(6,5))
ax = fig.gca()
ax2 = ax.twinx()
ax.set_xscale("log")
ax.set_yscale("log")
ax2.set_yscale("log")
ax.set_xlabel(r"$r_{tt}/F^{0.17}$")
ax.set_xlim(8,1e13)
legends = []
for i,f in enumerate(fluxes):
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
            data1.append([flux, t, cove, diff, hops, isld, p.r_tt])
        except FileNotFoundError:
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
        legends.append(iLg)
    plot(ax, ax2, data1, i)
    ax.legend(handles=legends, bbox_to_anchor=(0.63, 0.45),
              bbox_transform=plt.gcf().transFigure, numpoints=1, ncol=1, prop={'size':12}, markerscale=1)
    
    locator = LogLocator(100,[10])
    ax.xaxis.set_major_locator(locator)
    fig.savefig("../diffusivityTimeIslands.pdf")#, bbox_inches='tight')
    os.chdir(workingPath)
