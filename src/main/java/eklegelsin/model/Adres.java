package eklegelsin.model;

/** Teslimat adresi. {@code baslik} kullanıcının verdiği kısa ad, ör. "Ev", "İş". */
public record Adres(String baslik, String il, String ilce, String mahalle, String acikAdres, String tarif) {

    public Adres {
        baslik = Dogrula.bosOlamaz(baslik, "Adres başlığı");
        il = Dogrula.bosOlamaz(il, "İl");
        ilce = Dogrula.bosOlamaz(ilce, "İlçe");
        mahalle = Dogrula.bosOlamaz(mahalle, "Mahalle");
        acikAdres = Dogrula.bosOlamaz(acikAdres, "Açık adres");
        tarif = Dogrula.bosIseBosMetin(tarif);
    }

    /** {@code Bağdat Cad. No:12 D:5, Caddebostan Mah., Kadıköy/İstanbul} */
    public String tekSatir() {
        return acikAdres + ", " + mahalle + ", " + ilce + "/" + il;
    }
}
