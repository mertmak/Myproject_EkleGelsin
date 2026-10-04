package eklegelsin.servis;

/** Sipariş verilemediğinde fırlatılır. Mesaj doğrudan kullanıcıya gösterilebilir. */
public class SiparisVerilemezException extends RuntimeException {

    public SiparisVerilemezException(String mesaj) {
        super(mesaj);
    }
}
