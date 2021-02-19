import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.codec.digest.DigestUtils;

public class SimHash {
	
	static BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
	static List<String> summaries = new  ArrayList<String>();
	static List<String> queries = new ArrayList<String>();
	
	public static String make_simhash(String text) {
		String[] terms = text.split(" ");
		int sh[] = new int[128];
		
		for(int i = 0; i < terms.length; i++) {
			String term_hash = DigestUtils.md5Hex(terms[i]);
			BigInteger inthash = new BigInteger(term_hash, 16);
			String hash_binary = inthash.toString(2);
			
			while (hash_binary.length() < 128) {
				hash_binary = "0" + hash_binary;
			}
			
			for(int j = 0; j < 128; j++) {
				if (hash_binary.charAt(j) == '1') {
					sh[j]++;
				} 
				else {
					sh[j]--;
				}
			}	
		}
		
		for(int i = 0; i < 128; i++) {
			if(sh[i] >= 0) {
				sh[i] = 1;
			}
			else {
				sh[i] = 0;
			}
		}
		
		StringBuilder sb = new StringBuilder();
		
		for( int i = 0; i < 128; i++) {
			sb.append(sh[i]);
		}
		
		return sb.toString();
	}
	
	
	public static boolean compare_summaries(String sum1, String sum2, int k) {
		int counter = 0;
		
		for (int i = 0; i < 128; i++) {
			if (sum1.charAt(i) != sum2.charAt(i)) {
				counter++;
				
				if (counter > k) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {
	
		// TODO Auto-generated method stub
		int no_of_texts = Integer.parseInt(bf.readLine().trim());
	
		for(int i = 0; i < no_of_texts; i++) {			
			summaries.add(make_simhash(bf.readLine().trim()));						
		}
		
		int no_of_queries = Integer.parseInt(bf.readLine().trim());
		
		for (int i = 0; i < no_of_queries; i++) {
			queries.add(bf.readLine().trim());
		}
			
		
		for(int i = 0; i < no_of_queries; i++) {
			int counter = 0;
			String[] tk = queries.get(i).split(" ");
			int t = Integer.parseInt(tk[0]);
			int k = Integer.parseInt(tk[1]);
			
			for(int j = 0; j < no_of_texts; j++) {
				if (j != t){
					if(compare_summaries(summaries.get(j), summaries.get(t), k)) {
						counter++;
					}
				}
			}
			
			System.out.println(counter);
			
		}
		
		
		
		
		
		
}
}