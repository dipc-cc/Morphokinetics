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


def annotate(ax):
    ax.set_yscale("log")
    ax.set_xlabel(r"$1/k_BT$")
    ax.set_ylabel(r"$\log (D_T)$")
    ax.set_xlim(-10,120)
    ax.set_ylim(1e0,1e5)
    bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=1)
    ax.annotate("", xy=(29,1e0), xycoords='data', xytext=(29, 1e5),
                arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
    ax.annotate("", xy=(78,1e0), xycoords='data', xytext=(78, 1e5),
                arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
    y = 2e0
    ax.text(100, y, "$I$", color="gray")
    ax.text(50, y, "$II$", color="gray")
    ax.text(18, y, "$III$", color="gray")

    
def plot(ax, data, i):
    cm = plt.get_cmap("Accent")
    alpha = 0.5
    mew = 0
    data = np.array(data)
    try:
        flux = data[0,0]
    except IndexError:
        raise

    x = 1/kb/data[:,1]
    lw = 3
    lg1, = ax.plot(x, data[:,3], label=r"$F=$"+fun.base10(flux),
                   lw=lw, marker="", ls="-", mew=mew, ms=12, alpha=alpha, color=cm(i/8))
    ax.plot(x, data[:,4], label=r"$N_h$"+fun.base10(flux),
            lw=lw+1, ls="-.", mew=1, color=cm(i/8), ms=7, alpha=1)
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
            ax.text(xHalf,yHalf, text, color="black", ha="center", va="center", size=10, zorder=+400)
        i = 0 # print "interpolation" to the rest of lower x values
        ax.semilogy(x[0:rngt[i+1]+1], np.exp(fun.linear(x[0:rngt[i+1]+1], a, b)), ls=":", color="lightgray", zorder=+300)
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
        
    return lg1

workingPath = os.getcwd()
fluxes = inf.getFluxes()
i = 0
coverage = 30
kb = 8.617332e-5
fig = plt.figure(num=None, figsize=(6,4))
ax = fig.gca()
legends = annotate(ax)
f = fluxes[3]
if True:

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
        plot(ax, data, i)
    except IndexError:
        os.chdir(workingPath)
    fig.savefig("../diffusivityTimeSimpleTOC.pdf", bbox_inches='tight')
    os.chdir(workingPath)
