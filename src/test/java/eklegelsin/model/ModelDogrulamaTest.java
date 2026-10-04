package eklegelsin.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static eklegelsin.TestVerisi.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Model kurucularındaki tutarlılık kontrolleri. */
class ModelDogrulamaTest {

    @Test
    void restoranIcindeUrunIdTekil() {
        assertThrows(IllegalArgumentException.class, () -> new Restoran("r", "R", "doner", "", "", "", 4,
                new SureAraligi(10, 20), Para.SIFIR, Para.SIFIR, CalismaSaatleri.HER_ZAMAN,
                List.of(new MenuBolumu("A", List.of(AYRAN)), new MenuBolumu("B", List.of(AYRAN)))));
    }

    @Test
    void restoranPuaniSinirli() {
        assertThrows(IllegalArgumentException.class, () -> new Restoran("r", "R", "doner", "", "", "", 5.1,
                new SureAraligi(10, 20), Para.SIFIR, Para.SIFIR, CalismaSaatleri.HER_ZAMAN, List.of()));
    }

    @Test
    void restoranUrunBulur() {
        assertEquals(AYRAN, USTA_DONERCI.urun("ayran").orElseThrow());
        assertTrue(USTA_DONERCI.urun("yok").isEmpty());
        assertEquals(2, USTA_DONERCI.urunler().size());
    }

    @Test
    void secenekGrubuSinirlariTutarli() {
        assertThrows(IllegalArgumentException.class, () -> new SecenekGrubu("g", "G", 2, 1, List.of(KASAR, ACI_SOS)));
        assertThrows(IllegalArgumentException.class, () -> new SecenekGrubu("g", "G", 0, 3, List.of(KASAR, ACI_SOS)));
        assertThrows(IllegalArgumentException.class, () -> new SecenekGrubu("g", "G", 0, 1, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new SecenekGrubu("g", "G", 0, 1, List.of(KASAR, KASAR)));
        var grup = new SecenekGrubu("g", "G", 1, 1, List.of(KASAR, ACI_SOS));
        assertTrue(grup.zorunluMu());
        assertTrue(grup.tekSecimMi());
    }

    @Test
    void urundeSecenekIdTekil() {
        var g1 = new SecenekGrubu("g1", "G1", 0, 1, List.of(KASAR));
        var g2 = new SecenekGrubu("g2", "G2", 0, 1, List.of(KASAR));
        assertThrows(IllegalArgumentException.class, () -> new Urun("u", "U", "", "", Para.tl(1), List.of(g1, g2)));
        assertThrows(IllegalArgumentException.class, () -> new Urun("u", "U", "", "", Para.tl(1), List.of(g1, g1)));
    }

    @Test
    void zorunluAlanlarBosOlamaz() {
        assertThrows(IllegalArgumentException.class, () -> new Urun(" ", "U", "", "", Para.tl(1)));
        assertThrows(IllegalArgumentException.class, () -> new Kategori("doner", "", ""));
        assertThrows(IllegalArgumentException.class, () -> new Adres("Ev", "İstanbul", "Kadıköy", "Moda", "  ", ""));
        assertThrows(IllegalArgumentException.class, () -> new SureAraligi(30, 20));
    }

    @Test
    void adresTekSatir() {
        var adres = new Adres("Ev", "İstanbul", "Kadıköy", "Caferağa Mah.", "Moda Cad. No:12 D:5", null);
        assertEquals("Moda Cad. No:12 D:5, Caferağa Mah., Kadıköy/İstanbul", adres.tekSatir());
        assertEquals("", adres.tarif());
    }

    @Test
    void telefonFarkliYazimlariKabulEder() {
        for (String girdi : List.of("0532 123 45 67", "05321234567", "+90 532 123 45 67", "905321234567", "532-123-4567", "(0532) 123 4567")) {
            assertEquals("05321234567", Telefon.ayristir(girdi).numara(), girdi);
        }
        assertEquals("0532 123 45 67", Telefon.ayristir("5321234567").bicimli());
    }

    @Test
    void telefonGecersizleriReddeder() {
        for (String girdi : new String[]{null, "", "0212 123 45 67", "0532 123 45", "0532 123 45 678", "abc"}) {
            assertFalse(Telefon.gecerliMi(girdi), String.valueOf(girdi));
        }
    }

    @Test
    void odemeBilgisiKartSakla() {
        assertEquals("Online kart •••• 1111", OdemeBilgisi.onlineKart("1111").gorunenAd());
        assertEquals("Kapıda nakit", OdemeBilgisi.kapidaNakit().gorunenAd());
        assertEquals("Kapıda kart", OdemeBilgisi.kapidaKart().gorunenAd());
        assertThrows(IllegalArgumentException.class, () -> OdemeBilgisi.onlineKart(null));
        assertThrows(IllegalArgumentException.class, () -> OdemeBilgisi.onlineKart("4111111111111111"));
        assertThrows(IllegalArgumentException.class, () -> new OdemeBilgisi(OdemeYontemi.KAPIDA_NAKIT, "1111"));
    }

    @Test
    void teslimatPlaniAsamalari() {
        var plan = TeslimatPlani.SIMULASYON;
        assertEquals(SiparisDurumu.ALINDI, plan.durum(Duration.ofSeconds(-5)));
        assertEquals(SiparisDurumu.ALINDI, plan.durum(Duration.ZERO));
        assertEquals(SiparisDurumu.HAZIRLANIYOR, plan.durum(Duration.ofSeconds(20)));
        assertEquals(SiparisDurumu.HAZIRLANIYOR, plan.durum(Duration.ofSeconds(59)));
        assertEquals(SiparisDurumu.YOLDA, plan.durum(Duration.ofSeconds(60)));
        assertEquals(SiparisDurumu.TESLIM_EDILDI, plan.durum(Duration.ofSeconds(120)));
        assertEquals(SiparisDurumu.TESLIM_EDILDI, plan.durum(Duration.ofDays(3)));
        assertEquals("Yolda", SiparisDurumu.YOLDA.gorunenAd());
    }

    @Test
    void teslimatPlaniArtanSiradaOlmali() {
        assertThrows(IllegalArgumentException.class,
                () -> new TeslimatPlani(Duration.ofSeconds(10), Duration.ofSeconds(10), Duration.ofSeconds(20)));
    }

    @Test
    void siparisDurumuGecenSuredenHesaplanir() {
        Instant verilme = Instant.parse("2026-10-02T09:00:00Z");
        Siparis siparis = ornekSiparis(verilme, Para.tl(40), Para.tl(60));
        // Uygulama kapatılıp 90 sn sonra açılsa da durum doğrudan hesaplanır.
        assertEquals(SiparisDurumu.YOLDA, siparis.durum(verilme.plusSeconds(90)));
        assertEquals(verilme.plusSeconds(120), siparis.tahminiTeslimZamani());
        assertEquals(1, siparis.urunAdedi());
    }

    @Test
    void siparisToplamlariTutarliOlmali() {
        Instant t = Instant.parse("2026-10-02T09:00:00Z");
        assertDoesNotThrow(() -> ornekSiparis(t, Para.tl(40), Para.tl(60)));
        assertThrows(IllegalArgumentException.class, () -> ornekSiparis(t, Para.tl(41), Para.tl(61)));
        assertThrows(IllegalArgumentException.class, () -> ornekSiparis(t, Para.tl(40), Para.tl(70)));
    }

    private static Siparis ornekSiparis(Instant verilme, Para araToplam, Para genelToplam) {
        var teslimat = new TeslimatBilgisi(new Adres("Ev", "İstanbul", "Kadıköy", "Moda", "Moda Cad. 1", ""),
                Telefon.ayristir("05321234567"), null);
        return new Siparis("EG-000001", "usta-donerci", "Usta Dönerci", List.of(new SepetKalemi(AYRAN, 1)),
                teslimat, OdemeBilgisi.kapidaNakit(), araToplam, Para.tl(20), genelToplam, verilme, TeslimatPlani.SIMULASYON);
    }
}
