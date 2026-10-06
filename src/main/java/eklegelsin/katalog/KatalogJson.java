package eklegelsin.katalog;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * Katalog JSON dosyalarının birebir karşılığı. Doğrulama burada değil, {@link KatalogYukleyici}
 * model nesnelerini kurarken yapılır; bu yüzden tüm alanlar boş gelebilir.
 */
final class KatalogJson {

    private KatalogJson() {
    }

    /** {@code katalog/katalog.json}: kategoriler ve yüklenecek restoranların id'leri. */
    record Dizin(List<Kategori> kategoriler, List<String> restoranlar) {
    }

    record Kategori(String id, String ad, String gorsel) {
    }

    /** {@code katalog/restoranlar/<id>.json} */
    record Restoran(
            String id,
            String ad,
            String kategori,
            String tanitim,
            String logo,
            String kapak,
            Double puan,
            Sure teslimatSuresi,
            BigDecimal minimumSepet,
            BigDecimal teslimatUcreti,
            Saatler calismaSaatleri,
            List<SecenekGrubu> secenekGruplari,
            List<Bolum> menu) {
    }

    record Sure(Integer enAz, Integer enCok) {
    }

    record Saatler(LocalTime acilis, LocalTime kapanis) {
    }

    /** Restoran düzeyinde bir kez tanımlanır, ürünler id ile başvurur. */
    record SecenekGrubu(String id, String ad, Integer enAz, Integer enCok, List<Secenek> secenekler) {
    }

    /** {@code ekFiyat} verilmezse seçenek ücretsizdir. */
    record Secenek(String id, String ad, BigDecimal ekFiyat) {
    }

    record Bolum(String ad, List<Urun> urunler) {
    }

    record Urun(String id, String ad, String aciklama, String gorsel, BigDecimal fiyat, List<String> secenekGruplari) {
    }
}
