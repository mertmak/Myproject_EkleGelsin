package eklegelsin;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Arayüz dışındaki katmanlar JavaFX'e bağımlı olmamalı; böylece arayüz olmadan test edilebilirler.
 */
class KatmanKuraliTest {

    private static final List<String> ARAYUZSUZ_PAKETLER = List.of("model", "katalog", "veri", "servis");

    @Test
    void arayuzDisindakiPaketlerJavaFxKullanmaz() throws IOException {
        List<String> ihlaller = new java.util.ArrayList<>();
        for (String paket : ARAYUZSUZ_PAKETLER) {
            Path klasor = Path.of("src/main/java/eklegelsin", paket);
            if (!Files.isDirectory(klasor)) continue;
            try (Stream<Path> dosyalar = Files.walk(klasor)) {
                for (Path dosya : dosyalar.filter(d -> d.toString().endsWith(".java")).toList()) {
                    if (Files.readString(dosya).contains("import javafx.")) ihlaller.add(dosya.toString());
                }
            }
        }
        assertEquals(List.of(), ihlaller, "Bu dosyalar JavaFX import ediyor");
    }
}
