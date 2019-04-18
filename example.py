# Copyright (c) Microsoft Corporation 2015, 2016

# The Z3 Python API requires libz3.dll/.so/.dylib in the 
# PATH/LD_LIBRARY_PATH/DYLD_LIBRARY_PATH
# environment variable and the PYTHONPATH environment variable
# needs to point to the `python' directory that contains `z3/z3.py'
# (which is at bin/python in our binary releases).

# If you obtained example.py as part of our binary release zip files,
# which you unzipped into a directory called `MYZ3', then follow these
# instructions to run the example:

# Running this example on Windows:
# set PATH=%PATH%;MYZ3\bin
# set PYTHONPATH=MYZ3\bin\python
# python example.py

# Running this example on Linux:
# export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:MYZ3/bin
# export PYTHONPATH=MYZ3/bin/python
# python example.py

# Running this example on macOS:
# export DYLD_LIBRARY_PATH=$DYLD_LIBRARY_PATH:MYZ3/bin
# export PYTHONPATH=MYZ3/bin/python
# python example.py


from z3 import *

cell1 = Int('cell1')
cell2 = Int('cell2')
cell3 = Int('cell3')
cell4 = Int('cell4')
cell5 = Int('cell5')
cell6 = Int('cell6')
cell7 = Int('cell7')
cell8 = Int('cell8')
cell9 = Int('cell9')
height = Int('height')
deadline1 = Int('deadline1')
s = Solver()


s.add(Or( cell1 + cell2 + cell3 == 3, Or( cell4 + cell5 + cell6 == 3), Or( cell7 + cell8 + cell9 == 3), Or( cell1 + cell4 + cell7 == 3), Or( cell2 + cell5 + cell8 == 3), Or( cell3 + cell6 + cell9 == 3), Or( cell1 + cell5 + cell9 == 3), Or( cell3 + cell5 + cell7 == 3 ) ), Or ( cell1 + cell2 + cell3 == 30, Or( cell4 + cell5 + cell6 == 30), Or( cell7 + cell8 + cell9 == 30), Or( cell1 + cell4 + cell7 == 30 ), Or( cell2 + cell5 + cell8 == 30), Or ( cell3 + cell6 + cell9 == 30), Or( cell1 + cell5 + cell9 == 30), Or( cell3 + cell5 + cell7 == 30) ) )


#s.add( cell1 + cell2 + cell3 == 30, Or( cell4 + cell5 + cell6 == 30), Or( cell7 + cell8 + cell9 == 30), Or( cell1 + cell4 + cell7 == 30 ), Or( cell2 + cell5 + cell8 == 30), Or ( #cell3 + cell6 + cell9 == 30), Or( cell1 + cell5 + cell9 == 30), Or( cell3 + cell5 + cell7 == 30) )


s.add(cell1 > -1000)

print(s.check())

s.push()



print(s.check())

#print(s.model())

