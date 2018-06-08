import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import AmmoniaPlot as ap
import numpy as np
import operator
import functions as fun

import pdb

class Rds:
    def __init__(self, temperatures):
        self.ap = ap.AmmoniaPlot()
        self.temperatures = temperatures
        self.kb = 8.6173324e-5
        self.cm = plt.get_cmap('tab20')
        self.markers=["o", "s","D","^","d","h","p"]
        self.one = np.ones(len(temperatures))
        self.zero = np.zeros(len(temperatures))
        self.minOmega = 1e-128
        self.out = ".pdf"
        self.ratesI = 6

        self.figS, self.axarr = plt.subplots(2, sharex=True, figsize=(5,4))
        self.figS.subplots_adjust(top=0.95,left=0.15, right=0.95)
        self.ap.smallerFont(self.axarr[0], 8)
        self.ap.smallerFont(self.axarr[1], 8)

        self.figR, self.axR = plt.subplots(1, figsize=(5,2))
        self.figR.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.10)
        
        self.figL, self.axL = plt.subplots(1, figsize=(5,3))
        self.figL.subplots_adjust(top=0.85,left=0.15,right=0.95,bottom=0.05)


    def computeRds(self, tempMavg, rates, ratios, minAlfa, maxAlfa, ratioEa, multiplicityEa):
        self.tempMavg = tempMavg
        self.ratios = ratios
        self.rates = rates
    
        ##### Relative error (delta)
        delta = np.zeros(shape=(len(self.temperatures),maxAlfa-minAlfa))
        for i,a in enumerate(range(minAlfa,maxAlfa)):
            delta[:,i] =  abs(self.one-((tempMavg[-1,:,i]*ratios[:,i])/(rates[-1,:,self.ratesI])))

        #### Dominance
        self.dominance = np.zeros(shape=(len(self.temperatures),maxAlfa-minAlfa))
        for i,a in enumerate(range(minAlfa,maxAlfa)):
            m = []; m.append(self.one); m.append(delta[:,i]);
            self.dominance[:,i] =  self.one-np.amin(m,axis=0)
      
        self.maxI = np.argmax(self.dominance,axis=1)
        #pdb.set_trace()
        self.maxI[0] = 10
        ratioEaTmp = np.zeros(len(self.temperatures))
        multiplicityEaTmp = np.zeros(len(self.temperatures))
        for i,t in enumerate(self.temperatures):
            #if i == len(self.temperatures) -1:
            #    continue
            ratioEaTmp[i] = ratioEa[-1,i,self.maxI[i]]
            multiplicityEaTmp[i] = multiplicityEa[-1,i,self.maxI[i]]
        self.activationEnergyS = ratioEaTmp - multiplicityEaTmp

    
    def plotRds(self, omega, minAlfa, maxAlfa, labelAlfa, multiplicityEa):
        self.axarr[0].plot(1/self.kb/self.temperatures,self.one,ls="-",color="red")
        for i,a in enumerate(range(minAlfa,maxAlfa)):
            if any(abs(omega[-1,:,i]) >= self.minOmega):
                self.axarr[0].plot(1/self.kb/self.temperatures,self.dominance[:,i],
                                   label=labelAlfa[a], ls="", color=self.cm(abs((a%20)/20)),marker=self.markers[i%7], mec=self.ap.getMec(i), alpha=0.75)
        

        self.axarr[0].set_ylabel(r"$\sigma^{TOF}_\alpha$")
        self.axarr[0].set_yscale("log")
        self.axarr[0].set_yscale("linear")
        self.axarr[0].set_ylim(-0.1,1.1)

        for i,a in enumerate(range(minAlfa,maxAlfa)):
            if any(abs(omega[-1,:,i]) >= 1e-8):
                self.axarr[1].plot(1/self.kb/self.temperatures, -multiplicityEa[-1,:,i],label=labelAlfa[a], ls="", color=self.cm(abs((a%20)/20)),marker=self.markers[i%7], mec=self.ap.getMec(i), alpha=0.75)
        self.axarr[1].legend(loc="best", prop={'size':6})
        self.axarr[1].set_ylabel(r"$E^M_\alpha$")
        self.axarr[1].set_xlabel(r"$1/k_BT$")
        self.figS.savefig("multiplicitiesSlope"+self.out)
        plt.close(self.figS)
        
        for i,a in enumerate(range(minAlfa,maxAlfa)):
            if any(abs(omega[-1,:,i]) >= self.minOmega):
                self.axR.plot(1/self.kb/self.temperatures, (self.tempMavg[-1,:,i]*self.ratios[:,i]),label=labelAlfa[a], ls=" ", color=self.cm(abs((a%20)/20)),marker=self.markers[i%7], mec=self.ap.getMec(i), alpha=0.75)
        self.axR.plot(1/self.kb/self.temperatures, self.rates[-1,:,self.ratesI])
        self.axR.legend(loc="best", prop={'size':6})
        self.axR.set_ylabel(r"$M_\alpha k_\alpha$")
        self.axR.set_yscale("log")
        labels = [item for item in self.axR.get_xticklabels()]
        self.axR.set_xticklabels(labels)
        self.figR.savefig("rds"+self.out)

        
    def plotLambdas(self, activationEnergyT, labelAlfa):
        self.axL.plot(1/self.kb/self.temperatures, activationEnergyT[-1,:], label=r"$E^{TOF}_{app}$", color="red")
        self.axL.plot(1/self.kb/self.temperatures, self.activationEnergyS[:], "--", label=r"$\sum \xi^{TOF}_\alpha$")
        self.axL.plot(1/self.kb/self.temperatures, abs(activationEnergyT[-1,:]-self.activationEnergyS[:]), label="Absolute error", color="black")
        #pdb.set_trace()
        maxI = np.array(self.maxI)
        for i,a in enumerate(maxI):
            if i == 0:
                continue
            if i == 1:
                self.axL.fill_between(1/self.kb/self.temperatures[i-1:i+1],self.activationEnergyS[i-1:i+1],label=labelAlfa[a], color=self.cm(a%20/(19)))
            else: #the same without label
                self.axL.fill_between(1/self.kb/self.temperatures[i-1:i+1],self.activationEnergyS[i-1:i+1], color=self.cm(a%20/(19)))
        self.axL.legend(loc="best", prop={'size':6})
        self.axL.set_ylabel(r"Energy $(eV)$")
        self.axL.set_ylim(-0.1,2.2)
        self.ap.setY2TemperatureLabels(self.axL)
        self.figL.savefig("Lambdas"+self.out)
        plt.close(self.figL)
