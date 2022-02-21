When running the branch prediction strategies, first ensure the files have been compiled using:
javac *.java

To run a chosen branch prediction strategy, use the following:
java <FILE NAME> <BRANCH TRACE FILE OR DIRECTORY> <TABLE SIZE (OPTIONAL)>

The four accepted file names are: AlwaysTaken, TwoBit, GShare, Profiled

A specific table size can be entered in the command line for those specific results. The four accepted table sizes are: 512, 1024, 2048, 4096
