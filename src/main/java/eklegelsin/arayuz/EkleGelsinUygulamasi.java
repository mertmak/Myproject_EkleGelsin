package eklegelsin.arayuz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Uygulamanın giriş noktası. Tüm ekranlar bu tek pencerenin içinde değişir.
 */
public class EkleGelsinUygulamasi extends Application {

    static final String ANA_PENCERE_FXML = "AnaPencere.fxml";
    static final String TEMA_CSS = "tema.css";

    @Override
    public void start(Stage pencere) throws IOException {
        Parent kok = FXMLLoader.load(kaynak(ANA_PENCERE_FXML));
        Scene sahne = new Scene(kok, 1200, 800);
        sahne.getStylesheets().add(kaynak(TEMA_CSS).toExternalForm());

        pencere.setTitle("Ekle Gelsin");
        pencere.setMinWidth(900);
        pencere.setMinHeight(600);
        pencere.setScene(sahne);
        pencere.show();
    }

    private static java.net.URL kaynak(String ad) {
        return Objects.requireNonNull(EkleGelsinUygulamasi.class.getResource(ad), "Kaynak bulunamadı: " + ad);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
