import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class GNAlgorithm {
	static HashMap<Integer, List<Integer>> susjedni_cvorovi = new HashMap<Integer, List<Integer>>();
	static HashMap<Integer, List<Integer>> svojstva_korisnika = new HashMap<Integer, List<Integer>>();
	static HashMap<List<Integer>, Integer> tezine_bridova = new HashMap<List<Integer>, Integer>(); 
	static HashMap<Integer, Double> modularnosti = new HashMap<Integer, Double>();

	public static void procitaj_ulaz() throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		
		String line = bf.readLine().trim();
		
		while (!line.isEmpty()) {
			
			String[] cvorovi = line.split(" ");
			int cvor1 = Integer.parseInt(cvorovi[0]);
			int cvor2 = Integer.parseInt(cvorovi[1]);
			
			if (susjedni_cvorovi.containsKey(cvor1)) {
				susjedni_cvorovi.get(cvor1).add(cvor2);
			}
			else {
				List<Integer> lista_susjeda = new ArrayList<Integer>();
				lista_susjeda.add(cvor2);
				susjedni_cvorovi.put(cvor1, lista_susjeda);
			}
			
			if (susjedni_cvorovi.containsKey(cvor2)) {
				susjedni_cvorovi.get(cvor2).add(cvor1);
			}
			else {
				List<Integer> lista_susjeda = new ArrayList<Integer>();
				lista_susjeda.add(cvor1);
				susjedni_cvorovi.put(cvor2, lista_susjeda);
			}
			
			line =bf.readLine().trim();						
		}
			
		while (bf.ready()) {
			line = bf.readLine().trim();
			String[] svojstva = line.split(" ");		
			int indeks = Integer.parseInt(svojstva[0]);
			
			List<Integer> vektor_svojstava = new ArrayList<Integer>();
			
			for(int i = 1; i < svojstva.length; i++) {
				vektor_svojstava.add(Integer.parseInt(svojstva[i]));
			}
			
			svojstva_korisnika.put(indeks, vektor_svojstava);
		}
		
		for (int korisnik : svojstva_korisnika.keySet()) {
			int maksimalna_slicnost = svojstva_korisnika.get(korisnik).size();
			
			if (susjedni_cvorovi.containsKey(korisnik)) {
				for (int susjed : susjedni_cvorovi.get(korisnik)) {
					List<Integer> kljuc = new ArrayList<Integer>();
					kljuc.add(korisnik);
					kljuc.add(susjed);
					int brojac = 0;
				
					for (int i = 0; i < maksimalna_slicnost; i++) {
						if (svojstva_korisnika.get(korisnik).get(i) == svojstva_korisnika.get(susjed).get(i)) {
							brojac++;
						}
					}
				
					int tezina = maksimalna_slicnost - brojac + 1;
					tezine_bridova.put(kljuc, tezina);				
				}
			}
		}
	}
	
	
	public static List<List<Integer>> pronadi_zajednice() {
		List<Integer> posjeceni_cvorovi = new ArrayList<Integer>();
		List<List<Integer>> zajednice = new ArrayList<List<Integer>>();
		
		for (int cvor : svojstva_korisnika.keySet()) {
			
			if (!posjeceni_cvorovi.contains(cvor)) {
				posjeceni_cvorovi.add(cvor);
				List<Integer> zajednica = new ArrayList<Integer>();
				zajednica.add(cvor);
				
				if (susjedni_cvorovi.containsKey(cvor)) {
					List<Integer> susjedi = new ArrayList<Integer>();
					susjedi.addAll(susjedni_cvorovi.get(cvor));
					pronadi_novu_zajednicu(zajednica, posjeceni_cvorovi, susjedi);
				}
				
				zajednice.add(zajednica);
			}					
		}
	
		return zajednice;
	}
	
	public static void pronadi_novu_zajednicu(List<Integer> zajednica, List<Integer> posjeceni_cvorovi, List<Integer> cvorovi) {
		if (cvorovi.isEmpty()) {
			return;
		}
		
		List<Integer> susjedi = new ArrayList<Integer>(); 
		
		for(int cvor : cvorovi) {
			if (!posjeceni_cvorovi.contains(cvor)) {
				posjeceni_cvorovi.add(cvor);
				zajednica.add(cvor);
				susjedi.addAll(susjedni_cvorovi.get(cvor));			
			}
		}
		
		pronadi_novu_zajednicu(zajednica, posjeceni_cvorovi, susjedi);	
	}
	
	public static int izracunaj_ukupnu_tezinu_svih_izlaznih_bridova(int cvor) {
		
		if (!susjedni_cvorovi.containsKey(cvor)) {
			return 0;
		}
		
		int ukupna_tezina = 0;
		
		for(int susjed : susjedni_cvorovi.get(cvor)) {
			List<Integer> kljuc = new ArrayList<Integer>();
			kljuc.add(cvor);
			kljuc.add(susjed);
			
			ukupna_tezina += tezine_bridova.get(kljuc);
		}
		
		return ukupna_tezina;
	}
	
	public static int provjeri_jesu_li_u_i_v_u_istoj_zajednici(int cvoru, int cvorv, List<List<Integer>> zajednice) {
		
		for(List<Integer> zajednica : zajednice) {
			if (zajednica.contains(cvoru) && zajednica.contains(cvorv)) {
				return 1;
			}
			
			else if (zajednica.contains(cvoru) || zajednica.contains(cvorv)) {
				return 0;
			}
		}
		return 0;
	}
	
	public static double izracunaj_modularnost(List<List<Integer>> zajednice) {
		double suma = 0;
		double modularnost;
		int m;	
		m = tezine_bridova.values().stream().reduce(0, (zbroj, tezina) -> zbroj + tezina);
		m /= 2;
	
		
		for (int cvorv : svojstva_korisnika.keySet()) {
			int kv = izracunaj_ukupnu_tezinu_svih_izlaznih_bridova(cvorv);
			
			for(int cvoru : svojstva_korisnika.keySet()) {
				int ista_zajednica = provjeri_jesu_li_u_i_v_u_istoj_zajednici(cvorv,  cvoru, zajednice);
				
				if (ista_zajednica == 0)
						continue;
				
				int ku = izracunaj_ukupnu_tezinu_svih_izlaznih_bridova(cvoru);
				
				int A = 0;
				
				List<Integer> kljuc = new ArrayList<Integer>();
				kljuc.add(cvorv);
				kljuc.add(cvoru);
				
				if(tezine_bridova.containsKey(kljuc)) {
					A = tezine_bridova.get(kljuc);
				}
				suma += ((double) A - (double)(ku * kv) / (2 * m)) * ista_zajednica;
			}
		}
		
		modularnost = ( 1 / (double) (2 * m) ) * suma;
		
		if(Math.abs(modularnost) < Math.pow(10, -5)) {
			modularnost = 0;
		}
		
		BigDecimal bd = new BigDecimal(Double.toString(modularnost));
		bd = bd.setScale(4, RoundingMode.HALF_UP);
		modularnost = bd.doubleValue();
		return modularnost;
	}
	
	public static HashMap<Integer, List<List<Integer>>> pronadi_sve_puteve(int cvor) {
		
			HashMap<Integer, List<List<Integer>>> svi_putevi = new HashMap<Integer, List<List<Integer>>>();
			
			if (susjedni_cvorovi.containsKey(cvor)) {
				
				List<List<Integer>> trenutni_putevi = new ArrayList<List<Integer>>();
				List<Integer> put = new ArrayList<Integer>();
				put.add(0);
				put.add(cvor);
				trenutni_putevi.add(put);
				pronadi_put(trenutni_putevi, svi_putevi);			
					
				}
			
			return svi_putevi;
		
		}
	
	public static void pronadi_put(List<List<Integer>>  putevi, HashMap<Integer, List<List<Integer>>> svi_putevi) {
		
		if (putevi.isEmpty())
			return;
		List<List<Integer>> novi_putevi = new ArrayList<List<Integer>>();
		
		for(List<Integer> put : putevi) {
			int trenutna_duljina = put.get(0);
			int zadnji_cvor = put.get(put.size() - 1);
			
			if (susjedni_cvorovi.containsKey(zadnji_cvor)) {
				
				for(int susjed : susjedni_cvorovi.get(zadnji_cvor)) {
					if (!put.subList(1, put.size()).contains(susjed)) {
						List<Integer> novi_put = new ArrayList<Integer>(put);
						novi_put.add(susjed);
						
						List<Integer> kljuc = new ArrayList<Integer>();
						kljuc.add(zadnji_cvor);
						kljuc.add(susjed);
						
						int nova_duljina = trenutna_duljina + tezine_bridova.get(kljuc);
						novi_put.set(0, nova_duljina);
						
						if (svi_putevi.containsKey(susjed)){
							svi_putevi.get(susjed).add(novi_put);
						}
						
						else {
							List<List<Integer>> p = new ArrayList<List<Integer>>();
							p.add(novi_put);
							svi_putevi.put(susjed, p);
						}
						
						novi_putevi.add(novi_put);
					}
				}
			}
			
		}
		
		pronadi_put(novi_putevi, svi_putevi);
		
		
	}

	public static int pronadi_duljinu_najkraceg_puta(List<List<Integer>> putevi) {
		int min = Integer.MAX_VALUE;
		
		for(List<Integer> put : putevi) {
			if (put.get(0) < min)
				min = put.get(0);
		}
		
		return min;
	}
	
	public static List<List<Integer>> nadi_najkrace_puteve(List<List<Integer>> putevi, int duljina_najkraceg_puta){
		List<List<Integer>> najkraci_putevi = new ArrayList<List<Integer>>();
		
		for(List<Integer> put : putevi) {
			if (put.get(0) == duljina_najkraceg_puta) {
				najkraci_putevi.add(put);
			}
		}
		
		return najkraci_putevi;
	}
	public static HashMap<List<Integer>, Double> izracunaj_bridnu_centralnost() {
		
		HashMap<List<Integer>, Double> bridovi_i_centralnosti = new HashMap<List<Integer>, Double>();
		for (List<Integer> kljuc : tezine_bridova.keySet()) {
			bridovi_i_centralnosti.put(kljuc, (double) 0);
		}
		
		for (int cvor : svojstva_korisnika.keySet()) {
			HashMap<Integer, List<List<Integer>>> putevi_iz_cvora = new HashMap<Integer, List<List<Integer>>>();
			putevi_iz_cvora = pronadi_sve_puteve(cvor);
			
			if(!putevi_iz_cvora.isEmpty()) {
				for(int korisnik : putevi_iz_cvora.keySet()) {
					
					if (putevi_iz_cvora.get(korisnik).size() == 1) {
						
						for(int i = 1; i < putevi_iz_cvora.get(korisnik).get(0).size() - 1; i++) {
							List<Integer> brid = new ArrayList<Integer>();
							brid.add(putevi_iz_cvora.get(korisnik).get(0).get(i));
							brid.add(putevi_iz_cvora.get(korisnik).get(0).get(i + 1));
							bridovi_i_centralnosti.put(brid, bridovi_i_centralnosti.get(brid) + 1);
							
						}
					}
					
					else {
						int duljina_najkraceg_puta = pronadi_duljinu_najkraceg_puta(putevi_iz_cvora.get(korisnik));
						List<List<Integer>> najkraci_putevi = new ArrayList<List<Integer>>();
						najkraci_putevi = nadi_najkrace_puteve(putevi_iz_cvora.get(korisnik), duljina_najkraceg_puta);
						int broj_najkracih_puteva = najkraci_putevi.size();
						
						double c = 1 / (double) broj_najkracih_puteva;
						BigDecimal bd = new BigDecimal(Double.toString(c));
						bd = bd.setScale(4, RoundingMode.HALF_UP);
						c = bd.doubleValue();
						
						for(List<Integer> put : najkraci_putevi) {
							for(int i = 1; i < put.size() - 1; i++) {
								List<Integer> brid = new ArrayList<Integer>();
								brid.add(put.get(i));
								brid.add(put.get(i + 1));
								bridovi_i_centralnosti.put(brid, bridovi_i_centralnosti.get(brid) + c);
							}
						}
						
					}
				}
			}
		}		
		
		return bridovi_i_centralnosti;
	}
	
	
	public static List<List<Integer>> pronadi_bridove_s_najvecom_centralnosti(HashMap<List<Integer>, Double> bridovi_i_centralnosti){
		List<List<Integer>> bridovi = new ArrayList<List<Integer>>();
		
		double najveca_centralnost = Collections.max(bridovi_i_centralnosti.values());
		for(List<Integer> kljuc : bridovi_i_centralnosti.keySet()) {
			if (bridovi_i_centralnosti.get(kljuc) == najveca_centralnost) {
				bridovi.add(kljuc);
			}
		}
		return bridovi;
	}
	public static void girman_newman() {
		HashMap<Integer, List<List<Integer>>> zajednice_po_iteracijama = new HashMap<Integer, List<List<Integer>>>();
		HashMap<Integer, Double> modularnosti_po_iteracijama = new HashMap<Integer, Double>();
		int brojac_iteracija = 0;
		
		while (true){
			
			List<List<Integer>> zajednice = pronadi_zajednice();
			
			if(zajednice.size() == svojstva_korisnika.keySet().size()) {
				break;
			}
			zajednice_po_iteracijama.put(brojac_iteracija, zajednice);
			double modularanost = izracunaj_modularnost(zajednice);
			modularnosti_po_iteracijama.put(brojac_iteracija, modularanost);
			brojac_iteracija++;
				
			HashMap<List<Integer>, Double> bridovi_i_centralnosti = new HashMap<List<Integer>, Double>();
			
			bridovi_i_centralnosti = izracunaj_bridnu_centralnost();
			List<List<Integer>> bridovi_s_najvecom_centralnosti = new ArrayList<List<Integer>>();
			bridovi_s_najvecom_centralnosti = pronadi_bridove_s_najvecom_centralnosti(bridovi_i_centralnosti);
			
			for(List<Integer> brid : bridovi_s_najvecom_centralnosti) {
				tezine_bridova.remove(brid);
				susjedni_cvorovi.get(brid.get(0)).remove(brid.get(1));
			}
			
			bridovi_s_najvecom_centralnosti.sort(new Comparator<List<Integer>>() {
				public int compare(List<Integer> lista1, List<Integer> lista2) {
					if (lista1.get(0) > lista2.get(0))
						return 1;
					if(lista1.get(0) < lista2.get(0)) 
						return -1;
					if(lista1.get(1) > lista2.get(1))
						return 1;
					if(lista1.get(1) < lista2.get(1))
						return -1;
					else
						return 0;
				}
			});
		
			for(List<Integer> b : bridovi_s_najvecom_centralnosti) {
				if (b.get(0) < b.get(1))
					System.out.println(b.get(0) + " "+ b.get(1));
			}
			
		}
		
		double najveca_modularnost = Collections.max(modularnosti_po_iteracijama.values());
		int najveca_iteracija = 0;
		for(int i : modularnosti_po_iteracijama.keySet()) {
			if (modularnosti_po_iteracijama.get(i) == najveca_modularnost) {
				najveca_iteracija = i;
				break;
			}
		}
		
		List<List<Integer>> najbolja_zajednica = zajednice_po_iteracijama.get(najveca_iteracija);
		
		najbolja_zajednica.sort(new Comparator<List<Integer>>() {
			public int compare(List<Integer> zajednica1, List<Integer> zajednica2) {
				if (zajednica1.size() > zajednica2.size())
					return 1;
				if (zajednica1.size() < zajednica2.size())
					return -1;
				if(Collections.min(zajednica1) > Collections.min(zajednica2))
					return 1;
				if(Collections.min(zajednica1) < Collections.min(zajednica2)) 
					return -1;
				else
					return 0;
			}
		});
		
		StringBuilder sb = new StringBuilder();
		for(List<Integer> z : najbolja_zajednica) {
			Collections.sort(z);
			for(int korisnik : z) {
				sb.append(korisnik);
				sb.append("-");
			}
			
			sb.deleteCharAt(sb.length() - 1);
			sb.append(" ");
		}
		sb.deleteCharAt(sb.length() - 1);
		System.out.println(sb.toString());
		
		
		
	}
	public static void main(String[] args) throws IOException {
		procitaj_ulaz();
		girman_newman();
		
	}

}
