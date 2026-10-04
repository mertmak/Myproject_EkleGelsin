package eklegelsin.servis;

import java.time.Clock;
import java.time.YearMonth;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simüle edilen online ödeme için kart bilgisi doğrulaması. Gerçek bir ödeme yapılmaz ve kart numarası
 * hiçbir yere kaydedilmez; sonuçta yalnızca son 4 hane döner.
 */
public final class KartDogrulayici {

    public enum Alan { NUMARA, SON_KULLANMA, CVV }

    private static final Pattern SON_KULLANMA = Pattern.compile("\\s*(\\d{1,2})\\s*/\\s*(\\d{2})\\s*");
    private static final int EN_FAZLA_YIL = 20;

    private final Clock saat;

    public KartDogrulayici(Clock saat) {
        this.saat = Objects.requireNonNull(saat, "saat");
    }

    public KartDogrulamaSonucu dogrula(String numara, String sonKullanma, String cvv) {
        Map<Alan, String> hatalar = new EnumMap<>(Alan.class);

        String rakamlar = numara == null ? "" : numara.replaceAll("[\\s-]", "");
        boolean amex = false;
        if (!rakamlar.matches("\\d*")) {
            hatalar.put(Alan.NUMARA, "Kart numarası yalnızca rakam içermeli.");
        } else if (rakamlar.length() < 13 || rakamlar.length() > 19) {
            hatalar.put(Alan.NUMARA, "Kart numarası 13–19 haneli olmalı.");
        } else if (!luhnGecerliMi(rakamlar)) {
            hatalar.put(Alan.NUMARA, "Kart numarası geçersiz, lütfen kontrol edin.");
        } else {
            amex = rakamlar.length() == 15 && (rakamlar.startsWith("34") || rakamlar.startsWith("37"));
        }

        sonKullanmaHatasi(sonKullanma).ifPresent(h -> hatalar.put(Alan.SON_KULLANMA, h));

        String cvvRakamlar = cvv == null ? "" : cvv.strip();
        boolean numaraGecerli = !hatalar.containsKey(Alan.NUMARA);
        if (numaraGecerli && amex && !cvvRakamlar.matches("\\d{4}")) {
            hatalar.put(Alan.CVV, "American Express kartlarda CVV 4 haneli olmalı.");
        } else if (numaraGecerli && !amex && !cvvRakamlar.matches("\\d{3}")) {
            hatalar.put(Alan.CVV, "CVV 3 haneli olmalı.");
        } else if (!numaraGecerli && !cvvRakamlar.matches("\\d{3,4}")) {
            hatalar.put(Alan.CVV, "CVV 3 veya 4 haneli olmalı.");
        }

        String sonDortHane = hatalar.isEmpty() ? rakamlar.substring(rakamlar.length() - 4) : null;
        return new KartDogrulamaSonucu(hatalar, sonDortHane);
    }

    private Optional<String> sonKullanmaHatasi(String sonKullanma) {
        Matcher m = SON_KULLANMA.matcher(sonKullanma == null ? "" : sonKullanma);
        if (!m.matches()) {
            return Optional.of("Son kullanma tarihini AA/YY biçiminde girin, ör. 08/29.");
        }
        int ay = Integer.parseInt(m.group(1));
        if (ay < 1 || ay > 12) {
            return Optional.of("Ay 01 ile 12 arasında olmalı.");
        }
        YearMonth kart = YearMonth.of(2000 + Integer.parseInt(m.group(2)), ay);
        YearMonth buAy = YearMonth.now(saat);
        // Kart, üzerinde yazan ayın son gününe kadar geçerlidir.
        if (kart.isBefore(buAy)) {
            return Optional.of("Kartın süresi dolmuş.");
        }
        if (kart.isAfter(buAy.plusYears(EN_FAZLA_YIL))) {
            return Optional.of("Son kullanma tarihi geçersiz.");
        }
        return Optional.empty();
    }

    static boolean luhnGecerliMi(String rakamlar) {
        int toplam = 0;
        boolean ikiKati = false;
        for (int i = rakamlar.length() - 1; i >= 0; i--) {
            int r = rakamlar.charAt(i) - '0';
            if (ikiKati) {
                r *= 2;
                if (r > 9) {
                    r -= 9;
                }
            }
            toplam += r;
            ikiKati = !ikiKati;
        }
        return toplam % 10 == 0;
    }
}
