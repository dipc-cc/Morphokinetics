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
    

def plot(ax, ax2, data, i):
    marker = ["o", "s","H","D","^","d","h","p","o"]
    cm = plt.get_cmap("Accent")
    alpha = 0.5
    mew = 0
    data = np.array(data)
    flux = data[0,0]
    x = data[:,6]/flux**0.17
    if ax2 != None:
        ax2.plot(x, data[:,5], label="isld",
                        color=cm(i/8))
        ax2.plot(x, fun.power(x, 4e4, -0.3333))
    lg1, = ax.plot(x, data[:,3], label=r"$F=$"+fun.base10(flux),
            marker=marker[i], ls="", mew=mew, ms=8, alpha=alpha, markerfacecolor=cm(i/8))
    ax.plot(x, data[:,4], label=r"$N_h$"+fun.base10(flux),
            marker="+", ls="--", mew=1, color=cm(i/8), markeredgecolor=cm(i/8), ms=7, alpha=1)
    if i == 4:
        bbox_props = dict(boxstyle="round", fc="w", ec="0.5", alpha=0.3)
        label = r"$\theta=30\%$"
        ax.annotate(r"$1/3$", xy=(3e2,2e2),
                    bbox=bbox_props)
        ax.annotate(label, xy=(0.2,0.9), xycoords="axes fraction",
                    bbox=bbox_props)
        ax.annotate("", xy=(1e8,1e-5), xycoords='data', xytext=(1e8, 1e4),
                    arrowprops=dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray"))
        ax.annotate("", xy=(5e10,1e-5), xycoords='data', xytext=(5e10, 1e4),
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
ax.set_xscale("log")
ax.set_yscale("log")
ax2.set_yscale("log")
ax.set_xlabel(r"$r_{tt}/F^{0.17}$")
ax.set_ylabel(r"$R^2/(t F^{0.7})$,  $N_h/(t F^{0.7})$")
ax2.set_ylabel("isld")
ax3 = ax.twiny()
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
    fig.savefig("../diffusivityTimeIslands.pdf", bbox_inches='tight')
    os.chdir(workingPath)
