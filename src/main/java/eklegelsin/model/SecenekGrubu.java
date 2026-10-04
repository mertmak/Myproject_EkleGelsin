package eklegelsin.model;

import java.util.HashSet;
import java.util.List;

/**
 * Bir ürüne ait seçenek grubu. {@code enAz > 0} ise zorunludur; {@code enCok == 1} ise tek seçimlidir.
 * Örnekler: "Porsiyon" (1–1), "Ekstralar" (0–3), "Çıkarılacaklar" (0–n).
 */
public record SecenekGrubu(String id, String ad, int enAz, int enCok, List<Secenek> secenekler) {

    public SecenekGrubu {
        id = Dogrula.bosOlamaz(id, "Seçenek grubu id");
        ad = Dogrula.bosOlamaz(ad, "Seçenek grubu adı");
        secenekler = List.copyOf(secenekler);
        if (secenekler.isEmpty()) {
            throw new IllegalArgumentException("Seçenek grubu boş olamaz: " + id);
        }
        if (enAz < 0 || enCok < 1 || enAz > enCok || enCok > secenekler.size()) {
            throw new IllegalArgumentException("Geçersiz seçim sınırları (" + enAz + "–" + enCok + "): " + id);
        }
        var idler = new HashSet<String>();
        for (Secenek s : secenekler) {
            if (!idler.add(s.id())) {
                throw new IllegalArgumentException("Tekrar eden seçenek id: " + s.id());
            }
        }
    }

    public boolean zorunluMu() {
        return enAz > 0;
    }

    public boolean tekSecimMi() {
        return enCok == 1;
    }
}
