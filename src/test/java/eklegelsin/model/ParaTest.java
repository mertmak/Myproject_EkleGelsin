package eklegelsin.model;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParaTest {

    @Test
    void turkceBicimlenir() {
        assertEquals("₺150,00", Para.tl(150).bicimli());
        assertEquals("₺1.250,50", Para.tl("1250.5").bicimli());
        assertEquals("₺0,00", Para.SIFIR.bicimli());
    }

    @Test
    void H7_bicimSistemDilindenBagimsizdir() {
        Locale onceki = Locale.getDefault();
        try {
            Locale.setDefault(Locale.US);
            assertEquals("₺1.250,50", Para.tl("1250.5").bicimli());
        } finally {
            Locale.setDefault(onceki);
        }
    }

    @Test
    void ondalikToplamaKayipsizdir() {
        assertEquals(Para.tl("0.3"), Para.tl("0.1").arti(Para.tl("0.2")));
    }

    @Test
    void ikiBasamagaYuvarlanir() {
        assertEquals(Para.tl("10.01"), Para.tl("10.005"));
        assertEquals(Para.tl(10), Para.tl("10.0"));
    }

    @Test
    void carpmaVeCikarma() {
        assertEquals(Para.tl(450), Para.tl(150).carpi(3));
        assertEquals(Para.tl(50), Para.tl(200).eksi(Para.tl(150)));
        assertTrue(Para.tl(10).kucuktur(Para.tl(11)));
    }

    @Test
    void negatifTutarOlusamaz() {
        assertThrows(IllegalArgumentException.class, () -> Para.tl(-1));
        assertThrows(IllegalArgumentException.class, () -> Para.tl(10).eksi(Para.tl(11)));
        assertThrows(IllegalArgumentException.class, () -> Para.tl(10).carpi(-1));
    }
}
