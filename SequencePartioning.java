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
}
