package eklegelsin.model;

public enum SiparisDurumu {
    ALINDI("Sipariş alındı"),
    HAZIRLANIYOR("Hazırlanıyor"),
    YOLDA("Yolda"),
    TESLIM_EDILDI("Teslim edildi");

    private final String gorunenAd;

    SiparisDurumu(String gorunenAd) {
        this.gorunenAd = gorunenAd;
    }

    public String gorunenAd() {
        return gorunenAd;
    }
}
