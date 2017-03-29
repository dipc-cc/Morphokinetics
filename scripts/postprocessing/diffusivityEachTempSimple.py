#grep -v histo dataEvery1percentAndNucleation.txt | grep -v Ae | awk -v n=-1 '{if ($1<prev) {n++}prev=$1;} {print > "data"n".txt"} END{print n}'
# sed -i '1d' data0.txt
import info as inf
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as plticker
from matplotlib.patches import Rectangle
import glob
import os
import sys
import functions as fun
from scipy.signal import savgol_filter
from PIL import Image

def addSurface(temperature, ax=0):
    try:
        fileName = glob.glob("/home/jalberdi004/mk_test/activationEnergy/agUc/200/2.-surfaces/flux3.5e4/"+str(int(temperature))+"/results/*/surface000.png")[0]
        im = Image.open(fileName)
        position = [0.16, 0.3, 0.3, 0.3]
        if ax != 0:
            position = [-0.053, 0.27, 0.22, 0.22]
            position[0:2] += ax.get_position().get_points().reshape(4)[0:2]
        newax = plt.gcf().add_axes(position, zorder=+100)
        newax.imshow(im)
        newax.yaxis.set_major_locator(plticker.NullLocator())
        newax.xaxis.set_major_locator(plticker.NullLocator())
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.9)
        newax.annotate(r"$\theta = 0.3$", xy=[200,100], bbox=bbox_props)
    except IndexError:
        pass

def addFreeDiffusivity(ax, x, p):
    sublabel = {150: "a)", 250: "b)", 750: "c)"}
    try:
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.9)
        if p.temp == 150:
            note = "$F=$".format(int(p.temp))+fun.base10(p.flux)+"$ML/s$\n${}".format(p.sizI)+r"\times{}$".format(p.sizJ)
            ax.annotate(note, xy=(0.06,0.90), xycoords="axes fraction", size=14, bbox=bbox_props)
        note = sublabel[p.temp]+" ${} K$".format(int(p.temp))
        ax.annotate(note, xy=(0.06,0.74), xycoords="axes fraction", size=14, bbox=bbox_props)
    except KeyError:
        pass
    x = x[0:20]
    y = np.ones(len(x))
    y = y * 3/2*p.getRatios()[0]
    cm = plt.get_cmap("Accent")
    ax.plot(x, y, "-", color=cm(8/8))
    ax.annotate(r"$\frac{1}{2d}m_{0}\nu_{0}l^2$", xytext=(2e-2,4e11), textcoords="data",
                xy=(x[-1],y[-1]), xycoords='data', arrowprops=dict(arrowstyle="->", connectionstyle="arc3", color=cm(8/8)))
    y = y / 2
    ax.plot(x, y, "-", color=cm(8/8))
    ax.annotate(r"$\frac{1}{4d}m_{0}\nu_{0}l^2$", xytext=(2e-2,2e10), textcoords="data",
                xy=(x[-1],y[-1]), xycoords='data', arrowprops=dict(arrowstyle="->", connectionstyle="arc3", color=cm(8/8)))
    
def diffusivityDistance(smooth, binned, fig=0, ax=0, i=-1):
    p = inf.getInputParameters()
    if binned:
        d = inf.readBinnedAverages()
    else:
        d = inf.readAverages()

    cove = d.getRecomputedCoverage()/p.sizI/p.sizJ
    ratios = p.getRatios()
    Na = cove * p.sizI * p.sizJ

    innerFig = fig == 0 and ax == 0
    if innerFig:
        fig = plt.figure(num=33, figsize=(5,7))
        ax = plt.gca()
        
    x = list(range(0,len(d.time)))
    x = cove
    cm = plt.get_cmap("Accent")
    alpha = 0.5
    mew = 0
    diff = fun.timeDerivative(d.diff, d.time)/(4*Na)
    handles = []
    lgR, = ax.loglog(x, diff, label=r"$\frac{1}{2dN_a} \; \frac{d\langle R^2 \rangle}{dt}$",
               marker="s", ls="", mew=mew, markerfacecolor=cm(0/8), ms=8, alpha=alpha)
    hops = fun.timeDerivative(d.hops, d.time)/(4*Na)
    lgN, = ax.loglog(x, hops, label=r"$\frac{l^2}{2dN_a} \; \frac{d\langle N_h\rangle}{dt}$",
               marker="+", ls="", mew=1, markeredgecolor=cm(7/8), ms=7, alpha=alpha)
    lgR3, = ax.loglog(x, d.diff/d.time/(4*Na), label=r"$\frac{1}{2dN_a} \; \frac{\langle R^2\rangle}{t}$",
                      ls="-", color=cm(3/8), lw=2)
    lgN3, = ax.loglog(x, d.hops/d.time/(4*Na), label=r"$\frac{l^2}{2dN_a} \; \frac{\langle N_h\rangle}{t}$",
                      ls=":", color=cm(4.1/8), lw=1.8)

    k=0
    label = r"$\theta_0$"
    lg, = ax.loglog(x, d.negs[k]/p.sizI/p.sizJ, label=label, ms=1, lw=2, ls="-.", color=cm(1/8))
    if smooth:
        ySmooth = np.exp(savgol_filter(np.log(d.negs[k]), 9, 1))
        ax.loglog(x, ySmooth/p.sizI/p.sizJ, lw=2)
        d.negs[k] = ySmooth
    handles.append(lg)
    isld = d.isld
    lg, = ax.loglog(x, isld/p.sizI/p.sizJ, ls="--", lw=2, color=cm(6/8), label=r"$N_{isl}$", markerfacecolor="None")
    handles.append(lg)

    handles = [ lgR3, lgN3, lgR, lgN] + handles
    ax.grid()
    ax.set_xlabel(r"$\theta$", size=16)
    ax.set_ylim([1e-7,1e13])
    ax.set_xlim([1e-5,1e0])
    addFreeDiffusivity(ax, x, p)
    if innerFig:
        ax.legend(handles=handles, loc=(0.46,0.3), numpoints=1, prop={'size':15}, markerscale=2)
        addSurface(p.temp)
        fig.savefig("../../../plot"+str(p.flux)+str(p.temp)+".png")
        plt.close(33)
    else:
        addSurface(p.temp, ax)
        if i == 0:
            ax.legend(handles=handles, loc=(0.46,0.27), numpoints=1, prop={'size':13}, markerscale=1, labelspacing=0.4)
        if i > 0:
            xlim = (7e-1,1)
            if i == 1:
                rect = Rectangle((7e-1, 1e1), 30, 1e6, facecolor="white", edgecolor=cm(8/8))
                ax.add_patch(rect)
            if i == 2:
                rect = Rectangle((7e-1, 1e3), 30, 1e7, facecolor="white", edgecolor=cm(8/8))
                ax.add_patch(rect)
            position = [0.12, 0.27, 0.105, 0.22]
            position[0:2] += ax.get_position().get_points().reshape(4)[0:2]
            newax = plt.gcf().add_axes(position, zorder=+100)
            newax.loglog(x, diff, label=r"$\frac{1}{2dN_a} \; \frac{d(R^2)}{dt}$",
                             marker="s", ls="", mew=mew, markerfacecolor=cm(0/8), ms=8, alpha=alpha)
            newax.loglog(x, hops, label=r"$\frac{l^2}{2dN_a} \; \frac{d(N_h)}{dt}$",
               marker="+", ls="", mew=1, markeredgecolor=cm(7/8), ms=7, alpha=alpha)
            newax.loglog(x, d.diff/d.time/(4*Na), label=r"new",
                      color=cm(3/8), lw=2)
            newax.loglog(x, d.hops/d.time/(4*Na), "--", label=r"new",
                      color=cm(4.1/8), lw=1.8)
            newax.xaxis.set_major_formatter(plticker.NullFormatter())
            newax.yaxis.set_major_formatter(plticker.NullFormatter())
            if i == 1:
                ax.annotate("",xy=(7e-1,1e1), arrowprops=dict(arrowstyle="-", connectionstyle="arc3", color=cm(8/8)),
                            xytext=(3e-3,1.5e-1))
                ax.annotate("",xy=(1,1e1), arrowprops=dict(arrowstyle="-", connectionstyle="arc3", color=cm(8/8)),
                            xytext=(3.8e-1,1.3e-1))
                #ax.annotate("",xy=(1,1e6), arrowprops=dict(arrowstyle="-", connectionstyle="arc3", color=cm(8/8)),
                #            xytext=(3.8e-1,2e4))
                ax.annotate("",xy=(7e-1,1e6), arrowprops=dict(arrowstyle="-", connectionstyle="arc3", color=cm(8/8)),
                            xytext=(2.8e-3,2e4))
                newax.set_xlim(7e-1,1)
                newax.set_ylim(1,1e6)
            if i == 2:
                ax.annotate("",xy=(7e-1,1e3), arrowprops=dict(arrowstyle="-", connectionstyle="arc3", color=cm(8/8)),
                            xytext=(3e-3,1.5e-1))
                ax.annotate("",xy=(1,1e3), arrowprops=dict(arrowstyle="-", connectionstyle="arc3", color=cm(8/8)),
                            xytext=(3.8e-1,1.3e-1))
                ax.annotate("",xy=(7e-1,1e7), arrowprops=dict(arrowstyle="-", connectionstyle="arc3", color=cm(8/8)),
                            xytext=(2.8e-3,2e4))
                newax.set_xlim(7e-1,1)
                newax.set_ylim(1e3,3e7)


##########################################################
##########           Main function   #####################
##########################################################

try:
    smooth = sys.argv[1] == "y"
except IndexError:
    smooth = False
try:
    binned = sys.argv[2] == "y"
except IndexError:
    binned = False

workingPath = os.getcwd()
fluxes = inf.getFluxes()
i = 0
for f in fluxes:
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fig1, axarr = plt.subplots(1, 3, sharey=True,figsize=(15,7))
    fPath = os.getcwd()
    for t in [150, 250, 750]:#inf.getTemperatures()[14:]:
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            #diffusivityDistance(smooth, binned)
            if t == 150 or t == 250 or t == 750:
                fig1.subplots_adjust(top=0.95, bottom=0.08, wspace=0.08)
                diffusivityDistance(smooth, binned, fig=fig1, ax=axarr[i], i=i)
                i += 1
                fig1.savefig("../../../figures.pdf", bbox_inches='tight')
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    os.chdir(workingPath)

