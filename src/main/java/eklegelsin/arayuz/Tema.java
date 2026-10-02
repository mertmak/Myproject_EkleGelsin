package eklegelsin.arayuz;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Color;
import java.awt.Font;

/**
 * Ekle Gelsin tasarım sistemi. Renkler, yazı boyutları ve boşluklar yalnızca burada tanımlanır;
 * ekranlar bu sabitleri kullanır, kendi renklerini tanımlamaz.
 */
public final class Tema {

    public static final Color MARKA = new Color(0xE8461E);
    public static final Color ZEMIN = new Color(0xF7F5F2);
    public static final Color YUZEY = new Color(0xFFFFFF);
    public static final Color METIN = new Color(0x1F1B18);
    public static final Color METIN_IKINCIL = new Color(0x6B635C);
    public static final Color CIZGI = new Color(0xE6E1DB);

    public static final int YAZI_BOYUTU = 14;
    public static final int BOSLUK = 8;

    private Tema() {
    }

    /** Görünümü kurar. Herhangi bir bileşen oluşturulmadan önce, olay thread'inde bir kez çağrılır. */
    public static void kur() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
            // Sistem görünümü yüklenemezse Swing'in varsayılan görünümüyle devam edilir.
        }
        UIManager.put("Panel.background", ZEMIN);
        UIManager.put("Label.foreground", METIN);
        UIManager.put("Label.font", yazi(YAZI_BOYUTU, Font.PLAIN));
    }

    /** Varsayılan yazı tipinin verilen boyut ve stildeki hâli. */
    public static Font yazi(int boyut, int stil) {
        return new Font(Font.SANS_SERIF, stil, boyut);
    }
}
