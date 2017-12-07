#!/usr/bin/env python3

import numpy as np

table = np.loadtxt("input.txt")

types = ["type 0",
         "type 1",
         "type 2, subtype 0", 
         "type 2, subtype 1",
         "type 2, subtype 2",
         "type 3, subtype 0",
         "type 3, subtype 1",
         "type 3, subtype 2",
         "type 4, subtype 0",
         "type 4, subtype 1", 
         "type 4, subtype 2", 
         "type 5", 
         "type 1, detach", 
         "type 2, subtype 0, detach", 
         "type 2, subtype 1, detach", 
         "type 3, subtype 0, detach"]

a = table[1:,0] # row index
b = table[0,1:] # column index
table = table[:,1:] # remove index row
table = table[1:,:] # remove index column
for i in range(0,len(table)):
    print("    // From ", types[i])
    for j in range(0,len(table[0])):
        if table[i][j] < 100:
            print("    energies["+str(int(a[i]))+"]["+str(int(b[j]))+"] = "+str(table[i][j])+"; // to "+types[int(b[j])])
