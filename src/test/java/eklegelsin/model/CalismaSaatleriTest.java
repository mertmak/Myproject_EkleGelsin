package eklegelsin.model;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalismaSaatleriTest {

    @Test
    void gunIciAralik() {
        var saatler = new CalismaSaatleri(LocalTime.of(10, 0), LocalTime.of(22, 0));
        assertFalse(saatler.acikMi(LocalTime.of(9, 59)));
        assertTrue(saatler.acikMi(LocalTime.of(10, 0)));
        assertTrue(saatler.acikMi(LocalTime.of(21, 59)));
        assertFalse(saatler.acikMi(LocalTime.of(22, 0)));
    }

    @Test
    void geceYarisiniAsanAralik() {
        var saatler = new CalismaSaatleri(LocalTime.of(11, 0), LocalTime.of(2, 0));
        assertTrue(saatler.acikMi(LocalTime.of(23, 30)));
        assertTrue(saatler.acikMi(LocalTime.of(1, 59)));
        assertFalse(saatler.acikMi(LocalTime.of(2, 0)));
        assertFalse(saatler.acikMi(LocalTime.of(10, 59)));
    }

    @Test
    void yirmiDortSaatAcik() {
        assertTrue(CalismaSaatleri.HER_ZAMAN.acikMi(LocalTime.of(4, 0)));
    }
}
