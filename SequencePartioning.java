public class Main
{
	public static void main(String[] args) {
	    String outfile;
	    String seq="ACTAACTAGCGTCCTGA";
	    int min=3;
	    int max=7;
	    int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
	    int seqLen=seq.length();
	    System.out.println(seq.length());
		System.out.println(randomNum);
		String subSeq="";
		for(int i=0;i<seqLen;i++)
		{
		    
		    if(subSeq.length()==randomNum)
		    {
		        System.out.println(subSeq);
		        subSeq="";
		    }
		    subSeq=subSeq+seq.charAt(i);
		}
		if (subSeq.length()>=min)
		{
		    System.out.println(subSeq);
		}
		
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
	
	
}
