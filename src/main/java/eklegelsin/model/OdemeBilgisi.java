package eklegelsin.model;

import java.util.Objects;

/**
 * Siparişte saklanan ödeme bilgisi. Kart numarasının yalnızca son 4 hanesi tutulur.
 */
public record OdemeBilgisi(OdemeYontemi yontem, String kartSonDortHane) {

    public OdemeBilgisi {
        Objects.requireNonNull(yontem, "yontem");
        if (yontem == OdemeYontemi.ONLINE_KART) {
            if (kartSonDortHane == null || !kartSonDortHane.matches("\\d{4}")) {
                throw new IllegalArgumentException("Online kart ödemesinde kartın son 4 hanesi gerekli");
            }
        } else if (kartSonDortHane != null) {
            throw new IllegalArgumentException(yontem.gorunenAd() + " için kart bilgisi saklanmaz");
        }
    }

    public static OdemeBilgisi kapidaNakit() {
        return new OdemeBilgisi(OdemeYontemi.KAPIDA_NAKIT, null);
    }

    public static OdemeBilgisi kapidaKart() {
        return new OdemeBilgisi(OdemeYontemi.KAPIDA_KART, null);
    }

    public static OdemeBilgisi onlineKart(String sonDortHane) {
        return new OdemeBilgisi(OdemeYontemi.ONLINE_KART, sonDortHane);
    }

    /** ör. "Online kart •••• 1111" */
    public String gorunenAd() {
        return kartSonDortHane == null ? yontem.gorunenAd() : yontem.gorunenAd() + " •••• " + kartSonDortHane;
    }
}
