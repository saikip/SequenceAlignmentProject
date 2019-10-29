# Bioinformatics Course Project Fall 2019

## Team Members

** Priyam Saikia (UFID: 9414 5292) **

** Neha Rani (UFID: 3875 9234) **

# PROJECT DETAILS (Requirements detailed in hw1.pdf file)
Project implements:

1. Function for sequence generator - This function generates k number of sequences of length n each.
The first sequence is generated with randomly generated A,C,G and T as per the user input proportions.
The mutation probability is used to generated the the next k-1 similar sequences. The generated k
sequences are stored in output text file in FASTA format.
2. Function for sequence partitioning - Function for generating sub sequence with length of each sub sequence as a random number 
between the user input minimum and maximum integer. If the length of sub sequence is less than the 
miminum specified then it is discarded. The input sequence is taken from a text file in FASTA 
format. This input text file is actulaly the output of the sequence generator. All the output 
subsequence is printed in text file in FASATA format.
3. Function for Sequence assembler - This function takes the input from a text file that consists
of fragments of sequences in FASTA format. Alignment scores using dovetail alignment is calculated 
between every two pair of sequences/fragments. The two sequences of with best alignment score are 
then merged and replaced with the newly merged/aligned sequence. This process is repeated until only
one sequence is left or until the highest alignment score is negative. The longest sequence from the
resulting sequences is stored in an output test file in FASTA format.

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
1. Only txt files as input and output are supported as of now.
2. File names cannot have anything other than letters and numbers.
3. Old output files are replaced without any warning.
4. System has to have java (jdk 1.7+ and jre 1.7+) to run any java or jar command from command prompt. Latest jdk and jre can be installed from [here](https://www.oracle.com/technetwork/java/javase/downloads/index.html). Once installed/updated, add jdk and jre path to system environment variables to be able to call "java" from command prompt (Explained [here](https://www.java.com/en/download/help/path.xml)).
5. Makefile is available that compiles all java files to support any changes to the code in the java files. To run makefile, Type ```"make"``` without quotes in command prompt and enter.

#------------#------------#------------  The End ------------#------------#------------#
