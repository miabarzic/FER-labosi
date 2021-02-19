import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class CF {
	static int broj_stavki;
	static int broj_korisnika;
	static int broj_upita;
	static HashMap<Integer, List<Integer>> predmeti = new HashMap<Integer, List<Integer>>();
	static HashMap<Integer, List<Integer>> korisnici = new HashMap<Integer, List<Integer>>();
	static HashMap<Integer, List<Integer>> upiti = new HashMap<Integer, List<Integer>>();
	static HashMap<Integer, List<Float>> korisnici_razlika = new HashMap<Integer, List<Float>>();
	static HashMap<Integer, List<Float>> predmeti_razlika = new HashMap<Integer, List<Float>>();
	static HashMap<Integer, Float> korisnici_stddev = new HashMap<Integer, Float>();
	static HashMap<Integer, Float> predmeti_stddev = new HashMap<Integer, Float>();
	
	public static void procitaj_ulaz() throws IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		
	
		String[] linija = bf.readLine().trim().split(" ");
		broj_stavki = Integer.parseInt(linija[0]);
		broj_korisnika = Integer.parseInt(linija[1]);
		
		for (int i = 1; i <= broj_stavki; i++) {
			
			List<String> u = Arrays.asList(bf.readLine().trim().split(" "));
			List<Integer> ocjene = new ArrayList<Integer>();
			
			u.forEach(x -> {
				if (x.equals("X")){
					
					ocjene.add(0);
				}
				else {
					ocjene.add(Integer.parseInt(x));
				}
			});
			
			predmeti.put(i, ocjene);
		}
		
		for(int i = 1; i <= broj_korisnika; i++) {
			List<Integer> korisnik_ocjene = new ArrayList<Integer>();
			
			for(int j = 1; j <= broj_stavki; j++) {
				korisnik_ocjene.add(predmeti.get(j).get(i - 1));
			}
			
			korisnici.put(i, korisnik_ocjene);
		}
		
		broj_upita = Integer.parseInt(bf.readLine().trim());
		
		for (int i = 0; i < broj_upita; i++) {
			
			String l = bf.readLine().trim();
			List<String> up = Arrays.asList(l.split(" "));
			List<Integer> upit = new ArrayList<Integer>();	
			up.forEach(x -> upit.add(Integer.parseInt(x)));
			upiti.put(i, upit);
					
		}		
	}
	
	public static void izracunaj_razlike_korisnik() {
		
		for(int i = 1; i <= broj_korisnika; i++) {
			float brojac = 0;
			float zbroj = 0;
			float stddev_zbroj = 0;
			
			for(int ocjena : korisnici.get(i)) {
				if (ocjena > 0) {
					brojac++;
					zbroj += (float) ocjena;
				}
			}
			
			float srednja_ocjena = zbroj/brojac;
			
			List<Float> razlike = new ArrayList<Float>();
			
			for(int o: korisnici.get(i)) {
				if(o > 0) {
					float razlika;
					razlika = (float) o - srednja_ocjena;
					stddev_zbroj += Math.pow(razlika, 2);
					razlike.add(razlika);
				}
				else {
					razlike.add((float) 0);
				}
			}
			
			korisnici_stddev.put(i, stddev_zbroj);
			korisnici_razlika.put(i, razlike);
		}
	}
	
	public static void izracunaj_razlike_predmet() {
		
		for(int i = 1; i <= broj_stavki; i++) {
			float brojac = 0;
			float zbroj = 0;
			float stddev_zbroj = 0;
			
			for(int ocjena : predmeti.get(i)) {
				if(ocjena > 0) {
					brojac++;
					zbroj += (float) ocjena;
				}
			}
		
			float srednja_ocjena = zbroj/brojac;
		
			List<Float> razlike = new ArrayList<Float>();
			for(int o : predmeti.get(i)) {
				if(o > 0) {
					float razlika;
					razlika = (float) o - srednja_ocjena;
					stddev_zbroj += Math.pow(razlika, 2);
					razlike.add(razlika);
				}
				else {
					razlike.add((float) 0);
				}
			}
			
			predmeti_razlika.put(i, razlike);
			predmeti_stddev.put(i, stddev_zbroj);
		}
		
	}
	
	public static void user_user(int id_korisnika, int id_predmeta, int broj_najslicnijih) {
		HashMap<Integer, Float> slicnosti = new HashMap<Integer, Float>();
		
		for(int i = 1; i <= broj_korisnika; i++) {
			float brojnik = 0;
			float nazivnik;
			
			if(i != id_korisnika) {
				float slicnost;
				for(int j = 0; j < broj_stavki; j++) {
					float razlika_1 = korisnici_razlika.get(id_korisnika).get(j);
					float razlika_2 = korisnici_razlika.get(i).get(j);
					brojnik += razlika_1 * razlika_2;
				}
				
				
				nazivnik = korisnici_stddev.get(id_korisnika) * korisnici_stddev.get(i);
				nazivnik = (float) Math.pow(nazivnik, 0.5);
				
				slicnost = brojnik/nazivnik;
				if(slicnost > 0) {
					slicnosti.put(i, slicnost);
					
				}
			}
		}
			List<Integer> slicni_korisnici = new ArrayList<Integer>(slicnosti.keySet());
			
			slicni_korisnici.sort(new Comparator<Integer>() {
				public int compare(Integer key1, Integer key2) {
					if (slicnosti.get(key2) > slicnosti.get(key1))
							return 1;
					if(slicnosti.get(key2) < slicnosti.get(key1)) {
						return -1;
					}
					
					else
						return 0;
				}
			});
			
			
			float zbroj_ocjena = (float) 0;
			float brojac = (float) 0;
			int b = 0;
			for(int sk : slicni_korisnici) {
				if(korisnici.get(sk).get(id_predmeta - 1) != 0) {
					zbroj_ocjena += korisnici.get(sk).get(id_predmeta - 1) * slicnosti.get(sk);
					brojac += slicnosti.get(sk);
					b++;
					if (b == broj_najslicnijih) {
						break;
					}
				}
			}
			
			DecimalFormat df = new DecimalFormat("#.000");
			BigDecimal bd = new BigDecimal(zbroj_ocjena/brojac);
			BigDecimal res = bd.setScale(3, RoundingMode.HALF_UP);
			System.out.println(df.format(res));
			
			
			
			
			
			
		}
		
		
	public static void item_item(int id_korisnika, int id_predmeta, int broj_najslicnijih) {
		HashMap<Integer, Float> slicnosti = new HashMap<Integer, Float>();
		
		for(int i = 1; i <= broj_stavki; i++) {
			
			float brojnik = 0;
			float nazivnik;
			
			if(i != id_predmeta) {
				
				float slicnost;
				for(int j = 0; j < broj_korisnika; j++) {
					float razlika_1 = predmeti_razlika.get(id_predmeta).get(j);
					float razlika_2 = predmeti_razlika.get(i).get(j);
					brojnik += razlika_1 * razlika_2;
				}
				
				nazivnik = predmeti_stddev.get(id_predmeta) * predmeti_stddev.get(i);
				nazivnik = (float) Math.pow(nazivnik, 0.5);
				
				slicnost = brojnik/nazivnik;
				if(slicnost > 0) {
					slicnosti.put(i, slicnost);
					
				}
			}
		}
			
			List<Integer> slicni_predmeti = new ArrayList<Integer>(slicnosti.keySet());
			
			slicni_predmeti.sort(new Comparator<Integer>() {
				public int compare(Integer key1, Integer key2) {
					if (slicnosti.get(key2) > slicnosti.get(key1))
							return 1;
					if(slicnosti.get(key2) < slicnosti.get(key1)) {
						return -1;
					}
					
					else
						return 0;
				}
			});
			
			
			
			float zbroj_ocjena = (float) 0;
			float broj_predmeta = (float) 0;
			int brojac = 0;
			
			for(int sk : slicni_predmeti) {
				if(predmeti.get(sk).get(id_korisnika - 1) != 0) {
					zbroj_ocjena += predmeti.get(sk).get(id_korisnika - 1) * slicnosti.get(sk);
					broj_predmeta += slicnosti.get(sk);
					brojac++;
					if (brojac == broj_najslicnijih) {
						break;
					}
				}
			}
			
			
			DecimalFormat df = new DecimalFormat("#.000");
			BigDecimal bd = new BigDecimal(zbroj_ocjena/broj_predmeta);
			BigDecimal res = bd.setScale(3, RoundingMode.HALF_UP);
			System.out.println(df.format(res));		
			
		}
		
	
	
	public static void obradi_upite() {
		for(int i = 0; i < broj_upita; i++) {
			int id_stavke = upiti.get(i).get(0);
			int id_korisnika = upiti.get(i).get(1);
			int tip_algoritma = upiti.get(i).get(2);
			int broj_slicnih = upiti.get(i).get(3);
			
			if(tip_algoritma == 0) {
				item_item(id_korisnika, id_stavke, broj_slicnih);
			}
			else {
				user_user(id_korisnika, id_stavke, broj_slicnih);
			}
			
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		procitaj_ulaz();		
		izracunaj_razlike_korisnik();
		izracunaj_razlike_predmet();
		obradi_upite();	
	}	
	
}
