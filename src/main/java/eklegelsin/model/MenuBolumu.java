package eklegelsin.model;

import java.util.List;

/** Menüdeki başlık altında gruplanmış ürünler, ör. "Dönerler", "İçecekler". */
public record MenuBolumu(String ad, List<Urun> urunler) {

    public MenuBolumu {
        ad = Dogrula.bosOlamaz(ad, "Menü bölümü adı");
        urunler = List.copyOf(urunler);
    }
}
