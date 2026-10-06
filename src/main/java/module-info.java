module eklegelsin {
    requires java.desktop;
    requires tools.jackson.databind;

    opens eklegelsin.katalog to tools.jackson.databind;
}
