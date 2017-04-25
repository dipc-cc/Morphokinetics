#grep -v histo dataEvery1percentAndNucleation.txt | grep -v Ae | awk -v n=-1 '{if ($1<prev) {n++}prev=$1;} {print > "data"n".txt"} END{print n}'
# sed -i '1d' data0.txt
import info as inf
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.ticker as plticker
import matplotlib.lines as mlines
from matplotlib.patches import Rectangle
from matplotlib.ticker import LogLocator
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
            position = [-0.1, 0.3, 0.3, 0.3]
            position[0:2] += ax.get_position().get_points().reshape(4)[0:2]
        newax = plt.gcf().add_axes(position, zorder=+100)
        newax.imshow(im)
        newax.yaxis.set_major_locator(plticker.NullLocator())
        newax.xaxis.set_major_locator(plticker.NullLocator())
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.9)
        newax.annotate(r"$\theta = 0.30$", xy=(0.5,0.85), xycoords="axes fraction",
                       ha="center", va="center", bbox=bbox_props)
        #newax.axis('off')
    except IndexError:
        pass

def addFreeDiffusivity(ax, x, p):
    sublabel = {150: "(a)", 250: "(b)", 750: "(c)"}
    try:
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.9)
        if p.temp == 150:
            note = "$F=$".format(int(p.temp))+fun.base10(p.flux)+"$ML/s$\n${}".format(p.sizI)+r"\times{}$".format(p.sizJ)
            ax.annotate(note, xy=(0.06,0.85), xycoords="axes fraction", size=14, bbox=bbox_props)
        note = sublabel[p.temp]+" ${} K$".format(int(p.temp))
        ax.annotate(note, xy=(0.75,0.9), xycoords="axes fraction", size=14, bbox=bbox_props)
    except KeyError:
        pass
    x = x[0:20]
    y = np.ones(len(x))
    y = y * 3/2*p.getRatios()[0]
    cm = plt.get_cmap("Accent")
    y = y / 2
    ax.plot(x, y, "-", color=cm(8/8))
    ax.annotate(r"$\frac{3}{4}\nu_{0}l^2$", xytext=(1e-2,5e10), textcoords="data",
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
    handles = []
    lgE, = ax.loglog(x, d.diff/d.hops*1e9, label="$f_T\cdot10^9$",
                     marker="o", ms=1, ls="", mec=cm(3/20), mfc=cm(3/20), lw=2)
    lgR3, = ax.loglog(x, d.diff/d.time/(4*Na), label=r"$g \; \frac{\langle R^2\rangle}{t}$",
                      ls="-", color=cm(3/8), lw=5)
    lgN3, = ax.loglog(x, d.hops/d.time/(4*Na), label=r"$gl^2 \; \frac{\langle N_h\rangle}{t}$",
                      ls=":", color=cm(4.1/8), lw=4)

    lg, = ax.loglog(x, (np.sum(d.negs[0:7],axis=0))/p.sizI/p.sizJ*1e5, ms=1, lw=1, ls="-.", color="black", label=r"$\theta \cdot 10^5$")#, marker=markers[4])
    markers=["o", "s","D","^","d","h","p","o"]
    cm1 = plt.get_cmap("Set1")
    bbox_props = dict(boxstyle="round", fc="w", ec="1", alpha=0.7, pad=0.1)
    for k in range(0,4):
        label = r"$\theta_"+str(k)+r"\cdot 10^4$"
        ax.loglog(x, d.negs[k]/p.sizI/p.sizJ*1e5, label=label, ms=3, lw=1, ls="-", color=cm1(k/8))# marker=markers[k])
        #ax.text(x[1],1e1,"text")
        index = np.where(d.negs[k] > 0)[0][2]
        ax.text(x[index],d.negs[k][index]/p.sizI/p.sizJ*1e5, r"$\theta_{"+str(k)+r"}$", color=cm1(k/8), bbox=bbox_props)
    index = np.where(d.negs[4] > 0)[0][2]
    ax.text(x[index],d.negs[4][index]/p.sizI/p.sizJ*1e5, r"$\theta_{4+}$",color=cm1(7/8), bbox=bbox_props)
    ax.loglog(x, (d.negs[4]+d.negs[5]+d.negs[6])/p.sizI/p.sizJ*1e5, label=label, ms=1, lw=1, ls="-", color=cm1(7/8))#, marker=markers[4])
        
    if smooth:
        ySmooth = np.exp(savgol_filter(np.log(d.negs[k]), 9, 1))
        ax.loglog(x, ySmooth/p.sizI/p.sizJ, lw=2)
        d.negs[k] = ySmooth
    handles.append(lg)
    isld = d.isld
    lg, = ax.loglog(x, isld/p.sizI/p.sizJ*1e5, ls="--", lw=2, color=cm(6/8), label=r"$N_{isl}\cdot 10^5$", markerfacecolor="None")
    handles.append(lg)

    handles = [ lgR3, lgN3, lgE] + handles
    #ax.grid()
    ax.set_xlabel(r"$\theta$", size=16)
    ax.set_ylim([1e-2,1e13])
    ax.set_xlim([1e-5,1e0])
    ax.yaxis.set_major_locator(LogLocator(100,[1e-2]))
    addFreeDiffusivity(ax, x, p)
    if innerFig:
        ax.legend(handles=handles, loc=(0.46,0.3), numpoints=1, prop={'size':15}, markerscale=2)
        #addSurface(p.temp)
        fig.savefig("../../../plot"+str(p.flux)+str(p.temp)+".png")
        plt.close(33)
    else:
        addSurface(p.temp, ax)
        if i == 2:
            thetas = mlines.Line2D([], [], color='black',
                                   markersize=15, label=r"$\theta_{i} \cdot 10^5$"+"\n"+r"$i = 0,1,2,3,4+$")
            handles.append(thetas)
            ax.legend(handles=handles, loc=(1.05,.15), numpoints=4, prop={'size':12}, markerscale=1, labelspacing=1, ncol=1, columnspacing=.7, borderpad=0.3)
            #                                    -.15/1.03
        if i > 2:
            xlim = (7e-1,1)
            if i == 1:
                rect = Rectangle((7e-1, 1e1), 30, 1e6, facecolor="white", edgecolor=cm(8/8))
                ax.add_patch(rect)
            if i == 2:
                rect = Rectangle((7e-1, 1e3), 30, 1e7, facecolor="white", edgecolor=cm(8/8))
                ax.add_patch(rect)
            ax.annotate("", xy=(3e-2,1e5), xytext=(6e-1,1e5), arrowprops=dict(arrowstyle="->", connectionstyle="angle3", color=cm(8/8)))
            position = [0.1, 0.3, 0.06, 0.3]
            position[0:2] += ax.get_position().get_points().reshape(4)[0:2]
            newax = plt.gcf().add_axes(position, zorder=+100)
            newax.loglog(x, diff, label=r"$g \; \frac{d(R^2)}{dt}$",
                             marker="s", ls="", mew=mew, markerfacecolor=cm(0/8), ms=8, alpha=alpha)
            newax.loglog(x, hops, label=r"$gl^2 \; \frac{d(N_h)}{dt}$",
               marker="+", ls="", mew=1, markeredgecolor=cm(7/8), ms=7, alpha=alpha)
            newax.loglog(x, d.diff/d.time/(4*Na), label=r"new",
                      color=cm(3/8), lw=2)
            newax.loglog(x, d.hops/d.time/(4*Na), "--", label=r"new",
                      color=cm(4.1/8), lw=1.8)
            newax.xaxis.set_major_formatter(plticker.NullFormatter())
            newax.yaxis.set_major_formatter(plticker.NullFormatter())
            arrow = dict(arrowstyle="-", connectionstyle="arc3", color=cm(8/8))
            x1Big = 7e-1
            if i == 1:
                ax.annotate("",xy=(x1Big,1e1), arrowprops=arrow,
                            xytext=(3e-3,1.5e-1))
                ax.annotate("",xy=(1,1e1), arrowprops=arrow,
                            xytext=(3.8e-1,1.3e-1))
                #ax.annotate("",xy=(1,1e6), arrowprops=arrow,
                #            xytext=(3.8e-1,2e4))
                ax.annotate("",xy=(x1Big,1e6), arrowprops=arrow,
                            xytext=(2.8e-3,2e4))
                newax.set_xlim(x1Big,1)
                newax.set_ylim(1,1e6)
            if i == 2: 
                #ax.annotate("",xy=(x1Big,1e3), arrowprops=arrow,
                #            xytext=(3e-3,1.5e-1))
                #ax.annotate("",xy=(1,1e3), arrowprops=arrow,
                #            xytext=(3.8e-1,1.3e-1))
                #ax.annotate("",xy=(x1Big,1e7), arrowprops=arrow,
                #            xytext=(2.8e-3,2e4))
                newax.set_xlim(x1Big,1)
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
    fig1, axarr = plt.subplots(1, 3, sharey=True,figsize=(15,4))
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

