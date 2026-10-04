package eklegelsin.model;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record Restoran(
        String id,
        String ad,
        String kategoriId,
        String tanitim,
        String logo,
        String kapakGorseli,
        double puan,
        SureAraligi teslimatSuresi,
        Para minimumSepetTutari,
        Para teslimatUcreti,
        CalismaSaatleri calismaSaatleri,
        List<MenuBolumu> menu) {

    public Restoran {
        id = Dogrula.bosOlamaz(id, "Restoran id");
        ad = Dogrula.bosOlamaz(ad, "Restoran adı");
        kategoriId = Dogrula.bosOlamaz(kategoriId, "Kategori id");
        tanitim = Dogrula.bosIseBosMetin(tanitim);
        logo = Dogrula.bosIseBosMetin(logo);
        kapakGorseli = Dogrula.bosIseBosMetin(kapakGorseli);
        if (puan < 0 || puan > 5) {
            throw new IllegalArgumentException("Puan 0–5 arasında olmalı: " + puan);
        }
        Objects.requireNonNull(teslimatSuresi, "teslimatSuresi");
        Objects.requireNonNull(minimumSepetTutari, "minimumSepetTutari");
        Objects.requireNonNull(teslimatUcreti, "teslimatUcreti");
        Objects.requireNonNull(calismaSaatleri, "calismaSaatleri");
        menu = List.copyOf(menu);
        var urunIdleri = new HashSet<String>();
        for (MenuBolumu bolum : menu) {
            for (Urun urun : bolum.urunler()) {
                if (!urunIdleri.add(urun.id())) {
                    throw new IllegalArgumentException("Tekrar eden ürün id: " + urun.id() + " (" + id + ")");
                }
            }
        }
    }

    public List<Urun> urunler() {
        return menu.stream().flatMap(b -> b.urunler().stream()).toList();
    }

    public Optional<Urun> urun(String urunId) {
        return urunler().stream().filter(u -> u.id().equals(urunId)).findFirst();
    }
}
