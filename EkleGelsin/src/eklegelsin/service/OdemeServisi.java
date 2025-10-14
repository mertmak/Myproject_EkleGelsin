package eklegelsin.service;

import javax.swing.JOptionPane;
import java.util.Calendar;

public class OdemeServisi {

    public static boolean kartBilgileriniDogrula(String cardNumber, String expiry, String cvv) {
        if (!cardNumber.matches("\\d{16}")) {
            JOptionPane.showMessageDialog(null, "Geçersiz kart numarası! Kart numarası 16 haneli olmalıdır.", "Hata", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!expiry.matches("(0[1-9]|1[0-2])/([0-9]{2})")) {
            JOptionPane.showMessageDialog(null, "Geçersiz son kullanma tarihi! Format: MM/YY (Örnek: 12/25)", "Hata", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            String[] expiryParts = expiry.split("/");
            int month = Integer.parseInt(expiryParts[0]);
            int year = Integer.parseInt(expiryParts[1]) + 2000;
            Calendar expiryCal = Calendar.getInstance();
            expiryCal.set(year, month - 1, 1);
            if (expiryCal.before(Calendar.getInstance())) {
                JOptionPane.showMessageDialog(null, "Kartınızın son kullanma tarihi geçmiş!", "Hata", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Son kullanma tarihi işlenirken hata oluştu!", "Hata", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!cvv.matches("\\d{3}")) {
            JOptionPane.showMessageDialog(null, "Geçersiz CVV! CVV 3 haneli olmalıdır.", "Hata", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}