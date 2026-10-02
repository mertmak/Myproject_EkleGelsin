package eklegelsin.arayuz;

import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnaPanelTest {

    @Test
    void H5_arayuzOlayThreadiDisindaOlusturulamaz() {
        assertThrows(IllegalStateException.class, AnaPanel::new);
    }

    @Test
    void H5_arayuzOlayThreadindeTemaylaOlusur() throws Exception {
        var panel = new AtomicReference<AnaPanel>();
        SwingUtilities.invokeAndWait(() -> {
            Tema.kur();
            panel.set(new AnaPanel());
        });
        assertNotNull(panel.get());
        SwingUtilities.invokeAndWait(() -> assertDoesNotThrow(() -> panel.get().goster(AnaPanel.BOS_EKRAN)));
    }
}
