package eklegelsin.katalog;

import eklegelsin.model.CalismaSaatleri;
import eklegelsin.model.Kategori;
import eklegelsin.model.MenuBolumu;
import eklegelsin.model.Para;
import eklegelsin.model.Restoran;
import eklegelsin.model.Secenek;
import eklegelsin.model.SecenekGrubu;
import eklegelsin.model.SureAraligi;
import eklegelsin.model.Urun;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Kataloğu JSON dosyalarından okur ve doğrular. Yeni restoran eklemek için
 * {@code katalog/restoranlar/<id>.json} dosyası yazıp id'yi {@code katalog/katalog.json} listesine
 * eklemek ve görsellerini {@code gorseller/} altına koymak yeterlidir.
 *
 * <p>Doğrulama ilk hatada durmaz: bulunan tüm sorunlar tek bir {@link KatalogHatasi} içinde raporlanır.
 */
public final class KatalogYukleyici {

    static final String DIZIN = "katalog/katalog.json";
    static final String RESTORAN_KLASORU = "katalog/restoranlar/";
    /** Katalogdaki görsel yolları bu klasöre göredir, ör. {@code urunler/et-doner.jpg}. */
    public static final String GORSEL_KLASORU = "gorseller/";
    /** Swing'in (ImageIO) okuyabildiği biçimler. */
    private static final Set<String> GORSEL_UZANTILARI = Set.of(".jpg", ".jpeg", ".png");
    /** Id'ler dosya adı ve veritabanı anahtarı olarak kullanılır: küçük ASCII harf, rakam ve tire. */
    private static final Pattern ID_BICIMI = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");

    private final Function<String, URL> kaynakBulucu;
    private final JsonMapper json = JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    /** @param kaynakBulucu kaynak yolunu ({@code katalog/katalog.json} gibi) URL'ye çevirir; yoksa {@code null} döner */
    KatalogYukleyici(Function<String, URL> kaynakBulucu) {
        this.kaynakBulucu = kaynakBulucu;
    }

    /** Uygulamayla paketlenen kataloğu yükler. */
    public static Katalog uygulamaKatalogu() {
        return new KatalogYukleyici(KatalogYukleyici::uygulamaKaynagi).yukle();
    }

    /** Katalogdaki bir görsel yolunu ({@code urunler/et-doner.jpg}) paketteki kaynağa çevirir. */
    public static Optional<URL> gorsel(String yol) {
        return yol == null || yol.isBlank() ? Optional.empty() : Optional.ofNullable(uygulamaKaynagi(GORSEL_KLASORU + yol));
    }

    private static URL uygulamaKaynagi(String yol) {
        return KatalogYukleyici.class.getResource("/" + yol);
    }

    Katalog yukle() {
        var sorunlar = new ArrayList<String>();
        Optional<KatalogJson.Dizin> dizin = oku(DIZIN, KatalogJson.Dizin.class, sorunlar);
        if (dizin.isEmpty()) {
            throw new KatalogHatasi(sorunlar);
        }

        var kategoriler = new ArrayList<Kategori>();
        var kategoriIdleri = new HashSet<String>();
        for (KatalogJson.Kategori k : bosIseBos(dizin.get().kategoriler())) {
            try {
                var kategori = new Kategori(idDenetle(k.id()), k.ad(), k.gorsel());
                if (!kategoriIdleri.add(kategori.id())) {
                    sorunlar.add(DIZIN + ": tekrar eden kategori id: " + kategori.id());
                    continue;
                }
                gorseliDenetle(DIZIN + " › kategori " + kategori.id(), kategori.gorsel(), sorunlar);
                kategoriler.add(kategori);
            } catch (IllegalArgumentException e) {
                sorunlar.add(DIZIN + ": " + e.getMessage());
            }
        }
        if (kategoriler.isEmpty()) {
            sorunlar.add(DIZIN + ": en az bir kategori olmalı");
        }

        var restoranlar = new ArrayList<Restoran>();
        var restoranIdleri = new HashSet<String>();
        for (String id : bosIseBos(dizin.get().restoranlar())) {
            if (id == null || !ID_BICIMI.matcher(id).matches()) {
                sorunlar.add(DIZIN + ": geçersiz restoran id (küçük harf, rakam ve tire olmalı): " + id);
                continue;
            }
            if (!restoranIdleri.add(id)) {
                sorunlar.add(DIZIN + ": tekrar eden restoran id: " + id);
                continue;
            }
            restoran(id, sorunlar).ifPresent(restoran -> {
                if (!kategoriIdleri.contains(restoran.kategoriId())) {
                    sorunlar.add(restoranYolu(id) + ": bilinmeyen kategori: " + restoran.kategoriId());
                }
                restoranlar.add(restoran);
            });
        }

        if (!sorunlar.isEmpty()) {
            throw new KatalogHatasi(sorunlar);
        }
        return new Katalog(kategoriler, restoranlar);
    }

    private Optional<Restoran> restoran(String id, List<String> sorunlar) {
        String yol = restoranYolu(id);
        Optional<KatalogJson.Restoran> okunan = oku(yol, KatalogJson.Restoran.class, sorunlar);
        if (okunan.isEmpty()) {
            return Optional.empty();
        }
        KatalogJson.Restoran r = okunan.get();
        int oncekiSorunSayisi = sorunlar.size();

        if (!id.equals(r.id())) {
            sorunlar.add(yol + ": id dosya adıyla aynı olmalı, bulunan: " + r.id());
        }
        gorseliDenetle(yol + " › logo", r.logo(), sorunlar);
        gorseliDenetle(yol + " › kapak", r.kapak(), sorunlar);

        Map<String, SecenekGrubu> gruplar = secenekGruplari(yol, r.secenekGruplari(), sorunlar);
        var kullanilanGruplar = new HashSet<String>();
        var menu = new ArrayList<MenuBolumu>();
        for (KatalogJson.Bolum bolum : bosIseBos(r.menu())) {
            var urunler = new ArrayList<Urun>();
            for (KatalogJson.Urun u : bosIseBos(bolum.urunler())) {
                String konum = yol + " › ürün " + u.id();
                try {
                    var urunGruplari = new ArrayList<SecenekGrubu>();
                    for (String grupId : bosIseBos(u.secenekGruplari())) {
                        SecenekGrubu grup = gruplar.get(grupId);
                        if (grup == null) {
                            throw new IllegalArgumentException("tanımsız seçenek grubu: " + grupId);
                        }
                        urunGruplari.add(grup);
                        kullanilanGruplar.add(grupId);
                    }
                    Para fiyat = para(u.fiyat(), "fiyat");
                    if (fiyat.sifirMi()) {
                        throw new IllegalArgumentException("fiyat sıfır olamaz");
                    }
                    var urun = new Urun(idDenetle(u.id()), u.ad(), u.aciklama(), u.gorsel(), fiyat, urunGruplari);
                    gorseliDenetle(konum, urun.gorsel(), sorunlar);
                    urunler.add(urun);
                } catch (IllegalArgumentException e) {
                    sorunlar.add(konum + ": " + e.getMessage());
                }
            }
            if (bosIseBos(bolum.urunler()).isEmpty()) {
                sorunlar.add(yol + ": boş menü bölümü: " + bolum.ad());
            }
            if (urunler.isEmpty()) {
                continue;
            }
            try {
                menu.add(new MenuBolumu(bolum.ad(), urunler));
            } catch (IllegalArgumentException e) {
                sorunlar.add(yol + ": " + e.getMessage());
            }
        }
        if (bosIseBos(r.menu()).isEmpty()) {
            sorunlar.add(yol + ": menü boş");
        }
        for (String grupId : gruplar.keySet()) {
            if (!kullanilanGruplar.contains(grupId)) {
                sorunlar.add(yol + ": hiçbir üründe kullanılmayan seçenek grubu: " + grupId);
            }
        }

        if (sorunlar.size() > oncekiSorunSayisi) {
            return Optional.empty();
        }
        try {
            KatalogJson.Sure sure = zorunlu(r.teslimatSuresi(), "teslimatSuresi");
            KatalogJson.Saatler saatler = zorunlu(r.calismaSaatleri(), "calismaSaatleri");
            return Optional.of(new Restoran(id, r.ad(), r.kategori(), r.tanitim(), r.logo(), r.kapak(),
                    zorunlu(r.puan(), "puan"),
                    new SureAraligi(zorunlu(sure.enAz(), "teslimatSuresi.enAz"), zorunlu(sure.enCok(), "teslimatSuresi.enCok")),
                    para(r.minimumSepet(), "minimumSepet"),
                    para(r.teslimatUcreti(), "teslimatUcreti"),
                    new CalismaSaatleri(zorunlu(saatler.acilis(), "calismaSaatleri.acilis"),
                            zorunlu(saatler.kapanis(), "calismaSaatleri.kapanis")),
                    menu));
        } catch (IllegalArgumentException e) {
            sorunlar.add(yol + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    private static Map<String, SecenekGrubu> secenekGruplari(String yol, List<KatalogJson.SecenekGrubu> tanimlar,
                                                             List<String> sorunlar) {
        var gruplar = new LinkedHashMap<String, SecenekGrubu>();
        for (KatalogJson.SecenekGrubu g : bosIseBos(tanimlar)) {
            try {
                var secenekler = bosIseBos(g.secenekler()).stream()
                        .map(s -> new Secenek(idDenetle(s.id()), s.ad(), s.ekFiyat() == null ? Para.SIFIR : new Para(s.ekFiyat())))
                        .toList();
                var grup = new SecenekGrubu(idDenetle(g.id()), g.ad(),
                        zorunlu(g.enAz(), "enAz"), zorunlu(g.enCok(), "enCok"), secenekler);
                if (gruplar.putIfAbsent(grup.id(), grup) != null) {
                    sorunlar.add(yol + ": tekrar eden seçenek grubu id: " + grup.id());
                }
            } catch (IllegalArgumentException e) {
                sorunlar.add(yol + " › seçenek grubu " + g.id() + ": " + e.getMessage());
            }
        }
        return gruplar;
    }

    private void gorseliDenetle(String konum, String yol, List<String> sorunlar) {
        if (yol == null || yol.isBlank()) {
            return;
        }
        String kucuk = yol.toLowerCase(Locale.ROOT);
        if (GORSEL_UZANTILARI.stream().noneMatch(kucuk::endsWith)) {
            sorunlar.add(konum + ": desteklenmeyen görsel biçimi (jpg veya png olmalı): " + yol);
        } else if (kaynakBulucu.apply(GORSEL_KLASORU + yol) == null) {
            sorunlar.add(konum + ": görsel bulunamadı: " + GORSEL_KLASORU + yol);
        }
    }

    private <T> Optional<T> oku(String yol, Class<T> tur, List<String> sorunlar) {
        URL url = kaynakBulucu.apply(yol);
        if (url == null) {
            sorunlar.add(yol + ": dosya bulunamadı");
            return Optional.empty();
        }
        try (InputStream girdi = url.openStream()) {
            return Optional.of(json.readValue(girdi, tur));
        } catch (JacksonException e) {
            var konum = e.getLocation() == null ? "" : " (satır " + e.getLocation().getLineNr() + ")";
            sorunlar.add(yol + ": geçersiz JSON" + konum + ": " + e.getOriginalMessage());
        } catch (IOException e) {
            sorunlar.add(yol + ": okunamadı: " + e.getMessage());
        }
        return Optional.empty();
    }

    private static String restoranYolu(String id) {
        return RESTORAN_KLASORU + id + ".json";
    }

    private static String idDenetle(String id) {
        if (id != null && !id.isBlank() && !ID_BICIMI.matcher(id).matches()) {
            throw new IllegalArgumentException("geçersiz id (küçük harf, rakam ve tire olmalı): " + id);
        }
        return id;
    }

    private static Para para(BigDecimal tutar, String alan) {
        return new Para(zorunlu(tutar, alan));
    }

    private static <T> T zorunlu(T deger, String alan) {
        if (deger == null) {
            throw new IllegalArgumentException(alan + " eksik");
        }
        return deger;
    }

    private static <T> List<T> bosIseBos(List<T> liste) {
        return liste == null ? List.of() : liste;
    }
}
