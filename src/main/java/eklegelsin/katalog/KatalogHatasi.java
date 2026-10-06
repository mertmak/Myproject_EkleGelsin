package eklegelsin.katalog;

import java.util.List;

/** Katalog dosyaları okunamadığında veya geçersiz olduğunda bulunan tüm sorunlarla birlikte fırlatılır. */
public class KatalogHatasi extends RuntimeException {

    private final List<String> sorunlar;

    public KatalogHatasi(List<String> sorunlar) {
        super("Katalog geçersiz (" + sorunlar.size() + " sorun):\n- " + String.join("\n- ", sorunlar));
        this.sorunlar = List.copyOf(sorunlar);
    }

    public List<String> sorunlar() {
        return sorunlar;
    }
}
