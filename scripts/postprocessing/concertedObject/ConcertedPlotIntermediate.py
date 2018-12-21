import matplotlib.pyplot as plt
import multiplicitiesPlot as mp
import ConcertedPlot as cp
import numpy as np

import pdb

class ConcertedPlotIntermediate:
    
    
    def __init__(self, x, moment, prod, sp, total, one):
        self.showPlot = sp
        if not self.showPlot:
            return
        self.cp = cp.ConcertedPlot()
        self.x = x
        self.moment = moment # zenbat aldiz deitu den
        self.prod = prod # coverage
        self.markers=["o", "s","D","^","d","h","p"]
        self.cm = plt.get_cmap('tab20')
        self.total = total
        self.axarr = list()
        self.one = one
        if total or one:
            self.out = "T1.svg"
            self.ymin = 1e-3
            self.fig, ax = plt.subplots(1, sharex=True, figsize=(6,3.5))
            self.fig.subplots_adjust(top=0.88,left=0.15,right=0.8,bottom=0.15)
            self.axarr.append(0); self.axarr.append(ax)
        else:
            self.minM = 1e3
            self.out = ".svg"
            if total:
                self.out = "T.svg"
            self.ymin = 1e-4
            self.fig, ax = plt.subplots(2, sharex=True, figsize=(5,4))
            for i in range(0,len(ax)):
                self.axarr.append(ax[i])


    def plotLinear(self, y, alfa, verbose=False):
        if not self.showPlot:
            return
        if alfa < 0:
            return
        if not self.one and any(abs(self.omega) >= self.ymin):
            y = [float("NaN") if v < 1e-50 else v for v in y] # remove almost 0 multiplicities
            self.axarr[0].scatter(self.x, y, color=self.cm(abs((alfa%20)/20)), alpha=0.75, edgecolors=self.cp.getMec(alfa), marker=self.markers[alfa%7])
            arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
            if min(y) < self.minM:
                self.minM = min(y)


    def plotOmegas(self, y, i, labelAlfa):
        if not self.showPlot:
            return
        self.omega = y
        if any(abs(y) >= self.ymin):
            self.axarr[1].semilogy(self.x, y, ls="",color=self.cm(abs((i%20)/20)), label=labelAlfa[i], marker=self.markers[i%7], mec=self.cp.getMec(i), alpha=0.75)

    def flush(self, omegaSumTof):
        if not self.showPlot:
            return
        if self.one:
            self.axarr[1].legend(bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
            ax2 = mp.setY2TemperatureLabels(self.axarr[1],8.617332e-5, majors=np.array([25, 50, 75, 100, 200, 1000]))
            self.__smallerFont(ax2, 10)
        else:
            self.__smallerFont(self.axarr[0], 8)
        self.__smallerFont(self.axarr[1], 14)
        self.__putLabels()
        self.fig.savefig("../../../plot"+str(self.moment)+"_"+str(self.prod)+self.out)
        plt.close(self.fig)

    def saveOmegas(self, y, i, cov):
        np.savetxt("../../../omegas/omegas_"+"{:03d}".format(i)+"_{:03d}".format(cov)+".txt", y)
        
    def __smallerFont(self, ax, size=10):
        ax.tick_params(axis='both', which='major', labelsize=size)
        for tick in ax.xaxis.get_major_ticks():
            tick.label.set_fontsize(size)
        for tick in ax.yaxis.get_major_ticks():
            tick.label.set_fontsize(size)
            
    def __putLabels(self):
        if self.total:
            rl = "R"
        else:
            rl = "TOF"
        if not self.one:
            self.axarr[0].set_yscale("log")
            self.axarr[0].set_ylim(self.minM,10)
            self.axarr[0].set_ylabel(r"$M^{"+rl+r"}_\alpha$")
        self.axarr[1].set_ylim(self.ymin,2)
        self.axarr[1].set_ylabel(r"$\omega^{"+rl+r"}_\alpha$", size=14)
        self.axarr[1].set_xlabel(r"$1/k_BT$", size=14)
        arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
        #self.axarr[1].legend(prop={'size': 5}, loc="best", scatterpoints=1)
        self.axarr[1].legend(prop={'size': 7},bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
