package eklegelsin.ui;

import eklegelsin.model.Dukkan;
import eklegelsin.model.Yemek;

import javax.swing.JFrame;
import java.util.List;

public interface UI {
    void showShops(JFrame previousFrame, String yemekTuru, List<Dukkan> dukkanlar);
    void showMenu(JFrame shopFrame, JFrame mainFrame, Dukkan dukkan);
    void showCustomizeMenu(JFrame menuFrame, Yemek yemek);
    void showCart(JFrame previousFrame, JFrame mainFrame);
    void showPaymentScreen(JFrame cartFrame, JFrame mainFrame);
    void showCardPaymentScreen(JFrame paymentFrame, JFrame mainFrame);
    void run();
}