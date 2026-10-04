package eklegelsin.servis;

import eklegelsin.servis.KartDogrulayici.Alan;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static eklegelsin.TestVerisi.saat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KartDogrulayiciTest {

    /** Bugün: 2 Ekim 2026 */
    private final KartDogrulayici dogrulayici = new KartDogrulayici(saat(2026, 10, 2, 12, 0));

    @Test
    void gecerliKartSonDortHaneyiDondurur() {
        var sonuc = dogrulayici.dogrula("4111111111111111", "12/30", "123");
        assertTrue(sonuc.gecerliMi(), sonuc.hatalar().toString());
        assertEquals("1111", sonuc.sonDortHane());
    }

    @Test
    void H1_buAySonunaKadarGecerliKartKabulEdilir() {
        assertTrue(dogrulayici.dogrula("4111111111111111", "10/26", "123").gecerliMi());
    }

    @Test
    void H1_gecenAyDolanKartReddedilir() {
        var sonuc = dogrulayici.dogrula("4111111111111111", "09/26", "123");
        assertEquals("Kartın süresi dolmuş.", sonuc.hata(Alan.SON_KULLANMA).orElseThrow());
    }

    @ParameterizedTest
    @ValueSource(strings = {"4111 1111 1111 1111", "4111-1111-1111-1111", " 4111111111111111 "})
    void H4_boslukVeTireTemizlenir(String numara) {
        assertTrue(dogrulayici.dogrula(numara, "12/30", "123").gecerliMi());
    }

    @Test
    void H4_luhnKontrolu() {
        var sonuc = dogrulayici.dogrula("1234567812345678", "12/30", "123");
        assertTrue(sonuc.hata(Alan.NUMARA).isPresent());
        assertTrue(KartDogrulayici.luhnGecerliMi("4111111111111111"));
        assertFalse(KartDogrulayici.luhnGecerliMi("4111111111111112"));
    }

    @Test
    void H4_amexDortHaneliCvvIster() {
        assertTrue(dogrulayici.dogrula("378282246310005", "12/30", "1234").gecerliMi());
        assertEquals("American Express kartlarda CVV 4 haneli olmalı.",
                dogrulayici.dogrula("378282246310005", "12/30", "123").hata(Alan.CVV).orElseThrow());
        assertEquals("CVV 3 haneli olmalı.",
                dogrulayici.dogrula("4111111111111111", "12/30", "1234").hata(Alan.CVV).orElseThrow());
    }

    @ParameterizedTest
    @ValueSource(strings = {"4111a11111111111", "411111111111", "41111111111111111111"})
    void H4_hataliNumaralar(String numara) {
        assertTrue(dogrulayici.dogrula(numara, "12/30", "123").hata(Alan.NUMARA).isPresent());
    }

    @Test
    void sonKullanmaBicimleri() {
        assertTrue(dogrulayici.dogrula("4111111111111111", "1/30", "123").gecerliMi());
        assertTrue(dogrulayici.dogrula("4111111111111111", " 12 / 30 ", "123").gecerliMi());
        assertEquals("Ay 01 ile 12 arasında olmalı.",
                dogrulayici.dogrula("4111111111111111", "13/30", "123").hata(Alan.SON_KULLANMA).orElseThrow());
        assertTrue(dogrulayici.dogrula("4111111111111111", "1230", "123").hata(Alan.SON_KULLANMA).isPresent());
        assertEquals("Son kullanma tarihi geçersiz.",
                dogrulayici.dogrula("4111111111111111", "12/60", "123").hata(Alan.SON_KULLANMA).orElseThrow());
    }

    @Test
    void H8_tumHatalarAlanBazindaDonerVeArayuzGerekmez() {
        var sonuc = dogrulayici.dogrula(null, null, null);
        assertFalse(sonuc.gecerliMi());
        assertEquals(3, sonuc.hatalar().size());
        assertEquals(null, sonuc.sonDortHane());
        assertEquals("CVV 3 veya 4 haneli olmalı.", sonuc.hata(Alan.CVV).orElseThrow());
    }
}
