package eklegelsin.katalog;

import eklegelsin.model.Kategori;
import eklegelsin.model.Restoran;
import eklegelsin.model.Urun;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Uygulamayla paketlenen gerçek katalog geçerli ve yol haritasındaki hedefleri karşılıyor. */
class GercekKatalogTest {

    /** Repo herkese açık; gerçek zincirlerin adları kullanılmamalı. */
    private static final List<String> GERCEK_MARKALAR = List.of(
            "burger king", "mcdonald", "pizza hut", "domino", "little caesars", "papa john", "popeyes", "kfc",
            "starbucks", "kahve dünyası", "simit sarayı", "komagene", "baydöner", "usta dönerci", "dürümzade",
            "big chefs", "espressolab", "gloria jean", "caribou", "mado", "hafız mustafa", "güllüoğlu", "köşebaşı",
            "tavuk dünyası", "sbarro", "shake shack", "arby", "carl's jr", "midpoint", "kahve diyarı");

    private static Katalog katalog;

    @BeforeAll
    static void yukle() {
        katalog = KatalogYukleyici.uygulamaKatalogu();
    }

    @Test
    void enAzBesKategoriVar() {
        assertTrue(katalog.kategoriler().size() >= 5, katalog.kategoriler().toString());
    }

    @Test
    void herKategorideUcIleDortRestoranVar() {
        for (Kategori kategori : katalog.kategoriler()) {
            int sayi = katalog.kategoridekiRestoranlar(kategori.id()).size();
            assertTrue(sayi >= 3 && sayi <= 4, kategori.ad() + ": " + sayi + " restoran");
        }
        assertEquals(20, katalog.restoranlar().size());
    }

    @Test
    void herRestorandaSekizIleOnBesUrunVar() {
        for (Restoran restoran : katalog.restoranlar()) {
            int sayi = restoran.urunler().size();
            assertTrue(sayi >= 8 && sayi <= 15, restoran.ad() + ": " + sayi + " ürün");
        }
    }

    @Test
    void herRestoranTanitimLogoVeKapakIcerir() {
        for (Restoran restoran : katalog.restoranlar()) {
            assertTrue(!restoran.tanitim().isEmpty(), restoran.ad() + " tanıtımı yok");
            assertTrue(KatalogYukleyici.gorsel(restoran.logo()).isPresent(), restoran.ad() + " logosu yok");
            assertTrue(KatalogYukleyici.gorsel(restoran.kapakGorseli()).isPresent(), restoran.ad() + " kapağı yok");
        }
    }

    @Test
    void herKategorininGorseliVar() {
        for (Kategori kategori : katalog.kategoriler()) {
            assertTrue(KatalogYukleyici.gorsel(kategori.gorsel()).isPresent(), kategori.ad());
        }
    }

    @Test
    void herUrununAciklamasiVar() {
        for (Restoran restoran : katalog.restoranlar()) {
            for (Urun urun : restoran.urunler()) {
                assertTrue(!urun.aciklama().isEmpty(), restoran.ad() + " › " + urun.ad());
            }
        }
    }

    @Test
    void restoranAdlariBenzersizVeGercekZincirlerdenFarkli() {
        var adlar = new HashSet<String>();
        for (Restoran restoran : katalog.restoranlar()) {
            assertTrue(adlar.add(restoran.ad()), "Tekrar eden ad: " + restoran.ad());
            String kucuk = restoran.ad().toLowerCase(Locale.forLanguageTag("tr"));
            for (String marka : GERCEK_MARKALAR) {
                assertTrue(!kucuk.contains(marka), restoran.ad() + " gerçek bir marka adı içeriyor: " + marka);
            }
        }
    }

    @Test
    void bilinmeyenGorselYoluBosDoner() {
        assertTrue(KatalogYukleyici.gorsel("").isEmpty());
        assertTrue(KatalogYukleyici.gorsel("urunler/yok.jpg").isEmpty());
    }
}
