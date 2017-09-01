import numpy as np
import matplotlib.pyplot as plt

data = np.loadtxt("dataTof.txt")
first=200
time = data[:,0][-first:-1]
plt.plot(time,data[:,1][-first:-1])
plt.plot(time,data[:,2][-first:-1])
plt.plot(time,data[:,3][-first:-1])
plt.plot(time,data[:,4][-first:-1])

plt.show()
