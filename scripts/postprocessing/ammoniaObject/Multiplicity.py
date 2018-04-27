import matplotlib
matplotlib.use("Agg")
import Info
import glob as glob
import numpy as np
import os
import AmmoniaPlot
import AmmoniaPlotIntermediate
import matplotlib.pyplot as plt
import Energy

import pdb

class Multiplicity:

    def setInfo(self, info):
        self.info = info
        self.aPlot = AmmoniaPlot.AmmoniaPlot()
        
    def printRatios(self, ratios):
        rows = 12
        columns = 16
        for i in range(0,rows):
            for j in range(0,columns):
                if ratios[i*columns + j] < 1e-120:
                    print("\t", end="")
                else:
                    print("%1.3E"% (ratios[i*columns + j]), end="\t")
            print()
        print("Island diffusion rates")
        for i in range(192,201):
            print("%1.3E"% (ratios[i]), end="\t")
        print("\nMulti atom rates:")
        for i in range(201,205):
            print("%1.3E"% (ratios[i]), end="\t")
        print()
    
    def computeMavgAndOmega(self, fileNumber):
        p = self.info
        name = "dataAePossibleFromList"#name = "dataAeAll"
        inf = Info.Info()
        inf.setParams()
        #pTemp = inf.getInformationFromFile()
        ratios = inf.getRatiosTotal()[p.minA:p.maxA]
        possiblesFromList = np.loadtxt(fname=name+"{:03d}".format(fileNumber)+".txt")
        time = np.array(possiblesFromList[:p.mMsr,0])
        possiblesFromList = possiblesFromList[:,1:] # remove time
   
        Mavg = np.zeros(shape=(p.mMsr,p.maxA-p.minA))
        for i,a in enumerate(range(p.minA,p.maxA)): # iterate alfa
            Mavg[:,i] = (possiblesFromList[:p.mMsr,a])/time/p.sizI/p.sizJ
    
        totalRate = np.array(ratios.dot(np.transpose(Mavg)))#/time/p.sizI/p.sizJ
        #totalRate = (totalRate+occupied)/time/p.sizI/p.sizJ
        # define omegas 
        omega = np.zeros(shape=(p.mMsr,p.maxA-p.minA)) # [co2amount, alfa]
        for i in range(0,p.mMsr):
            omega[i,:] =  Mavg[i,:] * ratios / totalRate[i]
            #omega[i,:] =  (Mavg[i,:]+occupied[i]) * ratios / totalRate[i]
        return Mavg, omega, totalRate, ratios
    
    
    def computeMavgAndOmegaOverRuns(self):
        p = self.info
        files = glob.glob("dataAePossibleFromList*")
        files.sort()
        filesNumber = len(files)
        sumMavg = np.zeros(shape=(p.mMsr,p.maxA-p.minA))  # [time|CO2, alfa]
        sumOmega = np.zeros(shape=(p.mMsr,p.maxA-p.minA)) # [time|CO2, alfa]
        sumRate = np.zeros(p.mMsr)
        #iterating over runs
        for i in range(0,filesNumber):
            try:
                tmpMavg, tmpOmega, tmpRate, ratios = self.computeMavgAndOmega(i)
                sumMavg = sumMavg + tmpMavg
                sumOmega = sumOmega + tmpOmega
                sumRate = sumRate + tmpRate
            except (FileNotFoundError,ValueError,OSError) as e: # there is no file, or the file has less lines that previous lines
                filesNumber -= 1
                print("error ",e)
        
        runMavg = sumMavg / filesNumber
        runOavg = sumOmega / filesNumber
        totalRate = sumRate / filesNumber
    
        totalRateEvents, rates = self.getTotalRate()
        return runMavg, runOavg, totalRate, totalRateEvents, rates, ratios
    
    
    def getMavgAndOmega(self, temperatures, workingPath):
        p = self.info
        maxTemp = len(temperatures)
        p.mMsr = max(int(p.nCo2/10),p.mCov)
        tempMavg = np.zeros(shape=(p.mMsr,maxTemp,p.maxA-p.minA))
        tempOavg = np.zeros(shape=(p.mMsr,maxTemp,p.maxA-p.minA))
        totalRate = np.zeros(shape=(p.mMsr,maxTemp))
        ratios = np.zeros(shape=(maxTemp,p.maxA-p.minA)) # Used ratios for simulation
        totalRateEvents = np.zeros(shape=(p.mMsr,maxTemp))
        rates = np.zeros(shape=(p.mMsr,maxTemp,7)) # adsorption, desorption, reaction, diffusion, NO, N2 and TOF (NO+N2) rates
        for i,t in enumerate(temperatures):
            print(t)
            os.chdir(workingPath)
            try:
                os.chdir(str(t)+"/results")
                runFolder = glob.glob("*/");
                runFolder.sort()
                os.chdir(runFolder[-1])
            except FileNotFoundError:
                continue
            tempMavg[:,i,:], tempOavg[:,i,:], totalRate[:,i], totalRateEvents[:,i], rates[:,i,:], ratios[i,:] = self.computeMavgAndOmegaOverRuns()
            
        return tempMavg, tempOavg, totalRate, totalRateEvents, rates, ratios
    
    def getMultiplicityEa(self,temperatures,labelAlfa,sp,tempMavg,omega,totalRate,ext="",one=False):
        p = self.info
        maxRanges = len(temperatures)
        kb = 8.6173324e-5
                      # [co2, type (alfa), temperature range]
        multiplicityEa   = np.zeros(shape=(p.mMsr,maxRanges,p.maxA-p.minA))
        activationEnergy    = np.zeros(shape=(p.mMsr,maxRanges))
        total = ext == "T"
        for co2 in range(0,p.mMsr): # created co2: 10,20,30...1000
            x = 1/kb/temperatures
            self.api = AmmoniaPlotIntermediate.AmmoniaPlotIntermediate(x, co2, sp, total, one)
            print(co2+1,"/",p.mMsr,sp)
            y = totalRate
            # N_h
            activationEnergy[co2,:] = self.getSlopes(x, y[co2,:], -1, verbose=True)
    
            first = True
            omegaSumTof = np.zeros(shape=(len(temperatures)))
            for i,a in enumerate(range(p.minA,p.maxA)): # alfa
                y = np.sum(omega[co2,:,i:i+1], axis=1)
                if i == 9 or i == 10:
                    omegaSumTof += y # NO eta N2
                self.api.plotOmegas(y, a, labelAlfa)

                y = np.sum(tempMavg[co2,:,i:i+1], axis=1)
                multiplicityEa[co2,:,i] = self.getSlopes(x, y, i)
                self.api.plotLinear(y, a)
            self.api.flush(omegaSumTof)
        activationEnergy = -activationEnergy
        return activationEnergy, multiplicityEa
    
    def getTofSensibility(p,omega,ratioEa,multiplicityEa):
        sensibilityCo2 = np.zeros(np.shape(omega))
        sumBeta = 0
        sumOmegaBeta = 0
        for beta in range(0,4):
            sumBeta += omega[:,:,beta]*(ratioEa[:,:,beta]-multiplicityEa[:,:,beta])
            sumOmegaBeta += omega[:,:,beta]
        for a in range(p.minA,p.maxA):
            sensibilityCo2[:,:,a] = omega[:,:,a]/sumOmegaBeta*(sumBeta/ratioEa[:,:,a])
    
        return sensibilityCo2
    
    def getTotalSensibility(p,omega,ratioEa,multiplicityEa):
        sensibilityCo2 = np.zeros(np.shape(omega))
        for i in range(p.minA,p.maxA):
            sensibilityCo2[:,:,i] = omega[:,:,i]*(1-multiplicityEa[:,:,i]/ratioEa[:,:,i])
        return sensibilityCo2
    
    
    # Computes total rates from number of events
    def getTotalRate(self):
        files = glob.glob("dataCatalysis0*.txt")
        totalRate = 0
        rates = np.zeros(7)
        indexes = list(range(8,12)) + [21, 22]
        for t in files:
            data = np.loadtxt(t)
            events = 0
            eventsA = np.zeros(7)
            for i,p in enumerate(indexes):
                events += data[-1,p] - data[0,p]
                eventsA[i] += data[-1,p] - data[0,p]
            eventsA[6] = eventsA[4] + eventsA[5] # TOF: NO + N2
            totalRate += events / data[-1,0] # last time
            rates += eventsA / data[-1,0]
        totalRate = totalRate / len(files) / self.info.sizI / self.info.sizJ / 2
        rates = rates / len(files) / self.info.sizI / self.info.sizJ / 2
        return totalRate, rates
    
    def getSlopes(self, x, y, alfa, verbose=False):
        slopes = []
        l = 1
    
        # all
        s = self.aPlot.allSlopes(x,y)
        if verbose and (any(np.isinf(s)) or any(np.isnan(s))):
            print("error fitting",alfa)
            
        for i in range(0,len(x)):
            a, b = self.aPlot.fitA(x,y,s,i)
            slopes.append(b)
        return slopes
