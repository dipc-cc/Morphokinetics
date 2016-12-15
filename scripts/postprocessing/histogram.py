# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk
import results
import sys
import traceback

label = r''
plt.ylabel(label)
label = r'$R/F^{0.79}$'
plt.xlabel(label)
plt.figure(num=None, figsize=(8,8), dpi=80, facecolor='w', edgecolor='k')
plt.grid(True)
temperatures = np.array(list(range(120,321,5)))

workingPath = os.getcwd()
kb = 8.6173324e-5
for i in range(-6,1):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        meanValues = mk.getIslandDistribution(temperatures, sqrt=False, interval=False, growth=False, verbose=True)
    except OSError:
        print ("error changing to flux {}".format(folder))

    os.chdir(workingPath)
    
plt.close()


print("Good bye!")
          
