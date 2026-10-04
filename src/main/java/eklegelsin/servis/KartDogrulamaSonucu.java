package eklegelsin.servis;

import java.util.Map;
import java.util.Optional;

/**
 * Kart doğrulamasının sonucu. Hatalar alan bazında döner; arayüz her mesajı ilgili alanın altında gösterir.
 */
public record KartDogrulamaSonucu(Map<KartDogrulayici.Alan, String> hatalar, String sonDortHane) {

    public KartDogrulamaSonucu {
        hatalar = Map.copyOf(hatalar);
    }

    public boolean gecerliMi() {
        return hatalar.isEmpty();
    }

    public Optional<String> hata(KartDogrulayici.Alan alan) {
        return Optional.ofNullable(hatalar.get(alan));
    }
}
