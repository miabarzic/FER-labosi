#potez računala -> oznaka 1
#potez korisnika -> oznaka 2
#ploca.txt dimenzija br_redova * br_stupaca
#u igri igrac moze staviti u stupac od 0 do br_stupaca - 1
import copy

ispis = { 0: '=', 1: 'C', 2: 'P'}

class Board:

    def __init__(self):
        ploca = []
        #ploca = [[1, 2, 0, 0, 0, 1, 0], [2, 2, 1, 1, 1, 2, 1], [1, 1, 2, 2, 2, 1, 2], [1, 2, 1, 1, 2, 2, 1], [2, 1, 2, 1, 1, 2, 1], [1, 2, 1, 2, 2, 2, 1], [2, 1, 2, 2, 1, 1, 2]]
        for i in range(7):
            stupci = []
            for j in range(7):
                stupci.append(int(0))
            ploca.append(stupci)

        self.ploca = ploca
        self.broj_redaka = 7
        self.broj_stupaca = 7

    def ucitaj_plocu(self, ime_datoteke):
        file = open(ime_datoteke)
        line = file.readline().strip().split(" ")
        broj_redaka = int(line[0])
        broj_stupaca = int(line[1])
        ploca = []

        for i in range(broj_redaka):
            stupci = []
            line = file.readline().strip()
            for s in line.split(" "):
                for j in range(broj_stupaca):
                    stupci.append(int(s))
            ploca.append(stupci)

        self.ploca = ploca
        self.broj_redaka = broj_redaka
        self.broj_stupaca = broj_stupaca

    def kopiraj_plocu(self):
        nova_ploca = Board()
        nova_ploca.ploca = copy.deepcopy(self.ploca)
        return nova_ploca

    def legalan_potez(self, stupac):
        if self.ploca[0][stupac] == 0 and stupac < self.broj_stupaca:
            return True
        else:
            return False

    # igrac: 1 za racunalo, 2 za korisnika
    def napravi_potez(self, stupac, igrac):
        for i in range(self.broj_redaka - 1, -1, -1):
            if self.ploca[i][stupac] == 0:
                self.ploca[i][stupac] = igrac
                break

    def nadi_4_u_redu(self, red, stupac):
        pocetak = stupac - 3 if stupac - 3 >= 0 else 0
        kraj = stupac if stupac <= self.broj_stupaca - 4 else self.broj_stupaca - 4

        for i in range(pocetak, kraj + 1):
            if all(x == self.ploca[red][stupac] for x in self.ploca[red][i:i + 4]):
                return True
        return False

    def nadi_4_u_stupcu(self, red, stupac):
        pocetak = red - 3 if red - 3 >= 0 else 0
        kraj = red if red <= self.broj_redaka - 4 else self.broj_redaka - 4

        for i in range(pocetak, kraj + 1):
            if all(x[stupac] == self.ploca[red][stupac] for x in self.ploca[i:i+4]):
                return True
        return False

    def nadi_4_u_dijagonali(self, red, stupac):

        # dijagonala /
        for i in range(3, -1, -1):
            pocetni_red = red + i
            pocetni_stupac = stupac - i

            if pocetni_red < self.broj_redaka and pocetni_stupac >= 0:
                break

        for i in range(3, -1, -1):
            zadnji_red = red - i
            zadnji_stupac = stupac + i

            if zadnji_red >= 0 and zadnji_stupac < self.broj_stupaca:
                break

        potezi_na_dijagonali = []
        for i in range(0, pocetni_red - zadnji_red + 1):
            potezi_na_dijagonali.append(self.ploca[pocetni_red - i][pocetni_stupac + i])

        for i in range(0, len(potezi_na_dijagonali) - 4 + 1):
            if all(x == self.ploca[red][stupac] for x in potezi_na_dijagonali[i:i+4]):
                return True

        # dijagonala \

        for i in range(3, -1, -1):
            pocetni_red = red - i
            pocetni_stupac = stupac - i

            if pocetni_red >= 0 and pocetni_stupac >= 0:
                break

        for i in range(3, -1, -1):
            zadnji_red = red + i
            zadnji_stupac = stupac + i

            if zadnji_red < self.broj_redaka and zadnji_stupac < self.broj_stupaca:
                break

        potezi_na_dijagonali = []
        for i in range(0, zadnji_red - pocetni_red + 1):
            potezi_na_dijagonali.append(self.ploca[pocetni_red + i][pocetni_stupac + i])

        for i in range(0, len(potezi_na_dijagonali) - 4 + 1):
            if all(x == self.ploca[red][stupac] for x in potezi_na_dijagonali[i:i + 4]):
                return True

        return False

    def pronadi_red_zadnjeg_poteza(self, stupac):
        for i in range(0, self.broj_redaka):
            if self.ploca[i][stupac] != 0:
                return i


    def provjeri_pobjedu(self, stupac):

        red = self.pronadi_red_zadnjeg_poteza(stupac)

        if self.nadi_4_u_redu(red, stupac):
            return True

        elif self.nadi_4_u_stupcu(red, stupac):
            return True

        elif self.nadi_4_u_dijagonali(red, stupac):
            return True

        else:
            return False

    def ponisti_potez(self, stupac):
        red = self.pronadi_red_zadnjeg_poteza(stupac)
        self.ploca[red][stupac] = 0

    def ispisi_plocu(self):
        for i in range(self.broj_redaka):
            for j in range(self.broj_stupaca):
                print(ispis[self.ploca[i][j]], end="")
            print("")

    def ploca_puna(self):
        for i in range(self.broj_stupaca):
            if self.ploca[0][i] == 0:
                return False
        return True