#!/usr/bin/env python3

import Info as inf
import Energy
import Multiplicity as m
import AmmoniaPlot
import Rds
import os
import numpy as np

import pdb

class AmmoniaCatalysis:

    def __init__(self):
        self.aPlot = AmmoniaPlot.AmmoniaPlot()
        self.energy = Energy.Energy()
        self.info = inf.Info()
        self.total = False
        self.sp = False
        self.rAndM = False
        self.omegas = False
        self.sensibility = False
        self.tofSensibility = False
        self.kindOfSensibility = False
        self.lmbdas = False
        self.one = False
        self.ext = ""
        self.labelAlfa = [r"$V \rightarrow NH_3$",                 # P1
                          r"$NH_3 \rightarrow V$",                 # P2
                          r"$2V + O_2(g) \rightarrow 2O$",         # P3
                          r"$2O \rightarrow 2V + O_2(g)$",         # P4
                          r"$NH_3 + O \rightarrow NH_2 + OH$",     # P5
                          r"$NH_2 + OH \rightarrow NH + H_2O(g)$", # P6
                          r"$NH + OH \rightarrow N + H_2O(g)$"   , # P7
                          r"$NH + O \rightarrow N + OH$",          # P8
                          r"$N + O \rightarrow NO + V$",           # P9
                          r"$N + N \rightarrow N_2(g)$",           # P10
                          r"$NO \rightarrow V$",                   # P11
                          r"$N \rightarrow N$",                    # P12
                          r"$O \rightarrow O$",                    # P13
                          r"$OH \rightarrow OH$",                  # P14
                          r"$NH_2 + O \rightarrow NH + OH$",       # P15
                          r"$NH + OH \rightarrow NH_2 + O$",       # P16
                          r"$NH_2 + OH \rightarrow NH_3 + O$",     # P17
                          r"$N + OH \rightarrow NH + O$"]          # P18
                 
    def init(self, argv):
        self.multi = m.Multiplicity()
        self.workingPath = os.getcwd()
        if len(argv) > 1:
            self.total = "t" in argv[1]
            self.sp = "p" in argv[1]
            self.rAndM = "r" in argv[1]
            self.omegas = "o" in argv[1]
            self.sensibility = "s" in argv[1]
            self.tofSensibility = "f" in argv[1]
            self.kindOfSensibility = "k" in argv[1]
            self.lmbdas = "l" in argv[1]
            self.one = "1" in argv[1]
        if self.total:
            self.minAlfa = 0
            self.maxAlfa = 18
            self.info.maxA = 18
            if len(argv) > 3:
                self.minAlfa = int(argv[2])
                self.maxAlfa = int(argv[3])
            self.info.minA = 1#minAlfa
            self.info.maxA = 10#maxAlfa
            self.ext = "T"
        else:
            self.minAlfa = 9
            self.maxAlfa = 11
            if len(argv) > 3:
                self.minAlfa = int(argv[2])
                self.maxAlfa = int(argv[3])
            self.info.minA = self.minAlfa#0#minAlfa
            self.info.maxA = self.maxAlfa#18#maxAlfa
        self.ratesI = 6 # TOF (NO + N2)
        if self.lmbdas:
            self.multiL = m.Multiplicity()
            
    def read(self):
        self.info.setRefFile()
        self.info.setParams()
        self.info.minA = self.minAlfa # agian kentzeko
        self.info.maxA = self.maxAlfa # agian kentzeko
        self.multi.setInfo(self.info)
        self.temperatures = self.info.getTemperatures()
        self.maxRanges = len(self.temperatures)
        self.maxCo2 = int(self.info.nCo2/10)
        if self.lmbdas:
            self.multiL.setInfo(self.info)

    def compute(self):
        self.tempMavg, self.omega, self.totalRate, self.totalRateEvents, self.rates, self.ratios = self.multi.getMavgAndOmega(self.temperatures,self.workingPath)
        if not self.total:
            self.totalRateEvents = np.copy(self.rates[:,:,self.ratesI]) # it is a inner rate
        self.activationEnergy, self.multiplicityEa = self.multi.getMultiplicityEa(self.temperatures,self.labelAlfa,self.sp,self.tempMavg,self.omega,self.totalRateEvents,self.ext,self.one)
        energies = self.energy.getEnergies()
        self.ratioEa = np.zeros(shape=(self.maxCo2,self.maxRanges,self.info.maxA-self.info.minA))
        for i,a in enumerate(range(self.minAlfa,self.maxAlfa)):
            self.ratioEa[:,:,i] = energies[a]
            
        # No corrections, no temperature dependence in the prefactor.
        #ratioEa[:,:,0:maxAlfa-minAlfa] += e.getEaCorrections(p,temperatures)[:,minAlfa:maxAlfa]
        self.activationEnergyC = np.sum(self.omega*(self.ratioEa-self.multiplicityEa), axis=2)
        self.__computeOmegas()

        # RDS
        self.__computeRds()

        
    def __computeOmegas(self):
        self.lastOmegas = np.zeros(shape=(self.maxRanges,self.maxAlfa-self.minAlfa))
        self.epsilon = np.zeros(shape=(self.maxCo2,self.maxRanges,self.maxAlfa-self.minAlfa))
        if self.omegas:
            for j in range(0,self.maxRanges): # different temperature ranges (low, medium, high)
                partialSum = np.sum(self.omega[:,j,:]*(self.ratioEa[:,j,:]-self.multiplicityEa[:,j,:]), axis=1)
                for i,a in enumerate(range(self.minAlfa, self.maxAlfa)): #alfa
                    self.lastOmegas[self.maxRanges-1-j,i] = partialSum[-1]
                    partialSum    -= self.omega[:,j,i]*(self.ratioEa[:,j,i]-self.multiplicityEa[:,j,i])
                    self.epsilon[:,j,i] = self.omega[:,j,i]*(self.ratioEa[:,j,i]-self.multiplicityEa[:,j,i])

    def __computeRds(self):
        if self.lmbdas:
            tempMavgS, omegaS, totalRateS, totalRateEventsS, ratesS, ratiosS = self.multiL.getMavgAndOmega(self.temperatures,self.workingPath)
            totalRateEventsS = np.copy(self.rates[:,:,self.ratesI]) # it is a inner rate
            os.chdir(self.workingPath)
            #activationEnergyT, multiplicityEaS = mi.getMultiplicityEa(p,temperatures,labelAlfa,sp,tempMavgS,omegaS,totalRateEventsS,ext="")
            activationEnergyT, multiplicityEaS = self.multiL.getMultiplicityEa(self.temperatures,self.labelAlfa,self.sp,tempMavgS,omegaS,totalRateEventsS,self.ext,self.one)

            
            rds = Rds.Rds(self.temperatures)
            activationEnergyS = rds.computeRds(self.tempMavg,self.rates,self.ratios,self.minAlfa,self.maxAlfa,self.ratioEa, self.multiplicityEa)
            rds.plotRds(self.omega, self.minAlfa, self.maxAlfa, self.labelAlfa, self.multiplicityEa)
            
            rds.plotLambdas(activationEnergyT, activationEnergyS, self.labelAlfa)


    def plotTotalRate(self):
        os.chdir(self.workingPath)
        self.aPlot.plotTotalRate(self)

    def plotMultiplicities(self):
        self.aPlot.plotMultiplicities(self)
        
    def plotResume(self):
        self.aPlot.plotResume(self)



