# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk


plt.title("Fractal dimension")
label = r'coverage/number of islands'
plt.ylabel(label)
label = r'temperature'
plt.xlabel(label)
plt.legend(loc='upper left', prop={'size':6})
plt.grid(True)
axes = plt.gca()
#axes.set_ylim([1.5,2.5])
temperatures = list(range(120,321,5))

workingPath = os.getcwd()
print(workingPath)
results = []
for i in range(-5,1):
    folder = "flux3.5e"+str(i)
    flux = float("3.5e"+str(i))
    print(folder)
    verbose = False
    try:
        os.chdir(folder)
        results.append(mk.getAllFractalDimensions(temperatures, verbose))
    except OSError:
        print ("ERROR changing to {}".format(folder))
    os.chdir(workingPath)

    while(len(temperatures) > len(results[-1])):
        results[-1].append(float('nan'))
    plt.plot(temperatures, np.array(results[-1]), label=folder)
    plt.legend(loc='upper left', prop={'size':6})
    plt.savefig("fractalDimension.png")

print("Good bye!")
          
