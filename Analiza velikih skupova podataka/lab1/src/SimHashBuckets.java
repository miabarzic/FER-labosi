
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;

public class SimHashBuckets {
		
	static BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
	static List<String> summaries = new  ArrayList<String>();

		
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
	
	public static HashMap<Integer, Set<Integer>> lsh(List<String> summaries){
		HashMap<Integer, Set<Integer>> candidates = new HashMap<Integer, Set<Integer>>();
		
		for (int i = 0; i < summaries.size(); i++) {
			Set<Integer> noviset = new HashSet<Integer>();
			candidates.put(i, noviset);
		}
		for (int i = 0; i < 8; i++) {
			HashMap<Integer, List<Integer>> boxes = new HashMap<Integer, List<Integer>>();
			
			for(int j = 0; j < summaries.size(); j++) {
				
				int value = Integer.parseInt(summaries.get(j).substring(i * 16, (i + 1) *16), 2);
				
				if (boxes.containsKey(value)) {
					
					for(int m = 0; m < boxes.get(value).size(); m++) {
						int id = boxes.get(value).get(m);
						candidates.get(j).add(id);
						candidates.get(id).add(j);
					}
					
					boxes.get(value).add(j);
				}
				
				else {
					List<Integer> novalista = new ArrayList<Integer>();
					novalista.add(j);
					boxes.put(value, novalista);
				}
				
				
				
				
			}
		}
		
		return candidates;
		
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
		
		HashMap<Integer, Set<Integer>> candidates = lsh(summaries);
			
		for(int i = 0; i < no_of_queries; i++) {
			int counter = 0;
			String[] tk = bf.readLine().trim().split(" ");
			int t = Integer.parseInt(tk[0]);
			int k = Integer.parseInt(tk[1]);
			
			for(Integer entry:candidates.get(t)) {
				if(compare_summaries(summaries.get(entry), summaries.get(t), k)) {
					counter++;
				}
			}
	
			
			System.out.println(counter);
				
		}
			
			
			
			
			
			
	}
}
	

