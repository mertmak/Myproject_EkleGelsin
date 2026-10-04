package eklegelsin.model;

import java.time.Duration;
import java.util.Objects;

/**
 * Siparişin aşamalara geçeceği anlar, sipariş anından itibaren ölçülür. Durum zamanlayıcıyla değil,
 * geçen süreden hesaplanır; bu sayede uygulama kapatılıp açılsa da takip doğru aşamadan devam eder.
 */
public record TeslimatPlani(Duration hazirlanmayaBaslama, Duration yolaCikis, Duration teslim) {

    /** Gerçek süreler yerine demo için kısaltılmış plan. */
    public static final TeslimatPlani SIMULASYON =
            new TeslimatPlani(Duration.ofSeconds(20), Duration.ofSeconds(60), Duration.ofSeconds(120));

    public TeslimatPlani {
        Objects.requireNonNull(hazirlanmayaBaslama, "hazirlanmayaBaslama");
        Objects.requireNonNull(yolaCikis, "yolaCikis");
        Objects.requireNonNull(teslim, "teslim");
        if (hazirlanmayaBaslama.isNegative()
                || yolaCikis.compareTo(hazirlanmayaBaslama) <= 0
                || teslim.compareTo(yolaCikis) <= 0) {
            throw new IllegalArgumentException("Aşama süreleri artan sırada olmalı");
        }
    }

    public SiparisDurumu durum(Duration gecenSure) {
        if (gecenSure.compareTo(hazirlanmayaBaslama) < 0) {
            return SiparisDurumu.ALINDI;
        }
        if (gecenSure.compareTo(yolaCikis) < 0) {
            return SiparisDurumu.HAZIRLANIYOR;
        }
        if (gecenSure.compareTo(teslim) < 0) {
            return SiparisDurumu.YOLDA;
        }
        return SiparisDurumu.TESLIM_EDILDI;
    }
}
