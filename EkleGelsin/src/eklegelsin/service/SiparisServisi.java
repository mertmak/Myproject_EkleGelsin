package eklegelsin.service;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class SiparisServisi {

    public void siparisSureciniBaslat(JFrame previousFrame, Runnable onComplete) {
        previousFrame.dispose();

        JFrame orderStatusFrame = new JFrame("Sipariş Durumu");
        orderStatusFrame.setSize(300, 200);
        orderStatusFrame.setLocationRelativeTo(null);
        orderStatusFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        orderStatusFrame.add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel statusLabel = new JLabel("Sipariş alındı, hazırlanıyor...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalGlue());
        panel.add(statusLabel);
        panel.add(Box.createVerticalGlue());

        orderStatusFrame.setVisible(true);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> statusLabel.setText("Sipariş hazırlanıyor..."));
            }
        }, 3000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> statusLabel.setText("Sipariş yolda..."));
            }
        }, 8000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() { 
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Sipariş teslim edildi! Afiyet olsun!");
                    // 3 saniye sonra pencereyi kapat ve ana menüye dön
                    Timer closeTimer = new Timer();
                    closeTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            SwingUtilities.invokeLater(() -> {
                                orderStatusFrame.dispose();
                                onComplete.run(); // UI'ı resetlemek için callback
                            });
                        }
                    }, 3000);
                });
            }
        }, 12000);
    }
}