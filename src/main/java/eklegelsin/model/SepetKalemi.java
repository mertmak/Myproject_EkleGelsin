package eklegelsin.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sepetteki bir satır: ürün, seçilen seçenekler ve adet.
 * Seçimler ürünün seçenek gruplarına göre doğrulanır ve menüdeki sıraya göre dizilir; böylece
 * aynı seçimler farklı sırayla verilse de iki kalem eşit sayılır.
 */
public record SepetKalemi(Urun urun, List<Secenek> secimler, int adet) {

    public SepetKalemi {
        Objects.requireNonNull(urun, "urun");
        if (adet < 1) {
            throw new IllegalArgumentException("Adet en az 1 olmalı: " + adet);
        }
        secimler = dogrulaVeSirala(urun, secimler);
    }

    public SepetKalemi(Urun urun, int adet) {
        this(urun, List.of(), adet);
    }

    private static List<Secenek> dogrulaVeSirala(Urun urun, List<Secenek> secimler) {
        Set<String> secilenIdler = new HashSet<>();
        for (Secenek s : secimler) {
            if (!secilenIdler.add(s.id())) {
                throw new IllegalArgumentException("Aynı seçenek iki kez seçilmiş: " + s.ad());
            }
        }
        var sirali = new ArrayList<Secenek>();
        for (SecenekGrubu grup : urun.secenekGruplari()) {
            int secilen = 0;
            for (Secenek s : grup.secenekler()) {
                if (secilenIdler.contains(s.id())) {
                    sirali.add(s);
                    secilen++;
                }
            }
            if (secilen < grup.enAz()) {
                throw new IllegalArgumentException("\"" + grup.ad() + "\" için en az " + grup.enAz() + " seçim yapmalısınız.");
            }
            if (secilen > grup.enCok()) {
                throw new IllegalArgumentException("\"" + grup.ad() + "\" için en fazla " + grup.enCok() + " seçim yapabilirsiniz.");
            }
        }
        if (sirali.size() != secilenIdler.size()) {
            throw new IllegalArgumentException("Seçeneklerden bazıları " + urun.ad() + " ürününe ait değil.");
        }
        return List.copyOf(sirali);
    }

    public Para birimFiyat() {
        Para fiyat = urun.fiyat();
        for (Secenek s : secimler) {
            fiyat = fiyat.arti(s.ekFiyat());
        }
        return fiyat;
    }

    public Para toplam() {
        return birimFiyat().carpi(adet);
    }

    /** Seçimlerin okunabilir listesi, ör. "1,5 porsiyon, Ekstra kaşar". Seçim yoksa boş metin. */
    public String secimOzeti() {
        return secimler.stream().map(Secenek::ad).collect(Collectors.joining(", "));
    }

    /** Ürün adı ve varsa parantez içinde seçimler, ör. "Et Döner (Ekstra kaşar)". */
    public String aciklama() {
        return secimler.isEmpty() ? urun.ad() : urun.ad() + " (" + secimOzeti() + ")";
    }

    /** Aynı ürün ve aynı seçimlerse iki kalem sepette birleştirilir. */
    public boolean ayniSecimMi(SepetKalemi diger) {
        return urun.id().equals(diger.urun.id())
                && secimler.stream().map(Secenek::id).toList()
                        .equals(diger.secimler.stream().map(Secenek::id).toList());
    }

    public SepetKalemi adetle(int yeniAdet) {
        return new SepetKalemi(urun, secimler, yeniAdet);
    }
}
