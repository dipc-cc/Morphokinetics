#!/usr/bin/env python3

# Copyright (C) 2018 J. Alberdi-Rodriguez
#
# This file is part of Morphokinetics.
#
# Morphokinetics is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Morphokinetics is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Morphokinetics.  If not, see <http://www.gnu.org/licenses/>.

from psdPreparator import PsdPreparator
from configurator import Configurator
import os

cf = Configurator()
filtered = "Fil" # Available options are: "Fil" and "Raw"

for j in cf.generateParams():
    cf.createFolder(j)
    print(os.getcwd())
    try:
        psd = PsdPreparator()
        for i in psd.generateParams():
            psd.writeParameters(i)
            psd.runPsd(filtered)
            psd.plotPsd(filtered)
    except FileNotFoundError:
        continue
