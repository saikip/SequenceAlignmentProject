import java.io.*;
import java.util.*;
import java.nio.*;
import java.util.concurrent.ThreadLocalRandom;
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
		if(!isPosInt(args[1])){
			System.out.println("Please enter positive integer the second and third arguments");
			return;
		}
		
		if(!validateInFile(args[0])) {
			System.out.println("Input file does not exist. Make sure you enter filename with extension.");
			return;
		}
	
		
		
		String ifile=args[0].trim();
		
		int x=Integer.parseInt(args[1]);
		int y=Integer.parseInt(args[2]);
		
		// More processing for output filename
		String ofile=args[3].trim();
		String[] toks = ofile.split("\\.");
		if(toks.length==1 || (toks.length>1 && !toks[toks.length-1].equals("txt"))){
			ofile+=".txt";
		} else if (toks[toks.length-1].equals("txt") && toks[0].isEmpty()){
			// empty file 
			ofile+=".txt";
		}
		System.out.println(ifile);
		System.out.println(x);
		System.out.println(y);
		System.out.println(ofile);
		//call sequencePartition
		sequencePartition se = new sequencePartition();
		se.init(ifile, x, y, ofile);
	}
	    
		//initialises all the sequence and create subsequences according to the rule
		public void init(String ifile, int x, int y, String ofile)
		{
	
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
			while (line != null) {
			
				inputSequences.add(line);
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
				writer.println(outputSequences.get(i));
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