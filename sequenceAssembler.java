import java.io.*;
import java.util.*;
import java.nio.*;
/*
Author- 
Priyam Saikia 
Neha Rani 

Version History-
10/18/2019 - Priyam Saikia - Initial Version

Functionality-

Functions:
*/
public class sequenceAssembler{
	// **********************************
	// VARIABLES
	// **********************************
	// Parameters input from user
	String infilename; // Input filename
	String outfilename; // Output filename
	int matchVal; // score for match (>0)
	int replaceVal; // penalty for replace (<0)
	int delinVal; // penalty for delete/insert (<0)
	
	// Other parameters required during the run of the program
	String finalSequence;
	ArrayList<String> allSequences;
	String separator="----------------------------------------------------------------------"; // Just a separator
	
	//Constructor
	public sequenceAssembler(){}
	public static void main(String args[]) throws IOException{
		int numArgs = args.length;
		if(numArgs!=5) {
			//error
			showError(0);
			return;
		}
		// checks
		if(!isPosInt(args[1])){
			showError(1);
			return;
		}
		if(!isNegInt(args[2])){
			showError(2);
			return;
		}
		if(!isNegInt(args[3])){
			showError(3);
			return;
		}
		if(!validateInFile(args[0])) {
			showError(4);
			return;
		}
		if(!validateOutFile(args[4])) return;
		
		// initialize temp variables
		String ifile=args[0].trim();
		int s=Integer.parseInt(args[1]);
		int r=Integer.parseInt(args[2]);
		int d=Integer.parseInt(args[3]);
		
		// More processing for output filename
		String ofile=args[4].trim();
		String[] toks = ofile.split("\\.");
		if(toks.length==1 || (toks.length>1 && !toks[toks.length-1].equals("txt"))){
			ofile+=".txt";
		} else if (toks[toks.length-1].equals("txt") && toks[0].isEmpty()){
			// empty file 
			ofile+=".txt";
		}
		
		//call sequenceAssembler
		sequenceAssembler sa = new sequenceAssembler();
		sa.init(ifile, s,r,d, ofile);
	}
	public void init(String ifile, int s, int r, int d, String ofile){
		infilename=ifile;
		outfilename=ofile;
		matchVal=s; 
		replaceVal=r;
		delinVal=d; 
		// Task 1: Load sequences from input file
		if(!loadSequencesFromFile()) return;
		if(allSequences.size()==0){
			System.out.println("Input file is empty or corrupt. No sequences loaded.");
			return;
		}
		if(allSequences.size()==1){
			System.out.println("Input file has only 1 sequence. Writing to output. LOL.");
			finalSequence=allSequences.get(0);
			writeToOutfile();
			return;
		}
		// Task 2: Perform dovetail alignment;
		processAllSequences();
		// Task 3: Write output to file
		writeToOutfile();
		System.out.println(separator);
		System.out.println("Output written to file: " + outfilename);
	}
	// Process all sequences until only one sequence is left
	public void processAllSequences(){
		while(allSequences.size()>1){
			alignBestSequencePair();
		}
		finalSequence=allSequences.get(0);
	}
	// Pairwise align all sequences and replace the 
	// 2 sequences of best score with aligned sequence
	public void alignBestSequencePair(){
		int maxScore=0;
		String seq1=allSequences.get(0);
		String seq2=allSequences.get(1);
		String maxSeq="just testing";
		// align all sequences with each other and keep track of maxScore
		for(int i=0;i<allSequences.size();i++){
			for(int j=i+1;j<allSequences.size();j++){
				ArrayList<String> info= getDovetailScore(allSequences.get(i),allSequences.get(j));
				int currScore = Integer.parseInt(info.get(0)); // gets score
				if(currScore>maxScore){
					maxScore = currScore;
					seq1=allSequences.get(i);;
					seq2=allSequences.get(j);;
					maxSeq=info.get(1);
				}
			}
		}
		// Remove best 2 seq and replace with aligned sequence
		allSequences.remove(seq1);
		allSequences.remove(seq2);
		allSequences.add(maxSeq);
	}
	// returns score and aligned sequence
	public ArrayList<String> getDovetailScore(String s1,String s2){
		// TO DO!!!
		String currScore = Integer.toString(1);
		String resultSeq = s1;
		
		ArrayList<String> out = new ArrayList<>();
		out.add(currScore);
		out.add(resultSeq);
		return out;
	}
	// Load sequences from input file
	public boolean loadSequencesFromFile(){
		// TO DO: Add more validations for read lines?
		allSequences=new ArrayList<>();
		try(
			BufferedReader br = new BufferedReader(new FileReader(infilename))) {
			String line = br.readLine();
			while (line != null) {
				if(line.matches("[ACGT]+")) allSequences.add(line);
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
			//System.out.println(outfilename);
			PrintWriter writer = new PrintWriter(outfilename);
			writer.println(finalSequence);
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
	// checks if input is a negative integer
	public static boolean isNegInt(String s){
		if(s.matches("-?\\d+") && Integer.parseInt(s)<0) return true;
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
	// Display error
	public static void showError(int etype){
		String formatError="Format: java hw1-2 <input_filename> <score_for_match(positive_integer)> " +
						"<penalty_for_replace(negative_integer)> <penalty_for_delete_or_insert(negative_integer)>" +
						" <output_filename>";
		System.out.print("ERROR:");		
		if(etype==0) {
			System.out.println("Invalid number of arguments! Try Again.");
			System.out.println(formatError);
		}
		else if(etype==1) System.out.println("Score for match has to be a positive integer");
		else if(etype==2) System.out.println("Penalty for replace has to be a negative integer");
		else if(etype==3) System.out.println("Penalty for delete/insert has to be a negative integer");
		else if(etype==4) System.out.println("Input file does not exist. Make sure you enter filename with extension.");
	}
}