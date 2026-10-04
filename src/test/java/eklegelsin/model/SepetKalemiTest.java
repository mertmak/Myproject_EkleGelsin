package eklegelsin.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static eklegelsin.TestVerisi.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SepetKalemiTest {

    @Test
    void secimsizUrununFiyati() {
        var kalem = new SepetKalemi(AYRAN, 3);
        assertEquals(Para.tl(40), kalem.birimFiyat());
        assertEquals(Para.tl(120), kalem.toplam());
    }

    @Test
    void seceneklerFiyataEklenir() {
        var kalem = etDoner(2, BUCUK_PORSIYON, KASAR);
        assertEquals(Para.tl(225), kalem.birimFiyat());
        assertEquals(Para.tl(450), kalem.toplam());
    }

    @Test
    void zorunluGrupSecilmeli() {
        var hata = assertThrows(IllegalArgumentException.class, () -> etDoner(1, KASAR));
        assertTrue(hata.getMessage().contains("Porsiyon"));
    }

    @Test
    void grubunEnCokSiniriAsilamaz() {
        assertThrows(IllegalArgumentException.class, () -> etDoner(1, BIR_PORSIYON, KASAR, ACI_SOS, PATATES));
        assertThrows(IllegalArgumentException.class, () -> etDoner(1, BIR_PORSIYON, BUCUK_PORSIYON));
    }

    @Test
    void baskaUruneAitSecenekReddedilir() {
        var yabanci = new Secenek("ananas", "Ananas", Para.tl(10));
        assertThrows(IllegalArgumentException.class, () -> etDoner(1, BIR_PORSIYON, yabanci));
        assertThrows(IllegalArgumentException.class, () -> new SepetKalemi(AYRAN, List.of(KASAR), 1));
    }

    @Test
    void ayniSecenekIkiKezSecilemez() {
        assertThrows(IllegalArgumentException.class, () -> etDoner(1, BIR_PORSIYON, KASAR, KASAR));
    }

    @Test
    void adetEnAzBirOlmali() {
        assertThrows(IllegalArgumentException.class, () -> new SepetKalemi(AYRAN, 0));
    }

    @Test
    void secimSirasiOnemsizdir() {
        var a = etDoner(1, KASAR, SOGANSIZ, BIR_PORSIYON);
        var b = etDoner(1, BIR_PORSIYON, SOGANSIZ, KASAR);
        assertEquals(a, b);
        assertTrue(a.ayniSecimMi(b));
        assertEquals(List.of(BIR_PORSIYON, KASAR, SOGANSIZ), a.secimler());
    }

    @Test
    void farkliSecimlerAyriKalemdir() {
        assertFalse(etDoner(1, BIR_PORSIYON).ayniSecimMi(etDoner(1, BIR_PORSIYON, KASAR)));
    }

    @Test
    void H6_aciklamadaFazladanBoslukYok() {
        assertEquals("Ayran", new SepetKalemi(AYRAN, 1).aciklama());
        assertEquals("Et Döner (1 porsiyon, Ekstra kaşar, Soğansız)", etDoner(1, BIR_PORSIYON, KASAR, SOGANSIZ).aciklama());
        assertEquals("", new SepetKalemi(AYRAN, 1).secimOzeti());
    }
}
