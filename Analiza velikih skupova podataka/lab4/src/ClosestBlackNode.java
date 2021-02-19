import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ClosestBlackNode {
	static int broj_cvorova;
	static int broj_bridova;
	static HashMap<Integer, Integer> tipovi_cvorova = new HashMap<Integer, Integer>();
	static HashMap<Integer, List<Integer>> susjedni_cvorovi = new HashMap<Integer, List<Integer>>();
	
	
	public static void procitaj_ulaz() throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		
		String[] linija = bf.readLine().trim().split(" ");
		broj_cvorova = Integer.parseInt(linija[0]);
		broj_bridova = Integer.parseInt(linija[1]);
		
		for(int i = 0; i < broj_cvorova; i++) {
			List<Integer> susjedi = new ArrayList<Integer>();
			susjedni_cvorovi.put(i, susjedi);
			int tip = Integer.parseInt(bf.readLine().trim());
			tipovi_cvorova.put(i, tip);
		}
		
		
		for(int i = 0; i < broj_bridova; i++) {
			String[] cvorovi = bf.readLine().trim().split(" ");
			int cvor1 = Integer.parseInt(cvorovi[0]);
			int cvor2 = Integer.parseInt(cvorovi[1]);
			
			susjedni_cvorovi.get(cvor1).add(cvor2);
			susjedni_cvorovi.get(cvor2).add(cvor1);
			
		}
	}

	public static void pronadi_najblizi(List<Integer> cvorovi, int razina) {
		
		List<Integer> susjedi = new ArrayList<Integer>();
		for(int cvor : cvorovi) {
			susjedi.addAll(susjedni_cvorovi.get(cvor));	
		}
		
		List<Integer> pronadeni_crni = new ArrayList<Integer>();	
		int brojac_razina = 0;
		Queue<Integer> red_cvorova = new LinkedList<Integer>();
		
		for(int s : susjedi) {
			brojac_razina++;
			red_cvorova.add(s);
		}
		
		for (int i = 0; i < brojac_razina; i++) {
			int cvor_za_provjeru = red_cvorova.poll();
			
			if(tipovi_cvorova.get(cvor_za_provjeru) == 1 ) {
				pronadeni_crni.add(cvor_za_provjeru);
			}
		}
		
		if (!pronadeni_crni.isEmpty()) {
			int rez = Collections.min(pronadeni_crni);
			System.out.println(rez + " " + razina);
			return;
		}
		
		else {
			if(razina == 10) {
				System.out.println("-1 -1");
				return;
			}
			else {
				pronadi_najblizi(susjedi, razina + 1);
				return;
			}
		}
		
		
		
	}
	
	public static void ispisi_najblize() {
	
		for(int cvor = 0; cvor < broj_cvorova; cvor++) {
			if (tipovi_cvorova.get(cvor) == 1) {
				System.out.println(cvor + " " + "0");
			}
			
			else {
				List<Integer> lista = new ArrayList<Integer>();
				lista.add(cvor);
				pronadi_najblizi(lista, 1);
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		procitaj_ulaz();
		ispisi_najblize();
		
	}
}
