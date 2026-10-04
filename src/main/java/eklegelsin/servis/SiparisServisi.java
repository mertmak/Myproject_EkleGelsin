package eklegelsin.servis;

import eklegelsin.model.OdemeBilgisi;
import eklegelsin.model.Restoran;
import eklegelsin.model.Siparis;
import eklegelsin.model.TeslimatBilgisi;
import eklegelsin.model.TeslimatPlani;

import java.time.Clock;
import java.time.LocalTime;
import java.util.Objects;
import java.util.random.RandomGenerator;

public final class SiparisServisi {

    private final Clock saat;
    private final RandomGenerator rastgele;
    private final TeslimatPlani plan;

    public SiparisServisi(Clock saat, RandomGenerator rastgele, TeslimatPlani plan) {
        this.saat = Objects.requireNonNull(saat, "saat");
        this.rastgele = Objects.requireNonNull(rastgele, "rastgele");
        this.plan = Objects.requireNonNull(plan, "plan");
    }

    /**
     * Sepetteki ürünlerle sipariş oluşturur ve sepeti boşaltır.
     *
     * @throws SiparisVerilemezException sepet boşsa, minimum tutar karşılanmadıysa veya restoran kapalıysa
     */
    public Siparis siparisVer(Sepet sepet, TeslimatBilgisi teslimat, OdemeBilgisi odeme) {
        Restoran restoran = sepet.restoran()
                .orElseThrow(() -> new SiparisVerilemezException("Sepetiniz boş."));
        if (!sepet.minimumaKalan().sifirMi()) {
            throw new SiparisVerilemezException(restoran.ad() + " için minimum sepet tutarı "
                    + restoran.minimumSepetTutari() + ". " + sepet.minimumaKalan() + " daha ekleyin.");
        }
        if (!restoran.calismaSaatleri().acikMi(LocalTime.now(saat))) {
            throw new SiparisVerilemezException(restoran.ad() + " şu an kapalı.");
        }
        Siparis siparis = new Siparis(
                yeniNumara(),
                restoran.id(),
                restoran.ad(),
                sepet.kalemler(),
                teslimat,
                odeme,
                sepet.araToplam(),
                sepet.teslimatUcreti(),
                sepet.genelToplam(),
                saat.instant(),
                plan);
        sepet.temizle();
        return siparis;
    }

    /** ör. {@code EG-482913} */
    private String yeniNumara() {
        return "EG-%06d".formatted(rastgele.nextInt(1_000_000));
    }
}
