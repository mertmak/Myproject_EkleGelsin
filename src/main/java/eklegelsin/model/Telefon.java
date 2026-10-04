package eklegelsin.model;

import java.util.regex.Pattern;

/** Türkiye cep telefonu numarası, her zaman {@code 05XXXXXXXXX} biçiminde saklanır. */
public record Telefon(String numara) {

    public static final String HATA_MESAJI = "Geçerli bir cep telefonu numarası girin, ör. 0532 123 45 67";

    private static final Pattern CEP_TELEFONU = Pattern.compile("05\\d{9}");

    public Telefon {
        if (numara == null || !CEP_TELEFONU.matcher(numara).matches()) {
            throw new IllegalArgumentException(HATA_MESAJI);
        }
    }

    /** "0532 123 45 67", "+90 532 123 4567", "5321234567" gibi yazımları kabul eder. */
    public static Telefon ayristir(String girdi) {
        if (girdi == null) {
            throw new IllegalArgumentException(HATA_MESAJI);
        }
        String rakamlar = girdi.replaceAll("[\\s\\-()]", "");
        if (rakamlar.startsWith("+90")) {
            rakamlar = "0" + rakamlar.substring(3);
        } else if (rakamlar.startsWith("90") && rakamlar.length() == 12) {
            rakamlar = "0" + rakamlar.substring(2);
        } else if (rakamlar.startsWith("5") && rakamlar.length() == 10) {
            rakamlar = "0" + rakamlar;
        }
        return new Telefon(rakamlar);
    }

    public static boolean gecerliMi(String girdi) {
        try {
            ayristir(girdi);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /** {@code 0532 123 45 67} */
    public String bicimli() {
        return numara.substring(0, 4) + " " + numara.substring(4, 7) + " "
                + numara.substring(7, 9) + " " + numara.substring(9);
    }
}
