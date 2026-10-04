package eklegelsin.servis;

import eklegelsin.model.Adres;
import eklegelsin.model.OdemeBilgisi;
import eklegelsin.model.Para;
import eklegelsin.model.SepetKalemi;
import eklegelsin.model.Siparis;
import eklegelsin.model.SiparisDurumu;
import eklegelsin.model.Telefon;
import eklegelsin.model.TeslimatBilgisi;
import eklegelsin.model.TeslimatPlani;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.Random;

import static eklegelsin.TestVerisi.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SiparisServisiTest {

    private static final TeslimatBilgisi TESLIMAT = new TeslimatBilgisi(
            new Adres("Ev", "İstanbul", "Kadıköy", "Caferağa Mah.", "Moda Cad. No:12 D:5", "Parkın karşısı"),
            Telefon.ayristir("0532 123 45 67"), "Zili çalmayın");

    private final Sepet sepet = new Sepet();

    private static SiparisServisi servis(Clock saat) {
        return new SiparisServisi(saat, new Random(42), TeslimatPlani.SIMULASYON);
    }

    @Test
    void siparisVerilirVeSepetBosalir() {
        Clock saat = saat(2026, 10, 2, 12, 0);
        sepet.ekle(USTA_DONERCI, etDoner(1, BUCUK_PORSIYON, KASAR));
        sepet.ekle(USTA_DONERCI, new SepetKalemi(AYRAN, 2));

        Siparis siparis = servis(saat).siparisVer(sepet, TESLIMAT, OdemeBilgisi.onlineKart("1111"));

        assertTrue(siparis.numara().matches("EG-\\d{6}"), siparis.numara());
        assertEquals("Usta Dönerci", siparis.restoranAdi());
        assertEquals(2, siparis.kalemler().size());
        assertEquals(Para.tl(305), siparis.araToplam());
        assertEquals(Para.tl(20), siparis.teslimatUcreti());
        assertEquals(Para.tl(325), siparis.genelToplam());
        assertEquals(saat.instant(), siparis.verilmeZamani());
        assertEquals(SiparisDurumu.ALINDI, siparis.durum(saat.instant()));
        assertEquals("Online kart •••• 1111", siparis.odeme().gorunenAd());
        assertTrue(sepet.bosMu());
    }

    @Test
    void bosSepetleSiparisVerilemez() {
        var hata = assertThrows(SiparisVerilemezException.class,
                () -> servis(saat(2026, 10, 2, 12, 0)).siparisVer(sepet, TESLIMAT, OdemeBilgisi.kapidaNakit()));
        assertEquals("Sepetiniz boş.", hata.getMessage());
    }

    @Test
    void minimumTutarAltindaSiparisVerilemez() {
        sepet.ekle(USTA_DONERCI, etDoner(1, BIR_PORSIYON));

        var hata = assertThrows(SiparisVerilemezException.class,
                () -> servis(saat(2026, 10, 2, 12, 0)).siparisVer(sepet, TESLIMAT, OdemeBilgisi.kapidaNakit()));
        assertEquals("Usta Dönerci için minimum sepet tutarı ₺200,00. ₺50,00 daha ekleyin.", hata.getMessage());
        assertFalse(sepet.bosMu());
    }

    @Test
    void kapaliRestorandanSiparisVerilemez() {
        sepet.ekle(USTA_DONERCI, etDoner(2, BIR_PORSIYON)); // 10:00–02:00 arası açık

        var hata = assertThrows(SiparisVerilemezException.class,
                () -> servis(saat(2026, 10, 2, 4, 0)).siparisVer(sepet, TESLIMAT, OdemeBilgisi.kapidaKart()));
        assertEquals("Usta Dönerci şu an kapalı.", hata.getMessage());
        assertFalse(sepet.bosMu());
    }

    @Test
    void geceYarisindanSonraAcikRestorandanSiparisVerilir() {
        sepet.ekle(USTA_DONERCI, etDoner(2, BIR_PORSIYON));
        servis(saat(2026, 10, 3, 1, 30)).siparisVer(sepet, TESLIMAT, OdemeBilgisi.kapidaNakit());
        assertTrue(sepet.bosMu());
    }
}
