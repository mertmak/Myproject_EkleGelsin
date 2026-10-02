package eklegelsin.arayuz;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Dimension;

/**
 * Uygulamanın giriş noktası. Tüm ekranlar bu tek pencerenin içinde değişir.
 */
public final class EkleGelsinUygulamasi {

    private EkleGelsinUygulamasi() {
    }

    public static void main(String[] args) {
        // Swing bileşenleri yalnızca olay thread'inde oluşturulur (H5).
        SwingUtilities.invokeLater(EkleGelsinUygulamasi::pencereyiAc);
    }

    private static void pencereyiAc() {
        Tema.kur();

        var pencere = new JFrame("Ekle Gelsin");
        pencere.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pencere.setContentPane(new AnaPanel());
        pencere.setMinimumSize(new Dimension(900, 600));
        pencere.setSize(1200, 800);
        pencere.setLocationRelativeTo(null);
        pencere.setVisible(true);
    }
}
