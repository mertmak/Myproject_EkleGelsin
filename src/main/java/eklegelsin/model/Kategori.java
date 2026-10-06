package eklegelsin.model;

/** Ana sayfadaki kategori, ör. "Döner". {@code gorsel} boş olabilir. */
public record Kategori(String id, String ad, String gorsel) {

    public Kategori {
        id = Dogrula.bosOlamaz(id, "Kategori id");
        ad = Dogrula.bosOlamaz(ad, "Kategori adı");
        gorsel = Dogrula.bosIseBosMetin(gorsel);
    }
}
