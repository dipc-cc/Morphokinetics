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


def annotate(ax, ax2):
    ax.set_yscale("log")
    ax.set_xlabel(r"$1/k_BT + ln(F^{1.5})$")
    ax.set_xlim(0,250)
    ax.set_ylim(1e-3,1e4)
    ax2.set_ylim(0.88,1.2)
    bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=1)
    label = r"$\theta=0.60$"
    ax.annotate(label, xy=(0.015,0.55), xycoords="axes fraction",
                bbox=bbox_props, zorder=+100)
    ax.annotate("", xy=(45,1e-3), xycoords='data', xytext=(45, 1e4),
                arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
    ax.annotate("", xy=(94,1e-3), xycoords='data', xytext=(94, 1e4),
                arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
    y = 2e-3
    ax.text(120, y, "$I$", color="gray")
    ax.text(60, y, "$II$", color="gray")
    ax.text(25, y, "$III$", color="gray")
    legends = []
    rLg = mlines.Line2D([], [], color='black', ls="-", lw=4,
                        markersize=10, label=r"$g\; \frac{\langle R^2\rangle}{t F^{0.7}}$")
    nLg = mlines.Line2D([], [], color='black', ls="-.", lw=3,
                        markersize=8, label=r"$gl^2\; \frac{\langle N_h\rangle}{t F^{0.7}}$")
    iLg = mlines.Line2D([], [], color='black', ls='--',
                        markersize=8, label=r"$N_{isl}$")
    FLg = mlines.Line2D([], [], color="lightgray", ls='-',
                        markersize=8, label=r"$Fit$")
    fLg = mlines.Line2D([], [], color="black", ls=":",
                        markersize=8, label=r"$f_T$")
    legends.append(rLg)
    legends.append(nLg)
    legends.append(FLg)
    legends.append(fLg)
    return legends

    
def annotateBasic(ax, ax2):
    ax.set_xlim(30,120)
    ax.set_ylim(1e-2,1e3)
    ax.set_yscale("log")
    ax.set_xlabel(r"$1/k_BT + ln(F^{1.78})$", size=8)
    ax2.set_ylim(0.6,1.2)
    inf.smallerFont(ax, 8)
    inf.smallerFont(ax2, 6)
    ax.annotate("", xy=(45,1e-2), xycoords='data', xytext=(45, 1e3), 
                arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
    ax.annotate("", xy=(63,1e-2), xycoords='data', xytext=(63, 1e3),
                arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
    y = 2e-2
    ax.text(70, y, "$I$", color="gray", size=8)
    ax.text(50, y, "$II$", color="gray", size=8)
    ax.text(35, y, "$III$", color="gray", size=8)
    legends = []
    rLg = mlines.Line2D([], [], color='black', ls="-", lw=4,
                        markersize=10, label=r"$g\; \frac{\langle R^2\rangle}{t F^{0.8}}$")
    nLg = mlines.Line2D([], [], color='black', ls="-.", lw=3,
                        markersize=8, label=r"$gl^2\; \frac{\langle N_h\rangle}{t F^{0.8}}$")
    iLg = mlines.Line2D([], [], color='black', ls='--',
                        markersize=8, label=r"$N_{isl}$")
    fLg = mlines.Line2D([], [], color='lightgray', ls='-',
                        markersize=8, label=r"$Fit$")
    legends.append(rLg)
    legends.append(nLg)
    legends.append(fLg)
    alpha = 0.3
    return legends, alpha

    
def plot(ax, ax2, data, i, alp=1):
    marker = ["o", "s","H","D","^","d","h","p","o"]
    cm = plt.get_cmap("Accent")
    alpha = 0.5
    mew = 0
    data = np.array(data)
    try:
        flux = data[0,0]
    except IndexError:
        raise
    factor = data[0,6]

    x = 1/kb/data[:,1]+np.log(flux**factor)
    lw = 3
    lg1, = ax.plot(x, data[:,3], label=r"$F=$"+fun.base10(flux),
                   lw=lw, marker="", ls="-", mew=mew, ms=12, alpha=alpha, color=cm(i/8))
    ax.plot(x, data[:,4], label=r"$N_h$"+fun.base10(flux),
            lw=lw+1, ls="-.", mew=1, color=cm(i/8), ms=7, alpha=1)
    ax2.plot(x, data[:,3]/data[:,4], label="aa", color=cm(i/8), ls="", marker="o", ms=2, mec=cm(i/8), mfc=cm(i/8), alpha=alp)
    arrow = dict(arrowstyle="->", connectionstyle="arc3", ls="-", color=cm(i/8))
    if str(flux)[0] == "5":
        ax.annotate(fun.base10(flux), xy=(x[2], data[:,4][2]), color=cm(i/8),
                    xytext=(0.9,0.52-0.05*i), textcoords="axes fraction", arrowprops=arrow)
    if flux == 5e4:
        #Fit
        rngt = inf.defineRanges("AgUc","simple", data[:,1])
        for i in range(len(rngt)-2,-1,-1):
            y = data[:,4]
            a, b = fun.linearFit(x, y, rngt[i], rngt[i+1])
            ax.semilogy(x[rngt[i]:rngt[i+1]+1], np.exp(fun.linear(x[rngt[i]:rngt[i+1]+1], a, b)), ls="-", color="lightgray", zorder=+200)
            xHalf = (x[rngt[i]]+x[rngt[i+1]]+1)/2
            text = "{:03.3f}".format(-b)
            yHalf = np.exp(fun.linear(xHalf, a, b))*10
            text = r"$"+text+r"$"
            ax.text(xHalf,yHalf, text, color="black", ha="center", va="center", size=10, zorder=+400)
        i = 0 # print "interpolation" to the rest of lower x values
        ax.semilogy(x[0:rngt[i+1]+1], np.exp(fun.linear(x[0:rngt[i+1]+1], a, b)), ls=":", color="lightgray", zorder=+300)
        #Fit f
        for i in range(len(rngt)-2,-1,-1):
            y = data[:,3]/data[:,4]
            a, b = fun.linearFit(x, y, rngt[i], rngt[i+1])
            ax2.plot(x[rngt[i]:rngt[i+1]+1], np.exp(fun.linear(x[rngt[i]:rngt[i+1]+1], a, b)), ls="-", color="lightgray", zorder=+200)
            xHalf = (x[rngt[i]]+x[rngt[i+1]]+1)/2
            text = "{:03.3f}".format(-b)
            yHalf = np.exp(fun.linear(xHalf, a, b))*1.02
            text = r"$"+text+r"$"
            ax2.text(xHalf,yHalf, text, color="gray", ha="center", va="center", size=10, zorder=+400)
        i = 0 # print "interpolation" to the rest of lower x values
        ax2.plot(x[0:rngt[i+1]+1], np.exp(fun.linear(x[0:rngt[i+1]+1], a, b)), ls=":", color="lightgray", zorder=+300)
    if flux == 3.5e0:
        rngt = inf.defineRanges("basic","simple", data[:,1])
        for i in range(len(rngt)-2,-1,-1): #reverse iteration
            y = data[:,4]
            a, b = fun.linearFit(x, y, rngt[i], rngt[i+1])
            ax.semilogy(x[rngt[i]:rngt[i+1]+1], np.exp(fun.linear(x[rngt[i]:rngt[i+1]+1], a, b)), ls="-", color="lightgray", zorder=+200)
            xHalf = (x[rngt[i]]+x[rngt[i+1]]+1)/2
            text = "{:03.3f}".format(-b)
            yHalf = np.exp(fun.linear(xHalf, a, b))*10
            text = r"$"+text+r"$"
            ax.text(xHalf,yHalf, text, color="black", ha="center", va="center", size=10, zorder=+400)
        # print "interpolation" to the rest of lower x values
        ax.semilogy(np.arange(120,70,-1), np.exp(fun.linear(np.arange(120,70,-1), a, b)), ls=":", color="lightgray", zorder=+300)
        #Fit f
        for i in range(len(rngt)-2,-1,-1): #reverse iteration
            y = data[:,3]/data[:,4]
            a, b = fun.linearFit(x, y, rngt[i], rngt[i+1])
            ax2.plot(x[rngt[i]:rngt[i+1]+1], np.exp(fun.linear(x[rngt[i]:rngt[i+1]+1], a, b)), ls="-", color="lightgray", zorder=+200)
            xHalf = (x[rngt[i]]+x[rngt[i+1]]+1)/2
            text = "{:03.3f}".format(-b)
            yHalf = np.exp(fun.linear(xHalf, a, b))*1.02
            text = r"$"+text+r"$"
            ax2.text(xHalf,yHalf, text, color="gray", ha="center", va="center", size=7, zorder=+400)
        
    return lg1

workingPath = os.getcwd()
fluxes = inf.getFluxes()
i = 0
coverage = 60
kb = 8.617332e-5
fig = plt.figure(num=None, figsize=(6,4))
ax = fig.gca()
twinx = ax.twinx()
legends = annotate(ax,twinx)
i = 0
f = "flux5e4"
for i,f in enumerate(fluxes):
    os.chdir(workingPath)
    if i<-1:
        continue
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    data = []
    for t in inf.getTemperatures():
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            p, d = read()
            flux, cove, diff, hops, isld, fact = inf.getAvgDataCoverage(p, d, coverage)
            data.append([flux, t, cove, diff, hops, isld, fact])
        except (TypeError, FileNotFoundError):
            pass
        os.chdir(fPath)
    ### Plot
    try:
        plot(ax, twinx, data, i)
    except IndexError:
        print("index error")
        os.chdir(workingPath)
        continue
    ax.legend(handles=legends, loc=(0.25,0.1),#bbox_to_anchor=(0.75, 0.32),
              bbox_transform=plt.gcf().transFigure, numpoints=1, ncol=2, prop={'size':8}, markerscale=1)
    fig.savefig("../diffusivityTimeSimple.pdf", bbox_inches='tight')
    os.chdir(workingPath)

workingPath = "/home/jalberdi004/mk_test/activationEnergy/basic/400/23.-newDiffusivity"
os.chdir(workingPath)
fluxes = inf.getFluxes()
ax2 = fig.add_axes([0.5,0.6,0.35,0.35])
twinx = ax2.twinx()
legends, alpha = annotateBasic(ax2,twinx)
for i,f in enumerate(fluxes):
    os.chdir(workingPath)
    if i<-1:
        continue
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    data = []
    for t in inf.getTemperatures():
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            p, d = read()
            flux, cove, diff, hops, isld, fact = inf.getAvgDataCoverage(p, d, coverage)
            data.append([flux, t, cove, diff, hops, isld, fact])
        except (TypeError, FileNotFoundError):
            pass
        os.chdir(fPath)
    ### Plot
    try:
        plot(ax2, twinx, data, i, alpha)
    except IndexError:
        continue
    fig.savefig("../diffusivityTimeSimple"+str(coverage)+".pdf", bbox_inches='tight')
