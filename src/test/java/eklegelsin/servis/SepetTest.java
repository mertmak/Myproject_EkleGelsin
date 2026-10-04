package eklegelsin.servis;

import eklegelsin.model.Para;
import eklegelsin.model.SepetKalemi;
import org.junit.jupiter.api.Test;

import static eklegelsin.TestVerisi.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SepetTest {

    private final Sepet sepet = new Sepet();

    @Test
    void H3_sepetTekRestoranaAittir() {
        sepet.ekle(USTA_DONERCI, new SepetKalemi(AYRAN, 1));

        assertTrue(sepet.baskaRestoranaAitMi(NAPOLI_FIRIN));
        assertFalse(sepet.baskaRestoranaAitMi(USTA_DONERCI));
        assertThrows(IllegalStateException.class, () -> sepet.ekle(NAPOLI_FIRIN, new SepetKalemi(MARGHERITA, 1)));
        assertEquals(1, sepet.kalemler().size());
        assertEquals(USTA_DONERCI, sepet.restoran().orElseThrow());
    }

    @Test
    void onaydanSonraRestoranDegisir() {
        sepet.ekle(USTA_DONERCI, new SepetKalemi(AYRAN, 1));
        sepet.bosaltVeEkle(NAPOLI_FIRIN, new SepetKalemi(MARGHERITA, 1));

        assertEquals(NAPOLI_FIRIN, sepet.restoran().orElseThrow());
        assertEquals(1, sepet.kalemler().size());
        assertEquals(MARGHERITA, sepet.kalemler().getFirst().urun());
    }

    @Test
    void menudeOlmayanUrunEklenemez() {
        assertThrows(IllegalArgumentException.class, () -> sepet.ekle(USTA_DONERCI, new SepetKalemi(MARGHERITA, 1)));
        assertTrue(sepet.bosMu());
        assertTrue(sepet.restoran().isEmpty());
    }

    @Test
    void ayniSecimlerBirlesir() {
        sepet.ekle(USTA_DONERCI, etDoner(1, BIR_PORSIYON, KASAR));
        sepet.ekle(USTA_DONERCI, etDoner(2, KASAR, BIR_PORSIYON));
        sepet.ekle(USTA_DONERCI, etDoner(1, BIR_PORSIYON));

        assertEquals(2, sepet.kalemler().size());
        assertEquals(3, sepet.kalemler().getFirst().adet());
        assertEquals(4, sepet.urunAdedi());
    }

    @Test
    void adetDegisirVeSifirdaSatirSilinir() {
        sepet.ekle(USTA_DONERCI, new SepetKalemi(AYRAN, 1));
        sepet.ekle(USTA_DONERCI, etDoner(1, BIR_PORSIYON));

        sepet.adetDegistir(0, 3);
        assertEquals(3, sepet.kalemler().getFirst().adet());

        sepet.adetDegistir(0, 0);
        assertEquals(1, sepet.kalemler().size());
        assertThrows(IllegalArgumentException.class, () -> sepet.adetDegistir(0, -1));
    }

    @Test
    void sonUrunSilininceSepetRestoranaBagliKalmaz() {
        sepet.ekle(USTA_DONERCI, new SepetKalemi(AYRAN, 1));
        sepet.kaldir(0);

        assertTrue(sepet.restoran().isEmpty());
        assertFalse(sepet.baskaRestoranaAitMi(NAPOLI_FIRIN));
        sepet.ekle(NAPOLI_FIRIN, new SepetKalemi(MARGHERITA, 1));
    }

    @Test
    void kalemListesiDisaridanDegistirilemez() {
        sepet.ekle(USTA_DONERCI, new SepetKalemi(AYRAN, 1));
        assertThrows(UnsupportedOperationException.class, () -> sepet.kalemler().clear());
    }

    @Test
    void tutarlar() {
        assertEquals(Para.SIFIR, sepet.teslimatUcreti());
        assertEquals(Para.SIFIR, sepet.genelToplam());

        sepet.ekle(USTA_DONERCI, etDoner(1, BUCUK_PORSIYON, KASAR)); // 225
        sepet.ekle(USTA_DONERCI, new SepetKalemi(AYRAN, 2));         // 80

        assertEquals(Para.tl(305), sepet.araToplam());
        assertEquals(Para.tl(20), sepet.teslimatUcreti());
        assertEquals(Para.tl(325), sepet.genelToplam());
    }

    @Test
    void minimumSepetTutari() {
        assertEquals(Para.SIFIR, sepet.minimumaKalan());
        assertFalse(sepet.siparisVerilebilirMi());

        sepet.ekle(USTA_DONERCI, etDoner(1, BIR_PORSIYON)); // 150, minimum 200
        assertEquals(Para.tl(50), sepet.minimumaKalan());
        assertFalse(sepet.siparisVerilebilirMi());

        sepet.ekle(USTA_DONERCI, new SepetKalemi(AYRAN, 1)); // 190
        assertEquals(Para.tl(10), sepet.minimumaKalan());

        sepet.ekle(USTA_DONERCI, new SepetKalemi(AYRAN, 1)); // 230
        assertEquals(Para.SIFIR, sepet.minimumaKalan());
        assertTrue(sepet.siparisVerilebilirMi());
    }

    @Test
    void temizle() {
        sepet.ekle(USTA_DONERCI, new SepetKalemi(AYRAN, 1));
        sepet.temizle();
        assertTrue(sepet.bosMu());
        assertTrue(sepet.restoran().isEmpty());
    }
}
