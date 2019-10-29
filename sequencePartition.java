import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
/*
Author- 
Priyam Saikia 
Neha Rani 

Version History-
10/18/2019 - Neha Rani - Initial Version

Functionality
This program will partition the sequences in a
file into small fragments

Input
1. string: input file name
2. integer: x = minimum fragment length
3. integer: y = maximum fragment length (y > x)
4. string: output file name

Functions:
1.init
2.loadSequencesFromFile
3.writeToOutfile
4.validateInFile
5.validateOutFile
6.loadFilenames
7.isPosInt

*/
public class sequencePartition
{
	//Gobal variables
	String infilename; // Input filename
	String outfilename; // Output filename
	ArrayList<String> inputSequences;
	ArrayList<String> outputSequences;
	int min;
	int max;
	
	//Constructor
	public sequencePartition(){}
	
	public static void main(String args[]) throws IOException {
	    
		int numArgs = args.length;
		if(numArgs!=4) {
			//error check
			System.out.println("Invalid number of arguments! Try Again.");
			return;
		}
		// checks
		if(!isPosInt(args[1]) || !isPosInt(args[2])){
			System.out.println("Please enter positive integer the second and third arguments");
			return;
		}
		if(!validateInFile(args[0])) {
			System.out.println("Input file does not exist. Make sure you enter filename with extension.");
			return;
		}
		if(!validateOutFile(args[3])) return;
		
		String ifile=args[0].trim();
		
		int x=Integer.parseInt(args[1]);
		int y=Integer.parseInt(args[2]);
		if(x>y){
			System.out.println("Value of X should be less than or equal to Y. Try again");
			return;
		}
		
		// More processing for output filename
		String ofile=args[3].trim();
		String[] toks = ofile.split("\\.");
		if(toks.length==1 || (toks.length>1 && !toks[toks.length-1].equals("txt"))){
			ofile+=".txt";
		} else if (toks[toks.length-1].equals("txt") && toks[0].isEmpty()){
			// empty file 
			ofile+=".txt";
		}
		//System.out.println(ifile);
		//System.out.println(x);
		//System.out.println(y);
		//System.out.println(ofile);
		//call sequencePartition
		sequencePartition se = new sequencePartition();
		se.init(ifile, x, y, ofile);
	}
	//initialises all the sequence and create subsequences according to the rule
	public void init(String ifile, int x, int y, String ofile){
		min=x;
		max=y;
		infilename=ifile;
		outfilename=ofile;
		outputSequences=new ArrayList<>();
		// Task 1: Load sequences from input file
		if(!loadSequencesFromFile()) return;
		if(inputSequences.size()==0){
			System.out.println("Input file is empty or corrupt. No sequences loaded.");
			return;
		}
		// Task 2: Cut into fragments
		for(int l=0; l<inputSequences.size(); l++)
		{
			String seq=inputSequences.get(l);
			int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
			int seqLen=seq.length();
			
			//System.out.println(randomNum + "is random number");
			String subSeq="";
			for(int i=0;i<seqLen;i++)
			{
				if(subSeq.length()==randomNum)
				{
					//System.out.println(randomNum);
					outputSequences.add(subSeq);
					subSeq="";
					randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
					
				}
				subSeq=subSeq+seq.charAt(i);
			}
			if (subSeq.length()>=min)
			{
				outputSequences.add(subSeq);
		
			}
		}
		// Task 3: Write sequences to file
		writeToOutfile();
		System.out.println("Output written to file: " + outfilename);
	}
	// Load sequences from input file
	public boolean loadSequencesFromFile(){
		// TO DO: Add more validations for read lines?
		inputSequences=new ArrayList<>();
		try(
			BufferedReader br = new BufferedReader(new FileReader(infilename))) {
			String line = br.readLine();
			String curSeq="";
			while (line != null) {
				//if(line.matches("[ACGT]+")) inputSequences.add(line);
				if(line.matches("[ACGT]+")) curSeq=curSeq+line;
				else if(line.matches("[>]+")){
					if(!curSeq.isEmpty()) inputSequences.add(curSeq);
					curSeq="";
				}
				line = br.readLine();
			}
		}
		catch (IOException e){
			e.printStackTrace();
			System.out.println("IO Exception! Error reading sequences from input file!");
			return false;
		}
		return true;
	}
	// write final sequence to file
	public void writeToOutfile(){
		try{
			
			PrintWriter writer = new PrintWriter(outfilename);
			for(int i=0;i<outputSequences.size();i++){
				writer.println(">");
				//writer.println(outputSequences.get(i));
				String curSeq=outputSequences.get(i);
				int j=0;
				while(j+80<curSeq.length()){
					writer.println(curSeq.substring(j,j+80));
					j=j+80;
				}
				if(j<curSeq.length()) writer.println(curSeq.substring(j));
			}
			writer.close();
			
		} 
		catch (IOException e){
			e.printStackTrace();
			System.out.println("IO Exception! Error writing output to file!");
		}
	}
	
	// **********************************
	// PRE-PROCESSING GOES HERE
	// **********************************
	// checks if input is a positive integer
	
	public static boolean isPosInt(String s){
		if(s.matches("\\d+") && Integer.parseInt(s)>0) return true;
		else return false;
	}
	// Validates input filename
	public static boolean validateInFile(String s){
		ArrayList<String> filenames = loadFilenames();
		if(filenames.size()==0 || !filenames.contains(s)) return false;
		return true;
	}
	// Validates output filename
	public static boolean validateOutFile(String s){
		String[] toks = s.trim().split("\\.");
		if(toks.length>1 && !toks[toks.length-1].equals("txt")){
			System.out.println("ERROR: Output file cannot be a "+toks[toks.length-1]+" file. Try again.");
			return false;
		}
		ArrayList<String> filenames = loadFilenames();
		if(filenames.size()>0) {
			if(filenames.contains(s) || filenames.contains(s+".txt")){
				/*
				try{
					System.out.println("Output file already exists. Do you want to replace it? Y for yes; N for no");
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
					String response;
					while(true){
						response = bufferedReader.readLine();
						if(response.trim().toUpperCase().equals("Y")){
							return true;
						} else if(response.trim().toUpperCase().equals("N")){
							System.out.println("Exiting... Try again.");
							return false;
						}
						System.out.println("Invalid input! Try again.");
					}
				}
				catch(IOException e){
					e.printStackTrace();
					System.out.println("IO exception in filename. Try again.");
					return false;
				}
				*/
				return true; // replacing now 
			}
		} else if(!s.matches("^\\d*[a-zA-Z][a-zA-Z\\d]*$")){
			System.out.println("Output file must contain only letters and numbers. Please try again!");
			return false;
		} 
		return true;
	}
	// Read all filenames in folder
	public static ArrayList<String> loadFilenames(){
		File folder = new File(System.getProperty("user.dir"));
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> filenames = new ArrayList<>();

		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			filenames.add(listOfFiles[i].getName());
		  }
		}
		return filenames;
	}
	
}