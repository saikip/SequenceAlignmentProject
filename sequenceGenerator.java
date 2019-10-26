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
public class sequenceGenerator{
	// **********************************
	// VARIABLES
	// **********************************
	// Parameters input from user
	int seqLen; // Length of first sequence
	int numSeq; // Number of total sequences including first
	double frac_a; // Fraction of nucleotide 'A' in the sequence
	double frac_c; // Fraction of nucleotide 'C' in the sequence
	double frac_g; // Fraction of nucleotide 'G' in the sequence
	double frac_t; // Fraction of nucleotide 'T' in the sequence
	double mutationProbability; // Mutation probability of a any nucleotide. Range:[0-1]
	String outfile; // Filename to write output 
	
	// Other paramaters required during the run of the program
	double fracSum; // sum of all fractions. Doesn't have to be 1.
	String firstSeq; // String to store the first sequence
	ArrayList<String> kSeqs; // ArrayList to store all k sequences
	char[] firstSeqChar; // First sequence as a character array for ease of processing
	String separator="----------------------------------------------------------------------"; // Just a separator
	
	//Constructor
	public sequenceGenerator(){}
	// Main function - Validation of arguments and init
	public static void main(String args[]) throws IOException{
		int numArgs = args.length;
		if(numArgs!=8) {
			//error
			showError("",0);
			return;
		}
		// also check number and string
		if(!isNumber(args[0])){
			showError("sequence length",1);
			return;
		} else if(!isNumber(args[1])){
			showError("fraction for a",1);
			return;
		} else if(!isNumber(args[2])){
			showError("fraction for c",1);
			return;
		} else if(!isNumber(args[3])){
			showError("fraction for g",1);
			return;
		} else if(!isNumber(args[4])){
			showError("fraction for t",1);
			return;
		} else if(!isNumber(args[5])){
			showError("number of sequences",1);
			return;
		} else if(!isDouble(args[6])){
			showError("mutation probability",2);
			return;
		} else if(!validateFile(args[7])){
			// error already shown in validate file function
			//showError("output filename",3);
			return;
		} 
		// initialize temp variables
		int n=Integer.parseInt(args[0]);
		int a=Integer.parseInt(args[1]);
		int c=Integer.parseInt(args[2]);
		int g=Integer.parseInt(args[3]);
		int t=Integer.parseInt(args[4]);
		int k=Integer.parseInt(args[5]);
		double p=Double.parseDouble(args[6]);
		// More processing for output filename
		String ofile=args[7].trim();
		String[] toks = ofile.split("\\.");
		if(toks.length==1 || (toks.length>1 && !toks[toks.length-1].equals("txt"))){
			ofile+=".txt";
		} else if (toks[toks.length-1].equals("txt") && toks[0].isEmpty()){
			// empty file - no idea why I have a different check for this - maybe to yell at the user!!!!
			ofile+=".txt";
		}
		
		//call sequenceGenerator
		sequenceGenerator sg = new sequenceGenerator();
		sg.init(n, a, c, g, t, k, p, ofile);
	}
	
	// **********************************
	// ACTUAL PROCESSING GOES HERE
	// **********************************
	
	// Initialize variables and manage process flow
	public void init(int n, int a, int c, int g, int t, int k, double p, String ofile){
		seqLen=n;
		fracSum=a+c+g+t;
		frac_a=a/fracSum;
		frac_c=c/fracSum;
		frac_g=g/fracSum;
		frac_t=t/fracSum;
		numSeq=k;
		mutationProbability=p;
		outfile=ofile;
		// Task 1: Create first sequence of length n
		getFirstSequence();
		// Task 2: Create k-1 mutated sequences
		createKminus1();
		// Task 3: Write output to file
		writeToOutfile();
		System.out.println(separator);
		System.out.println("Output written to file: " + outfile);
	}
	// Generate first sequence
	public void getFirstSequence(){
		firstSeq="";
		firstSeqChar=new char[seqLen];
		for(int i=0;i<seqLen;i++){
			String curr=generateLetter();
			firstSeq=firstSeq+curr;
			firstSeqChar[i]=curr.charAt(0);;
		}
		//System.out.println(firstSeq);
	}
	// Generate each letter
	public String generateLetter(){
		double num=0.0;
		while(num==0.0){num=Math.random();}
		if(num>0.0 && num<=frac_a){
			return "A";
		} else if(num>frac_a && num<=(frac_a+frac_c)){
			return "C";
		} else if(num>(frac_a+frac_c) && num<=(frac_a+frac_c+frac_g)){
			return "G";
		} else if(num>(frac_a+frac_c+frac_g) && num<=1){
			return "T";
		} else {
			// shouldn't be here
			// Error???
			return "A";
		}
	}
	// Generate k-1 mutated sequences
	public void createKminus1(){
		kSeqs=new ArrayList<>();
		kSeqs.add(firstSeq);
		for(int i=0;i<numSeq-1;i++){
			String curSeq = "";
			for(int j=0;j<seqLen;j++){
				curSeq = curSeq + generateMutatedLetter(firstSeqChar[j]);
			}
			kSeqs.add(curSeq);
			//System.out.println(curSeq);
		}
	}
	// Decide if mutation required for a letter and process accordingly
	public String generateMutatedLetter(char c){
		String s=Character.toString(c);
		double num=0.0;
		while(num==0.0){num=Math.random();}
		if(num>0.0 && num<=mutationProbability){
			//do mutation;
			num=0.0;
			while(num==0.0){num=Math.random();}
			if(num>0.0 && num<=0.5){
				// replace
				String newS=s;
				while(s.equals(newS)){newS=generateLetter();}
				s=newS;
			} else {
				// delete
				return "";
			}
		} else {
			//no mutation
		}
		return s;
	}
	// write sequences to file
	public void writeToOutfile(){
		try{
			/*
			Path file = Paths.get(outfile);
			Files.write(file, kSeqs, StandardCharsets.UTF_8);
			*/
			//System.out.println(outfile);
			PrintWriter writer = new PrintWriter(outfile);
			for(int i=0;i<numSeq;i++){
				writer.println(">");
				writer.println(kSeqs.get(i));
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
	// checks if input is a integer and >0
	public static boolean isNumber(String s){
		if(s.matches("\\d+") && Integer.parseInt(s)!=0) return true;
		else return false;
	}
	// checks if input is a double and >0.0 and <1.0
	public static boolean isDouble(String s) {
		return (s.matches("\\d+(\\.\\d+)?") && Double.parseDouble(s)>0.0 && Double.parseDouble(s)<1.0);  //match a number with decimal.
		//return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	// Validates filename
	public static boolean validateFile(String s){
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
	// Display error
	public static void showError(String input,int etype){
		String formatError="Format: java hw1-1 <sequence_length> <fraction_for_a> " +
						"<fraction_for_c> <fraction_for_g> <fraction_for_t> " +
						"<number_of_sequences> <mutation_probability(real_number)> <output_file_name>";
		System.out.print("ERROR:");		
		if(etype==0) {
			System.out.println("Invalid number of arguments! Try Again.");
			System.out.println(formatError);
		}
		else if(etype==1) System.out.println("The "+input+" has to be an integer number>0");
		else if(etype==2) System.out.println("The "+input+" has to be a real number>0.0 AND <1.0");
		//else System.out.println("The "+input+" has to be a valid filename");
	}
}