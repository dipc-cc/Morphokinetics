import matplotlib.pyplot as plt

import pdb

class AmmoniaPlotIntermediate:
    
    
    def __init__(self, prod, sp):
        self.showPlot = sp
        if not self.showPlot:
            return
        self.fig, self.axarr = plt.subplots(2, sharex=True, figsize=(5,4))
        self.prod = prod # Production of NO + N2
        self.markers=["o", "s","D","^","d","h","p"]
        self.cm = plt.get_cmap('tab20')


    def plotLinear(self, x, y, alfa, co2, verbose=False):
        if not self.showPlot:
            return
        if alfa < 0:
            return
        if self.showPlot:
            y = [float("NaN") if v < 1e-50 else v for v in y] # remove almost 0 multiplicities
            self.axarr[0].scatter(x, y, color=self.cm(abs((alfa%20)/20)), alpha=0.75, edgecolors=self.getMec(alfa), marker=self.markers[alfa%7])
            arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
            a = alfa


    def plotOmegas(self, x, y, i, averageLines, labelAlfa):
        if not self.showPlot:
            return
        self.axarr[1].semilogy(x, y, ls="",color=self.cm(abs((i%20)/20)), label=labelAlfa[i], marker=self.markers[i%7], mec=self.getMec(i), alpha=0.75)
    

    def flush(self):
        if not self.showPlot:
            return
        self.__smallerFont(self.axarr[0], 8)
        self.__smallerFont(self.axarr[1], 8)
        self.__putLabels()
        self.fig.savefig("../../../fig"+str(self.prod)+".png")
        plt.close(self.fig)

    def getMec(self, i):
        if i  == 8 or i == 9:
            mec = "black"
        else:
            mec = "none"
        return mec

    def __smallerFont(self, ax, size=10):
        ax.tick_params(axis='both', which='major', labelsize=size)
        for tick in ax.xaxis.get_major_ticks():
            tick.label.set_fontsize(size)
        for tick in ax.yaxis.get_major_ticks():
            tick.label.set_fontsize(size)
            
    def __putLabels(self):
        ymin = 1e-4
        rl = ""
        self.axarr[0].set_ylabel(r"$M^{"+rl+r"}_\alpha$")
        self.axarr[1].set_ylim(ymin,2)
        self.axarr[1].set_ylabel(r"$\omega^{"+rl+r"}_\alpha$")
        self.axarr[1].set_xlabel(r"$1/k_BT$")
        arrow = dict(arrowstyle="-", connectionstyle="arc3", ls="--", color="gray")
        self.axarr[1].legend(prop={'size': 5}, loc="best", scatterpoints=1)
