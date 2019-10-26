import java.io.*;
import java.util.*;
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
	String finalSequence; //longest sequence at the end of rounds
	int currMaxScore; //maxscore at the end of each round
	int[][] scoreMatrix; //matrix to store maxscore among sequences
	ArrayList<String> allSequences; //stores all sequences; gets updates with new aligned sequences
	HashSet<Integer> removeSet=new HashSet<>(); //to store indexes of outdated/aligned sequences
	String separator="----------------------------------------------------------------------"; // Just a separator
	
	//Constructor
	public sequenceAssembler(){}
	//Main function - Validation of arguments and init
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
	// **********************************
	// ACTUAL PROCESSING GOES HERE
	// **********************************
	
	// Initialize variables and manage process flow
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
		// Load score matrix to keep scores so as to not re-calculate
		scoreMatrix=new int[allSequences.size()][allSequences.size()];
		// Complete process
		processAllSequences();
		// Task 3: Write output to file
		writeToOutfile();
		System.out.println("\n"+separator);
		System.out.println("\n"+"Output written to file: " + outfilename);
	}
	// Process all sequences until only one sequence is left 
	// or largest alignment score is negative
	public void processAllSequences(){
		int trueSize=allSequences.size();//actual # of all sequences
		int currSize=trueSize; //store # sequences for current round
		int newSeqId=0; // id of newly created aligned sequence
		currMaxScore=0; // current maxscore to make sure it's not negative
		
		// Perform rounds
		System.out.println("Total Fragments: " + trueSize);
		System.out.println("Calculating dovetail scores. Please wait...");
		while(currSize>1 && currMaxScore>=0){
			//System.out.println("# of sequences reduced to:" + currSize);
			if(currSize==trueSize) {
				newSeqId=alignBestSequencePair(true,0);
				System.out.print("Sequences processed:   0 ");
			}
			else newSeqId=alignBestSequencePair(false,newSeqId);
			// Reduce # sequences for next round by 1
			// Two sequences aligned, 
			// the result replaced one's index, other one put in outdated matrix
			currSize--;
			//System.out.println("overall maxscore: " + currMaxScore);
			double percent=((trueSize-currSize-1)*100.00/trueSize);
			System.out.printf("\b\b\b\b\b%3d %%", (int)percent );
		}
		// Get longest among the non-outdated sequences
		finalSequence=getLongestSequence();
	}
	// Write the longest fragment you assembled into output in Fasta format.
	public String getLongestSequence(){
		String longestSeq="";
		int maxLen=0;
		for(int i=0;i<allSequences.size();i++){
			// only consider non-outdated sequences
			if(!removeSet.contains(i)){
				String s=allSequences.get(i);
				if(s.length()>maxLen) {
					maxLen=s.length();
					longestSeq=s;
				}
			}
		}
		//System.out.println("Longest seq length: " + maxLen);
		return longestSeq;
	}
	// Pairwise align all sequences, select 2 sequences with best scores
	// replace one of the old sequences' index with aligned sequence
	// Put other old sequences' index in removeSet
	// return new sequence Index
	public int alignBestSequencePair(boolean firstTime, int newSeqId){
		currMaxScore=0;//max score for each round
		String seq1=allSequences.get(0);
		String seq2=allSequences.get(1);
		int id1=0; //index of s1
		int id2=0; //index of s2
			
		//String maxSeq="just testing";
		if(firstTime){
			// process all sequences
			// align all sequences with each other and keep track of maxScore
			for(int i=0;i<allSequences.size();i++){
				for(int j=i+1;j<allSequences.size();j++){
					int currScore= getDovetailScore(allSequences.get(i),allSequences.get(j));
					scoreMatrix[i][j]=currScore;
					if(currScore>currMaxScore){
						currMaxScore = currScore;
						seq1=allSequences.get(i);
						seq2=allSequences.get(j);
						id1=i;
						id2=j;
					}
				}
			}
		} else {
			// process only new sequence alignment score with others
			for(int i=0;i<allSequences.size();i++){
				for(int j=i+1;j<allSequences.size();j++){
					int currScore=0;
					// do only if both sequences are still in consideration and not outdated
					if(!removeSet.contains(i)&&!removeSet.contains(j)){
						if(newSeqId==i || newSeqId==j) {
							//one of these are newly aligned sequence, calculate new scores
							currScore= getDovetailScore(allSequences.get(i),allSequences.get(j));
							scoreMatrix[i][j]=currScore; // update score matrix
						} else {
							// both old matrices, we already have scores
							currScore=scoreMatrix[i][j];
						}
						if(currScore>currMaxScore){
							currMaxScore = currScore;
							seq1=allSequences.get(i);
							seq2=allSequences.get(j);
							id1=i;
							id2=j;
						}
					}
				}
			}
		}
		// Get the aligned sequence for the best score
		String maxSeq=getAlignedSequence(seq1,seq2);
		//removeSet.add(id1);
		removeSet.add(id2); //do not consider this sequence anymore
		allSequences.set(id1,maxSeq); // replace first sequence with new aligned sequence
		return id1;
	}
	// Perform dovetail alignment between 2 sequences and return score
	public int getDovetailScore(String s1,String s2){
		int l1=s1.length();
		int l2=s2.length();
		int maxscore =0;
		
		//// quadratic space
		//int[][] dp=new int[l1+1][l2+1];
		//// base cases - in java already 0
		////for(int i=0;i<Math.max(l1,l2);i++) {
		////	if(i<l1) dp[i][0]=0;
		////	if(i<l2) dp[0][i]=0;
		////}
		//// Main
		//for(int i=1;i<=l1;i++){
		//	for(int j=1;j<=l2;j++){
		//		if(s1.charAt(i-1)==s2.charAt(j-1)) {
		//			dp[i][j]=dp[i-1][j-1]+matchVal;
		//		} else {
		//			dp[i][j]=Math.max(dp[i-1][j-1]+replaceVal,
		//					Math.max(dp[i-1][j] + delinVal,
		//							 dp[i][j-1]+delinVal));
		//		}
		//		// keep track of max number in last column or last row
		//		if(i==l1) maxscore=Math.max(maxscore,dp[i][j]);
		//		if(j==l2) maxscore=Math.max(maxscore,dp[i][j]);
		//	}
		//}

		// Can reduce space since we aren't back tracking now
		// Linear space
		int[] prevRow=new int[l2+1];
		int[] currRow=new int[l2+1];
		for(int i=1;i<=l1;i++){
			for(int j=1;j<=l2;j++){
				if(s1.charAt(i-1)==s2.charAt(j-1)) {
					currRow[j]=prevRow[j-1]+matchVal;
				} else {
					currRow[j]=Math.max(prevRow[j-1]+replaceVal,
							Math.max(prevRow[j] + delinVal,
									 currRow[j-1]+delinVal));
				}
				// keep track of max number in last column or last row
				if(i==l1) maxscore=Math.max(maxscore,currRow[j]);
				if(j==l2) maxscore=Math.max(maxscore,currRow[j]);
			}
			for(int j=1;j<=l2;j++){
				prevRow[j]=currRow[j];
			}
		}
		return maxscore;
	}
	// returns aligned sequence - Run backtrack just once after best 2 sequences have been found.
	public String getAlignedSequence(String s1,String s2){
		String resultSeq="";
		int l1=s1.length();
		int l2=s2.length();
		int[][] dp=new int[l1+1][l2+1];
		int maxscore =0;
		// base cases - in java already 0
		//for(int i=0;i<Math.max(l1,l2);i++) {
		//	if(i<l1) dp[i][0]=0;
		//	if(i<l2) dp[0][i]=0;
		//}
		
		// Main
		// One of this would be last row or column
		int endc=0; // column with max score
		int endr=0; // row with max score
		int startr=0; // row start of back track
		int startc=0; // row end of back track
		for(int i=1;i<=l1;i++){
			for(int j=1;j<=l2;j++){
				if(s1.charAt(i-1)==s2.charAt(j-1)) {
					dp[i][j]=dp[i-1][j-1]+matchVal;
				} else {
					dp[i][j]=Math.max(dp[i-1][j-1]+replaceVal,
							Math.max(dp[i-1][j] + delinVal,
									 dp[i][j-1]+delinVal));
				}
				// keep track of max number in last column or last row
				if(i==l1) {
					// last row
					if(maxscore<dp[i][j]){
						maxscore=Math.max(maxscore,dp[i][j]);
						endr=i;
						endc=j;
					}
				}
				if(j==l2) {
					// last column
					if(maxscore<dp[i][j]){
						maxscore=Math.max(maxscore,dp[i][j]);
						endr=i;
						endc=j;
					}
				}
			}
		}
		// Back track
		int i=endr,j=endc;
	    while(i!=0 && j!=0){
			if(s1.charAt(i-1)==s2.charAt(j-1)){
				// Match
				resultSeq=s1.charAt(i-1)+resultSeq;
				i--;
				j--;
			} else {
				// MisMatch
				if(dp[i][j]==dp[i-1][j] + delinVal){
					// skip s1
					i--;
				} else if(dp[i][j]==dp[i][j-1] + delinVal){
					// skip s2
					j--;
				} else {
					//replace
					resultSeq=s1.charAt(i-1)+resultSeq; // take from s1 or s2
					i--;
					j--;
				}
			}
		}
		startr=i;
		startc=j;
		String prefix="";
		String dovetail=resultSeq;
		String suffix="";
		if(startr==0){
			prefix=s2.substring(0,startc);
			if(endc==l2){
				if(endr<l1) suffix=s1.substring(endr+1);
			} else {
				// full first sequence eaten up
				suffix="";
			}
		} else if(startc==0){
			prefix=s1.substring(0,startr);
			if(endr==l1){
				if(endc<l2) suffix=s2.substring(endc+1);
			} else {
				// full second sequence eaten up
				suffix="";
			}
		}
		String outseq=prefix+dovetail+suffix;
		//System.out.println("curr s1 length:"+s1.length());
		//System.out.println("curr s2 length:"+s2.length());
		//System.out.println("curr dovetail length:"+dovetail.length());
		//System.out.println("curr new seq length:"+outseq.length());
		return outseq;
		//return resultSeq;
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
			writer.println(">");
			writer.println(finalSequence);
			//for(int i=0;i<allSequences.size();i++){
			//	writer.println(allSequences.get(i));
			//}
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
				/*try{
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