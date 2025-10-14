package eklegelsin.ui;

import eklegelsin.model.Dukkan;
import eklegelsin.model.Sepet;
import eklegelsin.model.Yemek;
import eklegelsin.service.OdemeServisi;
import eklegelsin.service.SiparisServisi;
import eklegelsin.service.VeriServisi;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EkleGelsinUI implements UI {
    private JFrame mainFrame;
    private final VeriServisi veriServisi;
    private final SiparisServisi siparisServisi;
    private final Sepet sepet;

    // ... (Constructor ve diğer metotlar aynı kalıyor) ...
    public EkleGelsinUI() {
        this.veriServisi = new VeriServisi();
        this.siparisServisi = new SiparisServisi();
        this.sepet = Sepet.getInstance();
    }
    
    // ... run(), resetToMainMenu(), showShops() metotları aynı kalıyor ...
    
    @Override
    public void run() {
        mainFrame = new JFrame("Ekle Gelsin");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBackground(Color.white);
        
        JLabel label = new JLabel("Hangi tür yemek yemek istersiniz?", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label);

        JButton donerButton = new JButton("Döner");
        donerButton.addActionListener(e -> showShops(mainFrame, "Döner", veriServisi.getDonerDukkanlari()));
        panel.add(donerButton);

        JButton pizzaButton = new JButton("Pizza");
        pizzaButton.addActionListener(e -> showShops(mainFrame, "Pizza", veriServisi.getPizzaDukkanlari()));
        panel.add(pizzaButton);

        JButton burgerButton = new JButton("Burger");
        burgerButton.addActionListener(e -> showShops(mainFrame, "Burger", veriServisi.getBurgerDukkanlari()));
        panel.add(burgerButton);
        
        mainFrame.setContentPane(panel);
        mainFrame.setVisible(true);
    }
    
    private void resetToMainMenu() {
        sepet.sepetiTemizle();
        mainFrame.getContentPane().removeAll();
        run(); // Ana menüyü yeniden çiz
        mainFrame.revalidate();
        mainFrame.repaint();
    }


    @Override
    public void showShops(JFrame previousFrame, String yemekTuru, List<Dukkan> dukkanlar) {
        previousFrame.setVisible(false);
        JFrame shopFrame = new JFrame(yemekTuru + " Dükkanları");
        shopFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        shopFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel panel = new JPanel(new GridLayout(dukkanlar.size() + 2, 1, 10, 10));
        panel.add(new JLabel("Dükkan Seçiniz:", JLabel.CENTER));

        for (Dukkan dukkan : dukkanlar) {
            JButton button = new JButton(dukkan.getAd());
            button.addActionListener(e -> showMenu(shopFrame, previousFrame, dukkan));
            panel.add(button);
        }

        JButton backButton = new JButton("Geri");
        backButton.addActionListener(e -> {
            shopFrame.dispose();
            previousFrame.setVisible(true);
        });
        panel.add(backButton);
        
        shopFrame.setContentPane(panel);
        shopFrame.setVisible(true);
    }

    // #######################################################
    // ###          DEĞİŞİKLİK BU METOT İÇERİSİNDE         ###
    // #######################################################
    @Override
    public void showMenu(JFrame shopFrame, JFrame mainFrame, Dukkan dukkan) {
        shopFrame.setVisible(false);
        JFrame menuFrame = new JFrame(dukkan.getAd() + " Menüsü");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel panel = new JPanel(new GridLayout(dukkan.getMenu().size() + 3, 1, 10, 10));
        panel.add(new JLabel("Ürün Seçiniz:", JLabel.CENTER));

        for (Yemek yemek : dukkan.getMenu()) {
            JButton button = new JButton(yemek.getAd() + " - " + String.format("%.2f₺", yemek.getFiyat()));
            
            // YENİ MANTIK BURADA
            button.addActionListener(e -> {
                if (yemek.isDrink()) {
                    // Eğer ürün içecekse, doğrudan sepete ekle
                    sepet.urunEkle(yemek);
                    JOptionPane.showMessageDialog(menuFrame, yemek.getAd() + " sepete eklendi!");
                } else {
                    // Eğer ürün yiyecekse, özelleştirme ekranını göster
                    showCustomizeMenu(menuFrame, yemek);
                }
            });
            panel.add(button);
        }

        JButton goToCartButton = new JButton("Sepete Git");
        goToCartButton.setBackground(Color.ORANGE);
        goToCartButton.addActionListener(e -> showCart(menuFrame, this.mainFrame));
        panel.add(goToCartButton);

        JButton backButton = new JButton("Geri");
        backButton.addActionListener(e -> {
            menuFrame.dispose();
            shopFrame.setVisible(true);
        });
        panel.add(backButton);

        menuFrame.setContentPane(panel);
        menuFrame.setVisible(true);
    }
    
    // ... (Geri kalan tüm metotlar aynı kalıyor) ...
    @Override
    public void showCustomizeMenu(JFrame menuFrame, Yemek orjinalYemek) {
        JDialog customizeDialog = new JDialog(menuFrame, orjinalYemek.getAd() + " Özelleştirme", true);
        customizeDialog.setSize(300, 250);
        customizeDialog.setLocationRelativeTo(menuFrame);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(new JLabel(orjinalYemek.getAd() + " için malzeme ekleyin/çıkarın:"));

        JCheckBox extraCheese = new JCheckBox("Ekstra Peynir (+10₺)");
        JCheckBox extraKetchup = new JCheckBox("Ketçap (+5₺)");
        JCheckBox extraMayonnaise = new JCheckBox("Mayonez (+5₺)");
        JCheckBox noOnions = new JCheckBox("Soğansız");
        panel.add(extraCheese);
        panel.add(extraKetchup);
        panel.add(extraMayonnaise);
        panel.add(noOnions);

        JButton addToCartButton = new JButton("Sepete Ekle");
        addToCartButton.addActionListener(e -> {
            Yemek ozellestirilmisYemek = new Yemek(orjinalYemek);
            double ekFiyat = 0;
            StringBuilder ozellestirmeler = new StringBuilder();

            if (extraCheese.isSelected()) {
                ekFiyat += 10.0;
                ozellestirmeler.append("+Peynir ");
            }
            if (extraKetchup.isSelected()) {
                ekFiyat += 5.0;
                ozellestirmeler.append("+Ketçap ");
            }
            if (extraMayonnaise.isSelected()) {
                ekFiyat += 5.0;
                ozellestirmeler.append("+Mayonez ");
            }
            if (noOnions.isSelected()) {
                ozellestirmeler.append("-Soğan ");
            }

            ozellestirilmisYemek.setFiyat(ozellestirilmisYemek.getFiyat() + ekFiyat);
            ozellestirilmisYemek.setOzellestirmeler(ozellestirmeler.toString().trim());

            sepet.urunEkle(ozellestirilmisYemek);
            JOptionPane.showMessageDialog(menuFrame, ozellestirilmisYemek.getAd() + " sepete eklendi!");
            customizeDialog.dispose();
        });
        panel.add(addToCartButton);
        
        customizeDialog.setContentPane(panel);
        customizeDialog.setVisible(true);
    }


    @Override
    public void showCart(JFrame previousFrame, JFrame mainFrame) {
        previousFrame.setVisible(false);
        JFrame cartFrame = new JFrame("Sepetiniz");
        cartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cartFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        if (sepet.isBos()) {
            panel.add(new JLabel("Sepetiniz boş!", JLabel.CENTER), BorderLayout.CENTER);
        } else {
            JPanel itemsPanel = new JPanel();
            itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
            for (Yemek yemek : sepet.getUrunler()) {
                JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                itemPanel.add(new JLabel(yemek.toString()));
                JButton removeButton = new JButton("Kaldır");
                removeButton.addActionListener(e -> {
                    sepet.urunKaldir(yemek);
                    cartFrame.dispose();
                    showCart(previousFrame, mainFrame);
                });
                itemPanel.add(removeButton);
                itemsPanel.add(itemPanel);
            }
            panel.add(new JScrollPane(itemsPanel), BorderLayout.CENTER);

            JLabel totalLabel = new JLabel("Toplam Tutar: " + String.format("%.2f₺", sepet.getToplamTutar()));
            totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
            totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(totalLabel, BorderLayout.SOUTH);
        }

        JPanel buttonPanel = new JPanel();
        JButton checkoutButton = new JButton("Ödemeye Geç");
        checkoutButton.setEnabled(!sepet.isBos());
        checkoutButton.addActionListener(e -> showPaymentScreen(cartFrame, mainFrame));
        buttonPanel.add(checkoutButton);

        JButton backButton = new JButton("Geri");
        backButton.addActionListener(e -> {
            cartFrame.dispose();
            previousFrame.setVisible(true);
        });
        buttonPanel.add(backButton);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        cartFrame.setContentPane(panel);
        cartFrame.setVisible(true);
    }

    @Override
    public void showPaymentScreen(JFrame cartFrame, JFrame mainFrame) {
        cartFrame.setVisible(false);
        JFrame paymentFrame = new JFrame("Ödeme Seçenekleri");
        paymentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        paymentFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.add(new JLabel("Ödeme yönteminizi seçin:", JLabel.CENTER));

        JButton cashButton = new JButton("Kapıda Ödeme");
        cashButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(paymentFrame, "Kapıda ödeme seçildi. Siparişiniz alındı!");
            siparisServisi.siparisSureciniBaslat(paymentFrame, this::resetToMainMenu);
        });
        panel.add(cashButton);

        JButton cardButton = new JButton("Kart ile Ödeme");
        cardButton.addActionListener(e -> showCardPaymentScreen(paymentFrame, mainFrame));
        panel.add(cardButton);
        
        JButton backButton = new JButton("Geri");
        backButton.addActionListener(e -> {
            paymentFrame.dispose();
            cartFrame.setVisible(true);
        });
        panel.add(backButton);

        paymentFrame.setContentPane(panel);
        paymentFrame.setVisible(true);
    }
    
    @Override
    public void showCardPaymentScreen(JFrame paymentFrame, JFrame mainFrame) {
        JDialog cardDialog = new JDialog(paymentFrame, "Kart Bilgileri", true);
        cardDialog.setSize(400, 250);
        cardDialog.setLocationRelativeTo(paymentFrame);
        cardDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Başlık
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Kart bilgilerinizi girin:", JLabel.CENTER), gbc);

        // Kart Numarası
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Kart Numarası:"), gbc);
        JTextField cardNumberField = new JTextField(16);
        gbc.gridx = 1;
        panel.add(cardNumberField, gbc);

        // Son Kullanma Tarihi
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Son Kullanma (AA/YY):"), gbc);
        JTextField expiryField = new JTextField(5);
        gbc.gridx = 1;
        panel.add(expiryField, gbc);

        // CVV
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("CVV:"), gbc);
        JTextField cvvField = new JTextField(3);
        gbc.gridx = 1;
        panel.add(cvvField, gbc);

        // Butonlar için panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton payButton = new JButton("Ödemeyi Tamamla");
        payButton.addActionListener(e -> {
            String cardNumber = cardNumberField.getText();
            String expiry = expiryField.getText();
            String cvv = cvvField.getText();
            
            if (OdemeServisi.kartBilgileriniDogrula(cardNumber, expiry, cvv)) {
                JOptionPane.showMessageDialog(cardDialog, "Ödeme başarılı! Siparişiniz alındı.");
                cardDialog.dispose();
                // paymentFrame'i de kapatarak doğrudan sipariş simülasyonuna geç
                siparisServisi.siparisSureciniBaslat(paymentFrame, this::resetToMainMenu);
            }
        });
        buttonPanel.add(payButton);

        JButton backButton = new JButton("Geri");
        backButton.addActionListener(e -> {
            cardDialog.dispose();
        });
        buttonPanel.add(backButton);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(buttonPanel, gbc);

        cardDialog.setContentPane(panel);
        cardDialog.setVisible(true);
    }
}