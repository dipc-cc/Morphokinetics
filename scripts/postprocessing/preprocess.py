import os
import info as i


workingPath = os.getcwd()
fluxes = i.getFluxes()
for f in fluxes:
    firstCollisionTime = []
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    for t in i.getTemperatures():
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            i.splitDataFiles()
            i.splitAeFiles()
            i.splitHistogramFiles()
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    kb = 8.6173324e-5
    os.chdir(workingPath)

