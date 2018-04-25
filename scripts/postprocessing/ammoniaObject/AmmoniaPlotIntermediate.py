import matplotlib.pyplot as plt
import AmmoniaPlot as ap

import pdb

class AmmoniaPlotIntermediate:
    
    
    def __init__(self, x, prod, sp, total, one):
        self.ap = ap.AmmoniaPlot()
        self.showPlot = sp
        if not self.showPlot:
            return
        self.x = x
        self.prod = prod # Production of NO + N2
        self.markers=["o", "s","D","^","d","h","p"]
        self.cm = plt.get_cmap('tab20')
        self.total = total
        self.axarr = list()
        self.one = one
        if total and one:
            self.out = "T1.svg"
            self.ymin = 1e-10
            self.fig, ax = plt.subplots(1, sharex=True, figsize=(5,4))
            self.axarr.append(0); self.axarr.append(ax)
        else:
            self.minM = 1e4
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
        if not self.one:
            y = [float("NaN") if v < 1e-50 else v for v in y] # remove almost 0 multiplicities
            self.axarr[0].scatter(self.x, y, color=self.cm(abs((alfa%20)/20)), alpha=0.75, edgecolors=self.ap.getMec(alfa), marker=self.markers[alfa%7])
            arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
            if min(y) < self.minM:
                self.minM = min(y)


    def plotOmegas(self, y, i, labelAlfa):
        if not self.showPlot:
            return
        self.axarr[1].semilogy(self.x, y, ls="",color=self.cm(abs((i%20)/20)), label=labelAlfa[i], marker=self.markers[i%7], mec=self.ap.getMec(i), alpha=0.75)

    def flush(self, omegaSumTof):
        if not self.showPlot:
            return
        if self.one:
            self.axarr[1].plot(self.x,omegaSumTof,ls="-", label=r"TOF/R", color="C2")
            self.axarr[1].plot(self.x,2*omegaSumTof, ls=":", label=r"2 $\times$ TOF/R", color="C2")
            self.axarr[1].plot(self.x,0.05*omegaSumTof, ls="--", label=r" 0.05$ \times $ TOF/R", color="C2")
            self.axarr[1].legend(prop={'size': 5}, loc="best", scatterpoints=1) 
        else:
            self.__smallerFont(self.axarr[0], 8)
        self.__smallerFont(self.axarr[1], 8)
        self.__putLabels()
        self.fig.savefig("../../../plot"+str(self.prod)+self.out)
        plt.close(self.fig)

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
        self.axarr[1].set_ylabel(r"$\omega^{"+rl+r"}_\alpha$")
        self.axarr[1].set_xlabel(r"$1/k_BT$")
        arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
        self.axarr[1].legend(prop={'size': 5}, loc="best", scatterpoints=1)
