package eklegelsin.katalog;

import eklegelsin.model.CalismaSaatleri;
import eklegelsin.model.Para;
import eklegelsin.model.Restoran;
import eklegelsin.model.SureAraligi;
import eklegelsin.model.Urun;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Geçici bir klasöre yazılan küçük kataloglarla yükleme ve doğrulama kuralları. */
class KatalogYukleyiciTest {

    @TempDir
    Path kok;

    private static final String DIZIN = """
            {
              "kategoriler": [
                { "id": "doner", "ad": "Döner", "gorsel": "kategoriler/doner.jpg" },
                { "id": "pizza", "ad": "Pizza" }
              ],
              "restoranlar": ["usta"]
            }
            """;

    private static final String USTA = """
            {
              "id": "usta",
              "ad": "Usta Ocakbaşı",
              "kategori": "doner",
              "tanitim": "Odun ateşinde yaprak döner",
              "logo": "logolar/usta.png",
              "kapak": "",
              "puan": 4.6,
              "teslimatSuresi": { "enAz": 25, "enCok": 35 },
              "minimumSepet": 250,
              "teslimatUcreti": 19.90,
              "calismaSaatleri": { "acilis": "10:00", "kapanis": "02:00" },
              "secenekGruplari": [
                { "id": "porsiyon", "ad": "Porsiyon", "enAz": 1, "enCok": 1, "secenekler": [
                  { "id": "tek", "ad": "1 porsiyon" },
                  { "id": "bucuk", "ad": "1,5 porsiyon", "ekFiyat": 120 }
                ] }
              ],
              "menu": [
                { "ad": "Dönerler", "urunler": [
                  { "id": "et-doner", "ad": "Et Döner", "aciklama": "Yaprak döner, lavaş",
                    "gorsel": "urunler/et-doner.jpg", "fiyat": 390, "secenekGruplari": ["porsiyon"] },
                  { "id": "tavuk-doner", "ad": "Tavuk Döner", "fiyat": 240, "secenekGruplari": ["porsiyon"] }
                ] },
                { "ad": "İçecekler", "urunler": [
                  { "id": "ayran", "ad": "Ayran", "fiyat": 45 }
                ] }
              ]
            }
            """;

    @Test
    void gecerliKatalogModelNesnelerineDonusur() throws IOException {
        Katalog katalog = yukle(DIZIN, USTA);

        assertEquals(List.of("doner", "pizza"), katalog.kategoriler().stream().map(k -> k.id()).toList());
        assertEquals("", katalog.kategori("pizza").orElseThrow().gorsel());

        Restoran usta = katalog.restoran("usta").orElseThrow();
        assertEquals(new SureAraligi(25, 35), usta.teslimatSuresi());
        assertEquals(Para.tl(250), usta.minimumSepetTutari());
        assertEquals(Para.tl("19.90"), usta.teslimatUcreti());
        assertEquals(new CalismaSaatleri(LocalTime.of(10, 0), LocalTime.of(2, 0)), usta.calismaSaatleri());
        assertEquals(List.of("Dönerler", "İçecekler"), usta.menu().stream().map(b -> b.ad()).toList());
        assertEquals(List.of(usta), katalog.kategoridekiRestoranlar("doner"));
        assertEquals(List.of(), katalog.kategoridekiRestoranlar("pizza"));
        assertTrue(katalog.restoran("yok").isEmpty());
    }

    @Test
    void secenekGrubuRestoranDuzeyindeTanimlanipUrunlerdePaylasilir() throws IOException {
        Restoran usta = yukle(DIZIN, USTA).restoran("usta").orElseThrow();

        Urun etDoner = usta.urun("et-doner").orElseThrow();
        Urun tavukDoner = usta.urun("tavuk-doner").orElseThrow();
        assertEquals(etDoner.secenekGruplari(), tavukDoner.secenekGruplari());
        var bucuk = etDoner.secenekGruplari().getFirst().secenekler().get(1);
        assertEquals(Para.tl(120), bucuk.ekFiyat());
        assertEquals(Para.SIFIR, etDoner.secenekGruplari().getFirst().secenekler().getFirst().ekFiyat(),
                "ekFiyat verilmezse ücretsiz");
        assertTrue(usta.urun("ayran").orElseThrow().secenekGruplari().isEmpty());
    }

    @Test
    void negatifFiyatReddedilir() throws IOException {
        assertSorun("ürün ayran: Tutar negatif olamaz", DIZIN, USTA.replace("\"fiyat\": 45", "\"fiyat\": -45"));
    }

    @Test
    void sifirFiyatReddedilir() throws IOException {
        assertSorun("ürün ayran: fiyat sıfır olamaz", DIZIN, USTA.replace("\"fiyat\": 45", "\"fiyat\": 0"));
    }

    @Test
    void fiyatiEksikUrunReddedilir() throws IOException {
        assertSorun("ürün ayran: fiyat eksik", DIZIN, USTA.replace(", \"fiyat\": 45", ""));
    }

    @Test
    void eksikGorselRaporlanir() throws IOException {
        hazirla(DIZIN, USTA);
        Files.delete(kok.resolve("gorseller/urunler/et-doner.jpg"));
        assertSorunYukluKatalogda("ürün et-doner: görsel bulunamadı: gorseller/urunler/et-doner.jpg");
    }

    @Test
    void eksikKategoriGorseliRaporlanir() throws IOException {
        hazirla(DIZIN, USTA);
        Files.delete(kok.resolve("gorseller/kategoriler/doner.jpg"));
        assertSorunYukluKatalogda("kategori doner: görsel bulunamadı");
    }

    @Test
    void swinginOkuyamadigiGorselBicimiReddedilir() throws IOException {
        assertSorun("logo: desteklenmeyen görsel biçimi", DIZIN, USTA.replace("logolar/usta.png", "logolar/usta.webp"));
    }

    @Test
    void tekrarEdenUrunIdReddedilir() throws IOException {
        assertSorun("Tekrar eden ürün id: et-doner", DIZIN, USTA.replace("\"id\": \"ayran\"", "\"id\": \"et-doner\""));
    }

    @Test
    void turkceKarakterliIdReddedilir() throws IOException {
        assertSorun("geçersiz id (küçük harf, rakam ve tire olmalı): çay", DIZIN, USTA.replace("\"id\": \"ayran\"", "\"id\": \"çay\""));
    }

    @Test
    void gecersizRestoranIdReddedilir() throws IOException {
        assertSorun("geçersiz restoran id", DIZIN.replace("[\"usta\"]", "[\"usta\", \"../usta\"]"), USTA);
    }

    @Test
    void tekrarEdenRestoranIdReddedilir() throws IOException {
        assertSorun("tekrar eden restoran id: usta", DIZIN.replace("[\"usta\"]", "[\"usta\", \"usta\"]"), USTA);
    }

    @Test
    void tekrarEdenKategoriIdReddedilir() throws IOException {
        assertSorun("tekrar eden kategori id: doner", DIZIN.replace("\"id\": \"pizza\"", "\"id\": \"doner\""), USTA);
    }

    @Test
    void bilinmeyenKategoriReddedilir() throws IOException {
        assertSorun("bilinmeyen kategori: burger", DIZIN, USTA.replace("\"kategori\": \"doner\"", "\"kategori\": \"burger\""));
    }

    @Test
    void dosyaAdiIleIdUyusmazsaReddedilir() throws IOException {
        assertSorun("id dosya adıyla aynı olmalı", DIZIN, USTA.replace("\"id\": \"usta\"", "\"id\": \"usta-2\""));
    }

    @Test
    void listedekiRestoranDosyasiYoksaRaporlanir() throws IOException {
        assertSorun("katalog/restoranlar/yok.json: dosya bulunamadı", DIZIN.replace("[\"usta\"]", "[\"usta\", \"yok\"]"), USTA);
    }

    @Test
    void tanimsizSecenekGrubuReddedilir() throws IOException {
        assertSorun("ürün et-doner: tanımsız seçenek grubu: boy", DIZIN,
                USTA.replace("\"fiyat\": 390, \"secenekGruplari\": [\"porsiyon\"]", "\"fiyat\": 390, \"secenekGruplari\": [\"boy\"]"));
    }

    @Test
    void kullanilmayanSecenekGrubuYazimHatasiOlarakRaporlanir() throws IOException {
        assertSorun("kullanılmayan seçenek grubu: porsiyon", DIZIN, USTA.replace(", \"secenekGruplari\": [\"porsiyon\"]", ""));
    }

    @Test
    void gecersizSecimSiniriSecenekGrubuBaglamiylaRaporlanir() throws IOException {
        assertSorun("seçenek grubu porsiyon: Geçersiz seçim sınırları", DIZIN, USTA.replace("\"enCok\": 1, \"secenekler\"", "\"enCok\": 3, \"secenekler\""));
    }

    @Test
    void puanAraligiDisindaysaReddedilir() throws IOException {
        assertSorun("Puan 0–5 arasında olmalı", DIZIN, USTA.replace("4.6", "6.2"));
    }

    @Test
    void zorunluRestoranAlaniEksikseReddedilir() throws IOException {
        assertSorun("calismaSaatleri eksik", DIZIN, USTA.replace("\"calismaSaatleri\": { \"acilis\": \"10:00\", \"kapanis\": \"02:00\" },", ""));
    }

    @Test
    void bosMenuReddedilir() throws IOException {
        String bosMenu = USTA.substring(0, USTA.indexOf("\"menu\"")) + "\"menu\": [] }";
        assertSorun("menü boş", DIZIN, bosMenu);
    }

    @Test
    void bilinmeyenAlanYazimHatasiOlarakReddedilir() throws IOException {
        assertSorun("geçersiz JSON", DIZIN, USTA.replace("\"tanitim\"", "\"tanıtım\""));
    }

    @Test
    void bozukJsonSatirNumarasiylaRaporlanir() throws IOException {
        assertSorun("katalog/restoranlar/usta.json: geçersiz JSON (satır", DIZIN, USTA.replace("\"puan\": 4.6,", "\"puan\": 4.6"));
    }

    @Test
    void bosMenuBolumuReddedilir() throws IOException {
        assertSorun("boş menü bölümü: İçecekler", DIZIN, USTA.replace("{ \"id\": \"ayran\", \"ad\": \"Ayran\", \"fiyat\": 45 }", ""));
    }

    @Test
    void dizinDosyasiYoksaRaporlanir() {
        var hata = assertThrows(KatalogHatasi.class, () -> new KatalogYukleyici(this::bul).yukle());
        assertEquals(List.of("katalog/katalog.json: dosya bulunamadı"), hata.sorunlar());
    }

    @Test
    void tumSorunlarTekSeferdeRaporlanir() throws IOException {
        String ikiHatali = USTA.replace("\"fiyat\": 45", "\"fiyat\": -45")
                .replace("logolar/usta.png", "logolar/yok.png");
        var hata = assertThrows(KatalogHatasi.class, () -> yukle(DIZIN, ikiHatali));
        assertEquals(2, hata.sorunlar().size(), hata.getMessage());
        assertTrue(hata.getMessage().contains("2 sorun"));
    }

    private Katalog yukle(String dizin, String usta) throws IOException {
        hazirla(dizin, usta);
        return new KatalogYukleyici(this::bul).yukle();
    }

    private void hazirla(String dizin, String usta) throws IOException {
        yaz("katalog/katalog.json", dizin);
        yaz("katalog/restoranlar/usta.json", usta);
        yaz("gorseller/kategoriler/doner.jpg", "");
        yaz("gorseller/logolar/usta.png", "");
        yaz("gorseller/urunler/et-doner.jpg", "");
    }

    private void assertSorun(String beklenen, String dizin, String usta) {
        var hata = assertThrows(KatalogHatasi.class, () -> yukle(dizin, usta));
        assertTrue(hata.sorunlar().stream().anyMatch(s -> s.contains(beklenen)), hata.getMessage());
    }

    /** {@link #hazirla} ile yazılmış, sonra değiştirilmiş kataloğu yükler. */
    private void assertSorunYukluKatalogda(String beklenen) {
        var hata = assertThrows(KatalogHatasi.class, () -> new KatalogYukleyici(this::bul).yukle());
        assertTrue(hata.sorunlar().stream().anyMatch(s -> s.contains(beklenen)), hata.getMessage());
    }

    private void yaz(String yol, String icerik) throws IOException {
        Path dosya = kok.resolve(yol);
        Files.createDirectories(dosya.getParent());
        Files.writeString(dosya, icerik);
    }

    private URL bul(String yol) {
        Path dosya = kok.resolve(yol);
        try {
            return Files.exists(dosya) ? dosya.toUri().toURL() : null;
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }
}
