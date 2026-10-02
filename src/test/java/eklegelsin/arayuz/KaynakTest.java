package eklegelsin.arayuz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class KaynakTest {

    @Test
    void anaPencereVeTemaPaketeDahil() {
        assertNotNull(EkleGelsinUygulamasi.class.getResource(EkleGelsinUygulamasi.ANA_PENCERE_FXML));
        assertNotNull(EkleGelsinUygulamasi.class.getResource(EkleGelsinUygulamasi.TEMA_CSS));
    }
}
