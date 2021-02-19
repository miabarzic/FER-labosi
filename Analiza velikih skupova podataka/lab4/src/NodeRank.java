import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class NodeRank {
	static int broj_cvorova;
	static double beta;
	static HashMap<Integer, List<Integer>> susjedni_cvorovi = new HashMap<Integer, List<Integer>>();
	static HashMap<Integer, List<Integer>> ulazni_cvorovi = new HashMap<Integer, List<Integer>>();
	static int broj_upita;
	static List<List<Integer>> upiti = new ArrayList<List<Integer>>();
	static HashMap<Integer, List<Double>> iteracije = new HashMap<Integer, List<Double>>();
	
	public static void procitaj_ulaz() throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		
		String[] line = bf.readLine().trim().split(" ");		
		broj_cvorova = Integer.parseInt(line[0]);
		beta = Double.parseDouble(line[1]);
		
		for (int i = 0; i < broj_cvorova; i++) {
			List<Integer> ulaz = new ArrayList<Integer>();
			ulazni_cvorovi.put(i, ulaz);
		}
		
		for(int i = 0; i < broj_cvorova; i++) {
			
			List<Integer> susjedi = new ArrayList<Integer>();
			Arrays.asList(bf.readLine().trim().split(" ")).forEach(x -> susjedi.add(Integer.parseInt(x)));
			susjedni_cvorovi.put(i, susjedi);
			
			for(int susjed : susjedi) {
				ulazni_cvorovi.get(susjed).add(i);
			}
		}
		
		broj_upita = Integer.parseInt(bf.readLine().trim());
		
		for(int i = 0; i < broj_upita; i++) {
			List<Integer> upit = new ArrayList<Integer>();
			Arrays.asList(bf.readLine().trim().split(" ")).forEach(x -> upit.add(Integer.parseInt(x)));
			upiti.add(upit);
			
		}
				
		
	}
	
	public static void page_rank() {
		List<Double> rang = new ArrayList<Double>();
		for(int i = 0; i < broj_cvorova; i++) {
			rang.add((double) 1/ (double) broj_cvorova);
		}
		
		iteracije.put(0, rang);
		int najveca_iteracija = 0;
		
		for (List<Integer> upit : upiti) {
			
			int cvor = upit.get(0);
			int broj_iteracije = upit.get(1);
			
			
			//System.out.println(broj_iteracije);
			//System.out.println(najveca_iteracija);
			if(broj_iteracije <= najveca_iteracija) {
				System.out.printf("%.10f\n", iteracije.get(broj_iteracije).get(cvor));
			}
			else {
				for(int i = najveca_iteracija + 1; i <= broj_iteracije; i++ ) {
					float s = 0;
					List<Double> novi_rang = new ArrayList<Double>();
				
					for (int j = 0; j < broj_cvorova; j++) {
						double suma_znacaja = (1 - beta)/(float) broj_cvorova;
						
						for(int u : ulazni_cvorovi.get(j)) {
							suma_znacaja += beta * (iteracije.get(i - 1).get(u)/(double)susjedni_cvorovi.get(u).size());						
						}				
						
						novi_rang.add(suma_znacaja);
					
					}
					
					iteracije.put(i, novi_rang);
				}
				System.out.printf("%.10f\n", iteracije.get(broj_iteracije).get(cvor));
				//System.out.println(broj_iteracije);
				//System.out.println(najveca_iteracija);
				najveca_iteracija = broj_iteracije;			
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		procitaj_ulaz();
		page_rank();
	}
}
