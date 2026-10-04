package eklegelsin.model;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Verilmiş bir sipariş. Ürünler, fiyatlar ve restoran adı sipariş anındaki halleriyle saklanır;
 * katalog sonradan değişse de geçmiş sipariş değişmez.
 */
public record Siparis(
        String numara,
        String restoranId,
        String restoranAdi,
        List<SepetKalemi> kalemler,
        TeslimatBilgisi teslimat,
        OdemeBilgisi odeme,
        Para araToplam,
        Para teslimatUcreti,
        Para genelToplam,
        Instant verilmeZamani,
        TeslimatPlani plan) {

    public Siparis {
        numara = Dogrula.bosOlamaz(numara, "Sipariş numarası");
        restoranId = Dogrula.bosOlamaz(restoranId, "Restoran id");
        restoranAdi = Dogrula.bosOlamaz(restoranAdi, "Restoran adı");
        kalemler = List.copyOf(kalemler);
        if (kalemler.isEmpty()) {
            throw new IllegalArgumentException("Sipariş en az bir ürün içermeli");
        }
        Objects.requireNonNull(teslimat, "teslimat");
        Objects.requireNonNull(odeme, "odeme");
        Objects.requireNonNull(verilmeZamani, "verilmeZamani");
        Objects.requireNonNull(plan, "plan");
        Para kalemToplami = kalemler.stream().map(SepetKalemi::toplam).reduce(Para.SIFIR, Para::arti);
        if (!kalemToplami.equals(araToplam)) {
            throw new IllegalArgumentException("Ara toplam kalemlerle uyuşmuyor: " + araToplam + " ≠ " + kalemToplami);
        }
        if (!araToplam.arti(teslimatUcreti).equals(genelToplam)) {
            throw new IllegalArgumentException("Genel toplam, ara toplam ve teslimat ücretiyle uyuşmuyor");
        }
    }

    public SiparisDurumu durum(Instant simdi) {
        return plan.durum(Duration.between(verilmeZamani, simdi));
    }

    public Instant tahminiTeslimZamani() {
        return verilmeZamani.plus(plan.teslim());
    }

    public int urunAdedi() {
        return kalemler.stream().mapToInt(SepetKalemi::adet).sum();
    }
}
