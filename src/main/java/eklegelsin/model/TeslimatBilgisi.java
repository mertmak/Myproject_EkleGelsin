package eklegelsin.model;

import java.util.Objects;

public record TeslimatBilgisi(Adres adres, Telefon telefon, String not) {

    public TeslimatBilgisi {
        Objects.requireNonNull(adres, "adres");
        Objects.requireNonNull(telefon, "telefon");
        not = Dogrula.bosIseBosMetin(not);
    }
}
