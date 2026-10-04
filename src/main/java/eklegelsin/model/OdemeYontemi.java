package eklegelsin.model;

public enum OdemeYontemi {
    KAPIDA_NAKIT("Kapıda nakit"),
    KAPIDA_KART("Kapıda kart"),
    ONLINE_KART("Online kart");

    private final String gorunenAd;

    OdemeYontemi(String gorunenAd) {
        this.gorunenAd = gorunenAd;
    }

    public String gorunenAd() {
        return gorunenAd;
    }
}
