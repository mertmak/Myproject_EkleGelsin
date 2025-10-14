package eklegelsin.model;

import java.util.ArrayList;
import java.util.List;

public class Sepet {
    private static Sepet instance;
    private final List<Yemek> urunler;

    private Sepet() {
        urunler = new ArrayList<>();
    }

    // Singleton Design Pattern
    public static Sepet getInstance() {
        if (instance == null) {
            instance = new Sepet();
        }
        return instance;
    }

    public void urunEkle(Yemek yemek) {
        urunler.add(yemek);
    }

    public void urunKaldir(Yemek yemek) {
        urunler.remove(yemek);
    }

    public List<Yemek> getUrunler() {
        return new ArrayList<>(urunler); // Dışarıdan değiştirilmesini önlemek için kopyasını gönder
    }

    public double getToplamTutar() {
        double toplam = 0.0;
        for (Yemek yemek : urunler) {
            toplam += yemek.getFiyat();
        }
        return toplam;
    }

    public void sepetiTemizle() {
        urunler.clear();
    }

    public boolean isBos() {
        return urunler.isEmpty();
    }
}