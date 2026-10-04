package eklegelsin;

import eklegelsin.model.CalismaSaatleri;
import eklegelsin.model.MenuBolumu;
import eklegelsin.model.Para;
import eklegelsin.model.Restoran;
import eklegelsin.model.Secenek;
import eklegelsin.model.SecenekGrubu;
import eklegelsin.model.SepetKalemi;
import eklegelsin.model.SureAraligi;
import eklegelsin.model.Urun;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

/** Testlerde ortak kullanılan örnek katalog. */
public final class TestVerisi {

    public static final ZoneId ISTANBUL = ZoneId.of("Europe/Istanbul");

    public static final Secenek BIR_PORSIYON = new Secenek("porsiyon-1", "1 porsiyon", Para.SIFIR);
    public static final Secenek BUCUK_PORSIYON = new Secenek("porsiyon-1.5", "1,5 porsiyon", Para.tl(60));
    public static final Secenek KASAR = new Secenek("kasar", "Ekstra kaşar", Para.tl(15));
    public static final Secenek ACI_SOS = new Secenek("aci-sos", "Acı sos", Para.tl(5));
    public static final Secenek PATATES = new Secenek("patates", "Patates", Para.tl(25));
    public static final Secenek SOGANSIZ = new Secenek("sogansiz", "Soğansız", Para.SIFIR);

    public static final Urun ET_DONER = new Urun("et-doner", "Et Döner", "Yaprak döner, lavaş, domates, soğan", "", Para.tl(150),
            List.of(
                    new SecenekGrubu("porsiyon", "Porsiyon", 1, 1, List.of(BIR_PORSIYON, BUCUK_PORSIYON)),
                    new SecenekGrubu("ekstralar", "Ekstralar", 0, 2, List.of(KASAR, ACI_SOS, PATATES)),
                    new SecenekGrubu("cikarilacaklar", "Çıkarılacaklar", 0, 1, List.of(SOGANSIZ))));

    public static final Urun AYRAN = new Urun("ayran", "Ayran", "30 cl", "", Para.tl(40));

    public static final Restoran USTA_DONERCI = new Restoran("usta-donerci", "Usta Dönerci", "doner", "Odun ateşinde yaprak döner",
            "", "", 4.6, new SureAraligi(25, 35), Para.tl(200), Para.tl(20),
            new CalismaSaatleri(LocalTime.of(10, 0), LocalTime.of(2, 0)),
            List.of(new MenuBolumu("Dönerler", List.of(ET_DONER)), new MenuBolumu("İçecekler", List.of(AYRAN))));

    public static final Urun MARGHERITA = new Urun("margherita", "Margherita", "Domates sosu, mozzarella, fesleğen", "", Para.tl(220));

    public static final Restoran NAPOLI_FIRIN = new Restoran("napoli-firin", "Napoli Fırın", "pizza", "", "", "", 4.4,
            new SureAraligi(30, 45), Para.tl(150), Para.SIFIR, CalismaSaatleri.HER_ZAMAN,
            List.of(new MenuBolumu("Pizzalar", List.of(MARGHERITA))));

    private TestVerisi() {
    }

    public static SepetKalemi etDoner(int adet, Secenek... secimler) {
        return new SepetKalemi(ET_DONER, Arrays.asList(secimler), adet);
    }

    public static Clock saat(int yil, int ay, int gun, int saat, int dakika) {
        return Clock.fixed(LocalDateTime.of(yil, ay, gun, saat, dakika).atZone(ISTANBUL).toInstant(), ISTANBUL);
    }
}
