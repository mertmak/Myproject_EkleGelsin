package eklegelsin.model;

/** Dakika cinsinden tahmini teslimat süresi, ör. 25–35 dk. */
public record SureAraligi(int enAzDakika, int enCokDakika) {

    public SureAraligi {
        if (enAzDakika <= 0 || enCokDakika < enAzDakika) {
            throw new IllegalArgumentException("Geçersiz süre aralığı: " + enAzDakika + "–" + enCokDakika);
        }
    }

    public String bicimli() {
        return enAzDakika + "–" + enCokDakika + " dk";
    }
}
