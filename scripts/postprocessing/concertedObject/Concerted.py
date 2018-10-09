import Info as inf
import Energy
import Label
import Multiplicity as m
import ConcertedPlot
import Rds
import os
import glob
import numpy as np

import pdb

class Concerted:

    def __init__(self):
        self.cPlot = ConcertedPlot.ConcertedPlot()
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
        self.__getLabels()

    def __getLabels(self):
        labels = Label.Label()
        self.labelAlfa = labels.getLabels()

                 
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
            self.cacheTotalRate = "c" in argv[1]
            self.lmbdas = "l" in argv[1]
            self.one = "1" in argv[1]
        if self.total:
            self.minAlfa = 0
            self.maxAlfa = 206
            self.info.maxA = 206
            if len(argv) > 3:
                self.minAlfa = int(argv[2])
                self.maxAlfa = int(argv[3])
            # ez dakit zergatik
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
        self.temperatures = self.info.getTemperatures()
        self.maxRanges = len(self.temperatures)
        files = glob.glob("*/results/run*/dataAe000.txt")
        files.sort()
        matrix = np.loadtxt(fname=files[0],comments=['#', '[', 'h'])
        self.info.mCov = len(matrix)
        self.cov = matrix[:,0]
        self.multi.setInfo(self.info, self.cov)

        if self.lmbdas:
            self.multiL.setInfo(self.info)

    def compute(self):
        self.tempMavg, self.omega, self.totalRate, self.totalRateEvents, self.rates, self.ratios = self.multi.getMavgAndOmega(self.temperatures,self.workingPath)
        if not self.total:
            self.totalRateEvents = np.copy(self.rates[:,:,self.ratesI]) # it is a inner rate
        self.activationEnergy, self.multiplicityEa = self.multi.getMultiplicityEa(self.temperatures,self.labelAlfa,self.sp,self.tempMavg,self.omega,self.totalRateEvents,self.ext,self.one)
        energies = self.info.getEnergies()
        self.ratioEa = np.zeros(shape=(self.info.mCov,self.maxRanges,self.info.maxA-self.info.minA))
        for i,a in enumerate(range(self.minAlfa,self.maxAlfa)):
            self.ratioEa[:,:,i] = energies[a]
            
        # No corrections, no temperature dependence in the prefactor.
        #ratioEa[:,:,0:maxAlfa-minAlfa] += e.getEaCorrections(p,temperatures)[:,minAlfa:maxAlfa]
        self.activationEnergyC = np.sum(self.omega*(self.ratioEa-self.multiplicityEa), axis=2)
        self.__computeOmegas()

        # RDS
        self.__computeRds()

        
    def __computeOmegas(self):
        self.lastOmegas = np.zeros(shape=(self.info.mMsr, self.maxRanges,self.maxAlfa-self.minAlfa)) # 10 sample coverages: 0.1, 0.2, 0.3 .. 0.9, 1.0
        self.epsilon = np.zeros(shape=(self.info.mCov,self.maxRanges,self.maxAlfa-self.minAlfa))
        if self.omegas:
            for j in range(0,self.maxRanges): # different temperature ranges (low, medium, high)
                partialSum = np.sum(self.omega[:,j,:]*(self.ratioEa[:,j,:]-self.multiplicityEa[:,j,:]), axis=1)
                for i,a in enumerate(range(self.minAlfa, self.maxAlfa)): #alfa
                    for n,k  in enumerate(range(0,self.info.mMsr)): #enumerate(np.linspace(0.1,1,10)):
                        covIndex = n #self.cPlot.getIndexFromCov(self, np.around(k,1))
                        self.lastOmegas[n, self.maxRanges-1-j,i] = partialSum[covIndex]
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
        if self.cacheTotalRate:
            self.saveTotalRate()
        for i in self.cov[-101::10][1:]: # get coverages 0.1, 0.2, 0.3.... 1
            self.cPlot.plotTotalRate(self, i)

    def plotMultiplicities(self):
        self.cPlot.plotMultiplicities(self)
        
    def plotResume(self):
        np.savetxt("slopes.txt", self.tgt)
        for i in range(0,self.info.mMsr):
            self.cPlot.plotResume(self,i, self.cov[i])

    def saveTotalRate(self):
        np.savetxt("totalRate.txt", self.totalRate)
        latSize = self.info.sizI * int(self.info.sizJ/np.sqrt(3)*2)
        np.savetxt("totalRateEvents.txt", self.totalRateEvents/latSize)
        np.savetxt("coverages.txt", self.cov)
        np.savetxt("temperatures.txt", self.temperatures)


