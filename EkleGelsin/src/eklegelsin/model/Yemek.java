package eklegelsin.model;

import java.util.Objects;

public class Yemek {
    private String ad;
    private double fiyat;
    private String ozellestirmeler;
    private boolean isDrink;

    // Yiyecekler için varsayılan constructor (isDrink = false)
    public Yemek(String ad, double fiyat) {
        this(ad, fiyat, false); // Diğer constructor'ı çağırır
    }

    // YENİ CONSTRUCTOR: Ürünün tipini belirtmek için
    public Yemek(String ad, double fiyat, boolean isDrink) {
        this.ad = ad;
        this.fiyat = fiyat;
        this.ozellestirmeler = "";
        this.isDrink = isDrink;
    }

    // Kopyalama için constructor (özelleştirme sırasında yeni bir nesne yaratmak için)
    public Yemek(Yemek diger) {
        this.ad = diger.ad;
        this.fiyat = diger.fiyat;
        this.ozellestirmeler = diger.ozellestirmeler;
        this.isDrink = diger.isDrink; // Kopyalamaya eklendi
    }

    public String getAd() {
        return ad;
    }

    public double getFiyat() {
        return fiyat;
    }

    public void setFiyat(double fiyat) {
        this.fiyat = fiyat;
    }

    public String getOzellestirmeler() {
        return ozellestirmeler;
    }

    public void setOzellestirmeler(String ozellestirmeler) {
        this.ozellestirmeler = ozellestirmeler;
    }
    
    // YENİ GETTER
    public boolean isDrink() {
        return isDrink;
    }

    public String getTamAd() {
        return ad + " " + ozellestirmeler;
    }

    @Override
    public String toString() {
        return getTamAd() + " - " + String.format("%.2f₺", fiyat);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Yemek yemek = (Yemek) o;
        return Double.compare(yemek.fiyat, fiyat) == 0 &&
               isDrink == yemek.isDrink && // equals kontrolüne eklendi
               Objects.equals(ad, yemek.ad) &&
               Objects.equals(ozellestirmeler, yemek.ozellestirmeler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ad, fiyat, ozellestirmeler, isDrink); // hashCode'a eklendi
    }
}