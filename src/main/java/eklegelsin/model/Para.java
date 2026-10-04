package eklegelsin.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Türk lirası tutarı. Her zaman 2 ondalık basamakla tutulur ve negatif olamaz.
 */
public record Para(BigDecimal tutar) implements Comparable<Para> {

    private static final Locale TURKCE = Locale.forLanguageTag("tr-TR");

    public static final Para SIFIR = new Para(BigDecimal.ZERO);

    public Para {
        Objects.requireNonNull(tutar, "tutar");
        if (tutar.signum() < 0) {
            throw new IllegalArgumentException("Tutar negatif olamaz: " + tutar);
        }
        tutar = tutar.setScale(2, RoundingMode.HALF_UP);
    }

    public static Para tl(String tutar) {
        return new Para(new BigDecimal(tutar));
    }

    public static Para tl(long tutar) {
        return new Para(BigDecimal.valueOf(tutar));
    }

    public Para arti(Para diger) {
        return new Para(tutar.add(diger.tutar));
    }

    /** Sonuç negatif olacaksa {@link IllegalArgumentException} fırlatır. */
    public Para eksi(Para diger) {
        return new Para(tutar.subtract(diger.tutar));
    }

    public Para carpi(int adet) {
        if (adet < 0) {
            throw new IllegalArgumentException("Adet negatif olamaz: " + adet);
        }
        return new Para(tutar.multiply(BigDecimal.valueOf(adet)));
    }

    public boolean sifirMi() {
        return tutar.signum() == 0;
    }

    public boolean kucuktur(Para diger) {
        return compareTo(diger) < 0;
    }

    @Override
    public int compareTo(Para diger) {
        return tutar.compareTo(diger.tutar);
    }

    /** Sistem dilinden bağımsız olarak her zaman Türkçe biçim: {@code ₺1.250,50}. */
    public String bicimli() {
        return NumberFormat.getCurrencyInstance(TURKCE).format(tutar);
    }

    @Override
    public String toString() {
        return bicimli();
    }
}
