package eklegelsin.model;

public record Kategori(String id, String ad, String gorsel) {

    public Kategori {
        Dogrula.bosOlamaz(id, "Kategori id");
        Dogrula.bosOlamaz(ad, "Kategori adı");
    }
}
