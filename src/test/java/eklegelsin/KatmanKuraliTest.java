package eklegelsin;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Arayüz dışındaki katmanlar Swing/AWT'ye bağımlı olmamalı; böylece arayüz olmadan test edilebilirler.
 */
class KatmanKuraliTest {

    private static final List<String> ARAYUZSUZ_PAKETLER = List.of("model", "katalog", "veri", "servis");
    private static final List<String> ARAYUZ_IMPORTLARI = List.of("import javax.swing.", "import java.awt.");

    @Test
    void arayuzDisindakiPaketlerSwingKullanmaz() throws IOException {
        List<String> ihlaller = new java.util.ArrayList<>();
        for (String paket : ARAYUZSUZ_PAKETLER) {
            Path klasor = Path.of("src/main/java/eklegelsin", paket);
            if (!Files.isDirectory(klasor)) continue;
            try (Stream<Path> dosyalar = Files.walk(klasor)) {
                for (Path dosya : dosyalar.filter(d -> d.toString().endsWith(".java")).toList()) {
                    String kod = Files.readString(dosya);
                    if (ARAYUZ_IMPORTLARI.stream().anyMatch(kod::contains)) ihlaller.add(dosya.toString());
                }
            }
        }
        assertEquals(List.of(), ihlaller, "Bu dosyalar Swing/AWT import ediyor");
    }
}
