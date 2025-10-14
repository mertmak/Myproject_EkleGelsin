package eklegelsin.model;

import java.util.ArrayList;
import java.util.List;

public class Dukkan {
    private String ad;
    private List<Yemek> menu;

    public Dukkan(String ad) {
        this.ad = ad;
        this.menu = new ArrayList<>();
    }

    public String getAd() {
        return ad;
    }

    public void menuEkle(Yemek yemek) {
        menu.add(yemek);
    }

    public List<Yemek> getMenu() {
        return menu;
    }

    @Override
    public String toString() {
        return ad;
    }
}