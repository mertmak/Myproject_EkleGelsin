package eklegelsin.model;

import java.util.Objects;

/** Bir seçenek grubundaki tek seçenek, ör. "Ekstra kaşar (+₺15)" veya "Soğansız". */
public record Secenek(String id, String ad, Para ekFiyat) {

    public Secenek {
        id = Dogrula.bosOlamaz(id, "Seçenek id");
        ad = Dogrula.bosOlamaz(ad, "Seçenek adı");
        Objects.requireNonNull(ekFiyat, "ekFiyat");
    }
}
