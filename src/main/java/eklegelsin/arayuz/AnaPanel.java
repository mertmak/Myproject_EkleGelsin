package eklegelsin.arayuz;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;

/**
 * Ana pencerenin tüm içeriği: üst bar ve altında ekranlar. Ekranlar {@link CardLayout} ile bu panelin
 * içinde değişir, hiçbir zaman yeni pencere açılmaz (H2).
 */
public class AnaPanel extends JPanel {

    static final String BOS_EKRAN = "bos";

    private final CardLayout ekranDuzeni = new CardLayout();
    private final JPanel ekranlar = new JPanel(ekranDuzeni);

    public AnaPanel() {
        super(new BorderLayout());
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Arayüz yalnızca Swing olay thread'inde (EDT) oluşturulabilir");
        }
        setBackground(Tema.ZEMIN);
        add(ustBar(), BorderLayout.NORTH);

        ekranlar.setOpaque(false);
        ekranlar.add(bosDurum(), BOS_EKRAN);
        add(ekranlar, BorderLayout.CENTER);
    }

    /** Daha önce eklenmiş bir ekranı gösterir. */
    public void goster(String ekran) {
        ekranDuzeni.show(ekranlar, ekran);
    }

    private static JComponent ustBar() {
        var bar = new JPanel(new BorderLayout());
        bar.setBackground(Tema.YUZEY);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.CIZGI),
                BorderFactory.createEmptyBorder(2 * Tema.BOSLUK, 4 * Tema.BOSLUK, 2 * Tema.BOSLUK, 4 * Tema.BOSLUK)));

        var logo = new JLabel("Ekle Gelsin");
        logo.setFont(Tema.yazi(22, Font.BOLD));
        logo.setForeground(Tema.MARKA);
        bar.add(logo, BorderLayout.WEST);
        return bar;
    }

    private static JComponent bosDurum() {
        var baslik = new JLabel("Yeni Ekle Gelsin yapım aşamasında");
        baslik.setFont(Tema.yazi(20, Font.BOLD));
        var aciklama = new JLabel("Restoranlar ve menüler yakında burada olacak.");
        aciklama.setForeground(Tema.METIN_IKINCIL);

        var icerik = new JPanel();
        icerik.setOpaque(false);
        icerik.setLayout(new BoxLayout(icerik, BoxLayout.Y_AXIS));
        for (JComponent satir : new JComponent[] {baslik, aciklama}) {
            satir.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        icerik.add(baslik);
        icerik.add(Box.createVerticalStrut(Tema.BOSLUK));
        icerik.add(aciklama);

        var ortala = new JPanel(new GridBagLayout());
        ortala.setOpaque(false);
        ortala.add(icerik);
        return ortala;
    }
}
