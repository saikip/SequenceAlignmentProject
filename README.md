# Bioinformatics Course Project Fall 2019

## Team Members

** Priyam Saikia (UFID: 9414 5292) **

** Neha Rani (UFID: 3875 9234) **

# PROJECT DETAILS
Project implements:

1. Simulator for sequence generator
2. Simulator for sequence partitioning
3. Sequence assembler

# FILES in Zip Folder

## Java Files (and their corresponding class files): 

1. sequenceGenerator.java 
2. sequencePartition.java 
3. sequenceAssembler.java 

## Executable files: 

1. hw1-1.jar
2. hw1-2.jar
3. hw1-3.jar
4. hw1-1.exe
5. hw1-2.exe
6. hw1-3.exe

## Other files: 

1. makefile
2. readme.md

# Languages/Tools used
Java - for programming

Eclipse - for jar files creation

Launch4j - for executable creation

# Instructions to run
1. Extract files from Zip folder. 
2. Open command prompt or Terminal in your current directory.
3. You can run the programs using three methods:
	1. Using exe - This won't let you see our error and warning prompts in case of wrong inputs.
		1. hw1-1 seqLength FractionA FractionC FractionG FractionT NumofSeq mutationProb outputFileName
			
			sample: ```hw1-1 10000 25 25 25 25 10 0.005 out1.txt```
		2. hw1-2 inputFileName minFragmentLen maxFragmentLen outputFileName
			
			sample: ```hw1-2 out1.txt 100 150 out2.txt```
		3. hw1-3 inputFileName scoreMatch penaltyReplace penaltyDelete outputFileName
			
			sample: ```hw1-3 out2.txt 1 -1 -3 out3.txt```
	2. Using java jar - This will let you see our error and warning prompts
		1. ```java -jar hw1-1.jar 10000 25 25 25 25 10 0.005 out1.txt```
		2. ```java -jar hw1-2.jar out1.txt 100 150 out2.txt```
		3. ```java -jar hw1-3.jar out2.txt 1 -1 -3 out3.txt```
	3. Using java - This will let you see our error and warning prompts
		1. ```java sequenceGenerator 10000 25 25 25 25 10 0.005 out1.txt```
		2. ```java sequencePartition out1.txt 100 150 out2.txt```
		3. ```java sequenceAssembler out2.txt 1 -1 -3 out3.txt```
3. The output files will be created and placed in your current directory.

# NOTES
1. We have a make file that compiles all java files in case any of the java files are updated
	
	Type ```"make"``` without quotes in command prompt and enter
2. File names cannot have anything other than letters and numbers.
3. Only txt files as input and output are supported.
4. Old output files are replaced without any warning.

#------------#------------#------------  The End ------------#------------#------------#
