package eklegelsin.model;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Restoranın açık olduğu saat aralığı. Kapanış açılıştan önceyse aralık gece yarısını aşar
 * (ör. 11:00–02:00). Açılış ve kapanış eşitse restoran 24 saat açıktır.
 */
public record CalismaSaatleri(LocalTime acilis, LocalTime kapanis) {

    public static final CalismaSaatleri HER_ZAMAN = new CalismaSaatleri(LocalTime.MIDNIGHT, LocalTime.MIDNIGHT);

    public CalismaSaatleri {
        Objects.requireNonNull(acilis, "acilis");
        Objects.requireNonNull(kapanis, "kapanis");
    }

    public boolean acikMi(LocalTime saat) {
        if (acilis.equals(kapanis)) {
            return true;
        }
        if (acilis.isBefore(kapanis)) {
            return !saat.isBefore(acilis) && saat.isBefore(kapanis);
        }
        return !saat.isBefore(acilis) || saat.isBefore(kapanis);
    }
}
