import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PCY {
	static BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
	static int br_kosara;
	static int br_pretinaca;
	static float s;
	static List<List<Integer>> kosare = new ArrayList<List<Integer>>();
	static int prag;
	static HashMap<Integer, Integer> brojac_predmeta = new HashMap<Integer, Integer>();
	static HashMap<Integer, Integer> pretinci_brojac = new HashMap<Integer, Integer>();
	static HashMap<List<Integer>, Integer> broj_ponavljanja_par = new HashMap<List<Integer>, Integer>();
	
	public static void procitaj_ulaz() throws NumberFormatException, IOException {
				
		br_kosara = Integer.parseInt(bf.readLine().trim());
		s = Float.parseFloat(bf.readLine().trim());
		br_pretinaca = Integer.parseInt(bf.readLine());
		prag = (int) (s * br_kosara);
		
		for (int i = 0; i < br_kosara; i++) {
			List<String> p = Arrays.asList(bf.readLine().trim().split(" "));
			List<Integer> predmeti = new ArrayList<Integer>();
			p.forEach(x -> predmeti.add(Integer.parseInt(x)));
			kosare.add(predmeti);
			
			for(Integer predmet : predmeti) {
				
				if (brojac_predmeta.containsKey(predmet)) {
					brojac_predmeta.put(predmet, brojac_predmeta.get(predmet) + 1 );
				}
				else {
					brojac_predmeta.put(predmet, 1);
				}
			}
			
		}		

	}
		
	
	public static void drugi_prolaz() {
		
		int broj_predmeta = brojac_predmeta.keySet().size();
		
		for(List<Integer> kosara : kosare) {
			for(int i = 0; i < kosara.size(); i++) {
				for(int j = i + 1; j < kosara.size(); j++) {
					int prvi_predmet = kosara.get(i);
					int drugi_predmet = kosara.get(j);
					
					if(brojac_predmeta.get(prvi_predmet) >= prag && brojac_predmeta.get(drugi_predmet) >= prag) {
						int k = ((prvi_predmet * broj_predmeta) + drugi_predmet) % br_pretinaca;
						if (pretinci_brojac.containsKey(k)){
							pretinci_brojac.put(k, pretinci_brojac.get(k) + 1);			
						}
						else {
							pretinci_brojac.put(k, 1);
						}
					}
				
				}
			}
		}
	}
	
	public static void treci_prolaz() {
		int broj_predmeta = brojac_predmeta.keySet().size();
		
		for(List<Integer> kosara : kosare) {
			for(int i = 0; i < kosara.size(); i++) {
				for(int j = i + 1; j < kosara.size(); j++) {
					int prvi_predmet = kosara.get(i);
					int drugi_predmet = kosara.get(j);
					
					if(brojac_predmeta.get(prvi_predmet) >= prag && brojac_predmeta.get(drugi_predmet) >= prag) {
						int k = ((prvi_predmet * broj_predmeta) + drugi_predmet) % br_pretinaca;
						if (pretinci_brojac.get(k) >= prag) {
							List<Integer> par = new ArrayList<Integer>();
							par.add(prvi_predmet);
							par.add(drugi_predmet);
							
							if (broj_ponavljanja_par.containsKey(par)){
								broj_ponavljanja_par.put(par, broj_ponavljanja_par.get(par) + 1);			
							}
							else {
								broj_ponavljanja_par.put(par, 1);
							}
						}
					}
				}
			}
		}
	}
	
	public static int broj_cestih_predmeta() {
		int brojac = 0;
		
		for(int broj : brojac_predmeta.values()) {
			if (broj >= prag) {
				brojac++;
			}
		}
		
		return brojac;
	}
	
	public static void ispisi_izlaz() {
		int m = broj_cestih_predmeta();
		
		System.out.println((m * (m - 1)) / 2);
		System.out.println(broj_ponavljanja_par.keySet().size());
		List<Integer> ponavljanja = new ArrayList<Integer>();
		
		for(int broj : broj_ponavljanja_par.values()) {
			ponavljanja.add(broj);
		}
		
		Collections.sort(ponavljanja, Collections.reverseOrder());
		
		for(int p : ponavljanja) {
			if(p >= prag)
				System.out.println(p);
		}
		
		
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		
		procitaj_ulaz();
		drugi_prolaz();
		treci_prolaz();
		ispisi_izlaz();

	}

}
