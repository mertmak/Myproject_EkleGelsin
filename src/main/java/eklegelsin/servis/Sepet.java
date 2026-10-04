package eklegelsin.servis;

import eklegelsin.model.Para;
import eklegelsin.model.Restoran;
import eklegelsin.model.SepetKalemi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Kullanıcının sepeti. Sepet her zaman tek bir restorana aittir; başka bir restorandan ürün eklemek için
 * önce sepetin boşaltılması gerekir ({@link #bosaltVeEkle}). Aynı ürün aynı seçimlerle tekrar eklenirse
 * yeni satır açılmaz, adet artar.
 */
public final class Sepet {

    private Restoran restoran;
    private final List<SepetKalemi> kalemler = new ArrayList<>();

    public Optional<Restoran> restoran() {
        return Optional.ofNullable(restoran);
    }

    public List<SepetKalemi> kalemler() {
        return List.copyOf(kalemler);
    }

    public boolean bosMu() {
        return kalemler.isEmpty();
    }

    /** Sepet doluysa ve verilen restoran sepetin restoranı değilse {@code true}. Arayüz bu durumda onay ister. */
    public boolean baskaRestoranaAitMi(Restoran r) {
        return restoran != null && !restoran.id().equals(r.id());
    }

    /**
     * @throws IllegalStateException sepet başka bir restorana aitse
     * @throws IllegalArgumentException ürün bu restoranın menüsünde değilse
     */
    public void ekle(Restoran r, SepetKalemi kalem) {
        if (baskaRestoranaAitMi(r)) {
            throw new IllegalStateException("Sepette " + restoran.ad() + " restoranının ürünleri var");
        }
        if (r.urun(kalem.urun().id()).isEmpty()) {
            throw new IllegalArgumentException(kalem.urun().ad() + " ürünü " + r.ad() + " menüsünde yok");
        }
        restoran = r;
        for (int i = 0; i < kalemler.size(); i++) {
            SepetKalemi mevcut = kalemler.get(i);
            if (mevcut.ayniSecimMi(kalem)) {
                kalemler.set(i, mevcut.adetle(mevcut.adet() + kalem.adet()));
                return;
            }
        }
        kalemler.add(kalem);
    }

    /** Kullanıcı "sepet temizlensin mi?" sorusunu onayladıktan sonra çağrılır. */
    public void bosaltVeEkle(Restoran r, SepetKalemi kalem) {
        temizle();
        ekle(r, kalem);
    }

    /** Adet 0 ise satır kaldırılır. */
    public void adetDegistir(int sira, int yeniAdet) {
        if (yeniAdet < 0) {
            throw new IllegalArgumentException("Adet negatif olamaz: " + yeniAdet);
        }
        if (yeniAdet == 0) {
            kaldir(sira);
        } else {
            kalemler.set(sira, kalemler.get(sira).adetle(yeniAdet));
        }
    }

    public void kaldir(int sira) {
        kalemler.remove(sira);
        if (kalemler.isEmpty()) {
            restoran = null;
        }
    }

    public void temizle() {
        kalemler.clear();
        restoran = null;
    }

    public int urunAdedi() {
        return kalemler.stream().mapToInt(SepetKalemi::adet).sum();
    }

    public Para araToplam() {
        return kalemler.stream().map(SepetKalemi::toplam).reduce(Para.SIFIR, Para::arti);
    }

    public Para teslimatUcreti() {
        return restoran == null ? Para.SIFIR : restoran.teslimatUcreti();
    }

    public Para genelToplam() {
        return araToplam().arti(teslimatUcreti());
    }

    /** Minimum sepet tutarına ulaşmak için eklenmesi gereken tutar; ulaşıldıysa sıfır. */
    public Para minimumaKalan() {
        if (restoran == null || !araToplam().kucuktur(restoran.minimumSepetTutari())) {
            return Para.SIFIR;
        }
        return restoran.minimumSepetTutari().eksi(araToplam());
    }

    public boolean siparisVerilebilirMi() {
        return !bosMu() && minimumaKalan().sifirMi();
    }
}
