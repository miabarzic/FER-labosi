# ako nije postavljena zastavica NASTAVI_IGRU, program izračunava samo prvi potez računala i prekida s radom
# mjerenja su provedena za dubinu 7
import sys
from statistics import mean
from mpi4py import MPI
from Board import *
import time
from queue import Queue

DUBINA = 6
NASTAVI_IGRU = True

comm = MPI.COMM_WORLD
size = comm.Get_size()
rank = comm.Get_rank()


def posalji_kraj():
    for i in range(1, size):
        comm.send(["end"], dest=i)


def posalji_novi_potez():
    for i in range(1, size):
        comm.send(['novi_potez'], dest=i)


def posalji_racunaj():
    for i in range(1, size):
        comm.send(["racunaj"], dest=i)


def stvori_zadatke(p):
    cvorovi_vrijednosti = {}
    red_poteza = Queue()

    for i in range(p.broj_stupaca):
        cvorovi_vrijednosti[i] = []

    for i in range(p.broj_stupaca):
        if p.legalan_potez(i):
            p.napravi_potez(i, 1)
            if p.provjeri_pobjedu(i):
                cvorovi_vrijednosti[i].append(1)
            else:
                for j in range(p.broj_stupaca):
                    if p.legalan_potez(j):
                        red_poteza.put((i, j))
            p.ponisti_potez(i)
    return cvorovi_vrijednosti, red_poteza


def evaluate(p, zadnji_igrac, zadnji_potez, dubina):
    alllose = True
    allwin = True

    ploca = p.kopiraj_plocu()

    if ploca.provjeri_pobjedu(zadnji_potez):

        if zadnji_igrac == 1:
            return 1
        else:
            return -1

    if dubina == 0:
        return 0

    dubina -= 1

    novi_igrac = 1 if zadnji_igrac == 2 else 2

    ukupno = 0
    broj_poteza = 0

    for i in range(ploca.broj_stupaca):
        if ploca.legalan_potez(i):
            broj_poteza += 1
            ploca.napravi_potez(i, novi_igrac)
            rezultat = evaluate(ploca, novi_igrac, i, dubina)
            ploca.ponisti_potez(i)
            if rezultat > -1:
                alllose = False
            if rezultat != 1:
                allwin = False
            if rezultat == 1 and novi_igrac == 1:
                return 1
            if rezultat == -1 and novi_igrac == 2:
                return -1
            ukupno += rezultat

    if allwin:
        return 1
    if alllose:
        return -1
    ukupno /= broj_poteza
    return ukupno


if rank == 0:
    ploca = Board()
    status = MPI.Status()
    stanje = 0
    potezi = Queue()
    vrijednosti_cvorova = {}
    broj_poslanih_poruka_kraj = 0

    while True:
        if stanje == 0:
            broj_poslanih_poruka_kraj = 0
            legalan = False

            while not legalan:
                igracev_potez = int(input())
                if ploca.legalan_potez(igracev_potez):
                    ploca.napravi_potez(igracev_potez, 2)
                    legalan = True
                """else:
                    print("Vas potez nije dopusten, odaberite drugi stupac")
                    sys.stdout.flush()"""

            start_time = time.time()

            if ploca.provjeri_pobjedu(igracev_potez):
                #print("Igrac je pobijedio")
                posalji_kraj()
                break

            vrijednosti_cvorova, potezi = stvori_zadatke(ploca)

            if ploca.ploca_puna():
                #print("Ploca popunjena, igra zavrsava nerijeseno")
                posalji_kraj()
                break
            else:
                posalji_racunaj()
                stanje = 1

        elif stanje == 1:
            if potezi.qsize() == 0:
                stanje = 2

            else:
                poruka = comm.recv(source=MPI.ANY_SOURCE, status=status)

                if poruka[0] == 'z':
                    broj_procesa = status.Get_source()
                    p = potezi.get()

                    zadatak = ['o', ploca.kopiraj_plocu(), p]
                    comm.send(zadatak, dest=broj_procesa)

                elif poruka[0] == 'o':
                    indeks_zadatka = poruka[1]
                    vrijednost = poruka[2]
                    vrijednosti_cvorova[indeks_zadatka[0]].append(vrijednost)

        # u ovom stanju jos primam odgovore i zahtjeve za zadatke, ali saljem obavijest da nema zadataka
        elif stanje == 2:
            poruka = comm.recv(source=MPI.ANY_SOURCE, status=status)
            if poruka[0] == 'z':
                broj_procesa = status.Get_source()
                comm.send(['end'], dest=broj_procesa)
                broj_poslanih_poruka_kraj += 1
                if broj_poslanih_poruka_kraj == size - 1:
                    stanje = 3

            elif poruka[0] == 'o':
                indeks_zadatka = poruka[1]
                vrijednost = poruka[2]
                vrijednosti_cvorova[indeks_zadatka[0]].append(vrijednost)

        # u ovom stanju gleda najbolji potez i radi ga
        elif stanje == 3:
            lista_vrijednosti = []

            for i in range(ploca.broj_stupaca):
                if not vrijednosti_cvorova[i]:
                    if ploca.legalan_potez(i):
                        lista_vrijednosti.append(-2)
                    else:
                        lista_vrijednosti.append(-1000)
                else:
                    if -1 in vrijednosti_cvorova[i]:
                        lista_vrijednosti.append(-1)
                    else:
                        lista_vrijednosti.append(mean(vrijednosti_cvorova[i]))

            novi_potez = lista_vrijednosti.index(max(lista_vrijednosti))

            ploca.napravi_potez(novi_potez, 1)
            for v in lista_vrijednosti:
                if v >= -1:
                    print("%.3f" % v, end=" ")
            print("")
            ploca.ispisi_plocu()

            #print(" %s s" % (time.time() - start_time))
            if ploca.ploca_puna():
                #print("Ploca popunjena, igra zavrsava nerijeseno")
                posalji_kraj()
                break

            if ploca.provjeri_pobjedu(novi_potez):
                #print("Racunalo je pobijedilo")
                posalji_kraj()
                break

            if NASTAVI_IGRU:
                posalji_novi_potez()
                stanje = 0
            else:
                break

else:
    stanje = 0
    poruka = []

    # salje zahtjev, odgovor i prelazi u stanje 1 u kojem računa i vraća vrijednost
    while True:

        if stanje == 0:
            poruka = comm.recv(source = 0)

            if poruka[0] == 'end':
                break
            elif poruka[0] == 'racunaj':
                stanje = 1

        elif stanje == 1:
            comm.send(['z'], dest=0)
            poruka = comm.recv(source=0)
            if poruka[0] == 'o':
                stanje = 2

            elif poruka[0] == 'end':
                stanje = 3

        # rjesava zadatak i vraca rezultat
        elif stanje == 2:
            ploca = poruka[1]
            potezi = poruka[2]

            ploca.napravi_potez(potezi[0], 1)
            ploca.napravi_potez(potezi[1], 2)

            vrijednost = evaluate(ploca, 2, potezi[1], DUBINA - 2)

            comm.send(['o', potezi, vrijednost], dest=0)
            stanje = 1

        elif stanje == 3:
            sys.stdout.flush()
            if NASTAVI_IGRU:
                poruka = comm.recv(source=0)
                if poruka[0] == 'novi_potez':
                    stanje = 0
                elif poruka[0] == 'end':
                    break
            else:
                break
