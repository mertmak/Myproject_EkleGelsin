package eklegelsin.model;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Menüdeki bir ürün. {@code gorsel} boş olabilir; bu durumda arayüz yer tutucu gösterir.
 */
public record Urun(String id, String ad, String aciklama, String gorsel, Para fiyat, List<SecenekGrubu> secenekGruplari) {

    public Urun {
        id = Dogrula.bosOlamaz(id, "Ürün id");
        ad = Dogrula.bosOlamaz(ad, "Ürün adı");
        aciklama = Dogrula.bosIseBosMetin(aciklama);
        gorsel = Dogrula.bosIseBosMetin(gorsel);
        Objects.requireNonNull(fiyat, "fiyat");
        secenekGruplari = List.copyOf(secenekGruplari);
        var grupIdleri = new HashSet<String>();
        var secenekIdleri = new HashSet<String>();
        for (SecenekGrubu grup : secenekGruplari) {
            if (!grupIdleri.add(grup.id())) {
                throw new IllegalArgumentException("Tekrar eden seçenek grubu id: " + grup.id() + " (" + id + ")");
            }
            for (Secenek s : grup.secenekler()) {
                if (!secenekIdleri.add(s.id())) {
                    throw new IllegalArgumentException("Tekrar eden seçenek id: " + s.id() + " (" + id + ")");
                }
            }
        }
    }

    public Urun(String id, String ad, String aciklama, String gorsel, Para fiyat) {
        this(id, ad, aciklama, gorsel, fiyat, List.of());
    }

    public boolean secenekliMi() {
        return !secenekGruplari.isEmpty();
    }
}
