#!/usr/bin/env python3
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import AmmoniaPlot as api
import numpy as np
import operator
import functions as fun

import pdb

class Rds:
    def __init__(self, temperatures):
        self.api = api.AmmoniaPlot()
        self.temperatures = temperatures
        self.kb = 8.6173324e-5
        self.cm = plt.get_cmap('tab20')
        self.markers=["o", "s","D","^","d","h","p"]
        self.one = np.ones(len(temperatures))
        self.zero = np.zeros(len(temperatures))
        self.minOmega = 1e-128
        self.out = ".svg"
        self.ratesI = 6

        self.figS, self.axarr = plt.subplots(2, sharex=True, figsize=(5,4))
        self.figS.subplots_adjust(top=0.95,left=0.15, right=0.95)
        #inf.smallerFont(axar[0], 8)
        #inf.smallerFont(axar[1], 8)

        self.figR, self.axR = plt.subplots(1, figsize=(5,2))
        self.figR.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.12)
        
        self.figL, self.axL = plt.subplots(1, figsize=(5,3))
        self.figL.subplots_adjust(top=0.85,left=0.15,right=0.95,bottom=0.05)
        self.figL.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.12)


    def computeRds(self,tempMavg,rates,ratios,minAlfa,maxAlfa,ratioEa, multiplicityEa):
        #ratios[:,5] = 2*ratios[:,5] # correct O adsorption, just for RDS
        self.tempMavg = tempMavg
        self.ratios = ratios
        self.rates = rates
    
        #print(ratios[0,24]*tempMavg[-1,0,24],ratios[0,24]*tempMavg[-1,0,24]/rates[-1,0,2],rates[-1,0,2])
        ##### Relative error (delta)
        delta = np.zeros(shape=(len(self.temperatures),maxAlfa-minAlfa))
        for i,a in enumerate(range(minAlfa,maxAlfa)):
            delta[:,i] =  abs(self.one-((tempMavg[-1,:,i]*ratios[:,i])/(rates[-1,:,self.ratesI]/900)))

        #### Dominance
        self.dominance = np.zeros(shape=(len(self.temperatures),maxAlfa-minAlfa))
        for i,a in enumerate(range(minAlfa,maxAlfa)):
            m = []; m.append(self.one); m.append(delta[:,i]);
            self.dominance[:,i] =  self.one-np.amin(m,axis=0)
      
        self.maxI = np.argmax(self.dominance,axis=1)
        ratioEaTmp = np.zeros(len(self.temperatures))
        multiplicityEaTmp = np.zeros(len(self.temperatures))
        for u,t in enumerate(self.temperatures):
            ratioEaTmp[u] = ratioEa[-1,u,self.maxI[u]]
            multiplicityEaTmp[u] = multiplicityEa[-1,u,self.maxI[u]]
        activationEnergyS = ratioEaTmp - multiplicityEaTmp

        return activationEnergyS

    
    def plotRds(self, omega, minAlfa, maxAlfa, labelAlfa, multiplicityEa):
        self.axarr[0].plot(1/self.kb/self.temperatures,self.one,ls="-",color="red")
        for i,a in enumerate(range(minAlfa,maxAlfa)):
            if any(abs(omega[-1,:,i]) >= self.minOmega):
                self.axarr[0].plot(1/self.kb/self.temperatures,self.dominance[:,i],
                                   label=labelAlfa[a], ls="", color=self.cm(abs((a%20)/20)),marker=self.markers[i%7], mec=self.api.getMec(i), alpha=0.75)
        

        #self.axarr[0].legend(loc="best", prop={'size':6})
        self.axarr[0].set_ylabel(r"$\sigma^{TOF}_\alpha$")
        self.axarr[0].set_yscale("log")
        #plt.savefig("rdsDominanceLog.pdf")
        self.axarr[0].set_yscale("linear")
        self.axarr[0].set_ylim(-0.1,1.1)
        #plt.savefig("rdsDominance.pdf")

        for i,a in enumerate(range(minAlfa,maxAlfa)):
            if any(abs(omega[-1,:,i]) >= 1e-8):
                #ax.fill_between(x, lastOmegas[:,i], label=labelAlfa[a], color=cm(a%20/(19)))
                self.axarr[1].plot(1/self.kb/self.temperatures, -multiplicityEa[-1,:,i],label=labelAlfa[a], ls="", color=self.cm(abs((a%20)/20)),marker=self.markers[i%7], mec=self.api.getMec(i), alpha=0.75)
        self.axarr[1].legend(loc="best", prop={'size':6})
        self.axarr[1].set_ylabel(r"$E^M_\alpha$")
        self.axarr[1].set_xlabel(r"$1/k_BT$")
        #self.axarr[1].set_ylim(-5,5)
        self.figS.savefig("multiplicitiesSlope"+self.out)#, bbox_inches='tight')
        plt.close(self.figS)
        
        for i,a in enumerate(range(minAlfa,maxAlfa)):
            if any(abs(omega[-1,:,i]) >= self.minOmega):
                self.axR.plot(1/self.kb/self.temperatures, (self.tempMavg[-1,:,i]*self.ratios[:,i]),label=labelAlfa[a], ls=" ", color=self.cm(abs((a%20)/20)),marker=self.markers[i%7], mec=self.api.getMec(i), alpha=0.75)
        self.axR.plot(1/self.kb/self.temperatures, self.rates[-1,:,self.ratesI]/900)
        self.axR.legend(loc="best", prop={'size':6})
        self.axR.set_ylabel(r"$M_\alpha k_\alpha$")
        self.axR.set_yscale("log")
        #self.axR.set_ylim(1e-9,1e0)
        self.figR.savefig("rds"+self.out)#, bbox_inches='tight')

        
    def plotLambdas(self, activationEnergyT, activationEnergyS, labelAlfa):
        self.axL.plot(1/self.kb/self.temperatures, activationEnergyT[-1,:], label=r"$E^{TOF}_{app}$", color="red")
        self.axL.plot(1/self.kb/self.temperatures, activationEnergyS[:], "--", label=r"$\sum \xi^{TOF}_\alpha$")
        self.axL.plot(1/self.kb/self.temperatures, abs(activationEnergyT[-1,:]-activationEnergyS[:]), label="Absolute error", color="black")
        maxI = np.array(self.maxI)
        first = 0
        for i in np.unique(maxI)[::-1]:
            last = np.where(maxI == i)[0][-1]
            self.axL.fill_between(1/self.kb/self.temperatures[first:last+1],activationEnergyS[first:last+1],label=labelAlfa[i], color=self.cm(i%20/(19)))
            first = last
        self.axL.legend(loc="best", prop={'size':6})
        self.axL.set_ylabel(r"Energy $(eV)$")
        self.axL.set_ylim(-0.1,2.2)
        #labels = [item for item in self.axL.get_xticklabels()]
        #self.axL.set_xticklabels(labels)
        #mp.setY2TemperatureLabels(self.axL,self.kb)
        #fig.savefig(p.rLib+"Lambdas.pdf")
        self.figL.savefig("Lambdas"+self.out)
        plt.close(self.figL)
