package eklegelsin.model;

/** Model sınıflarının kurucularında kullanılan ortak kontroller. */
final class Dogrula {

    private Dogrula() {
    }

    static String bosOlamaz(String deger, String alan) {
        if (deger == null || deger.isBlank()) {
            throw new IllegalArgumentException(alan + " boş olamaz");
        }
        return deger.strip();
    }

    static String bosIseBosMetin(String deger) {
        return deger == null ? "" : deger.strip();
    }
}
