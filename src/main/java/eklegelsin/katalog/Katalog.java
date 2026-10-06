package eklegelsin.katalog;

import eklegelsin.model.Kategori;
import eklegelsin.model.Restoran;

import java.util.List;
import java.util.Optional;

/** Uygulamadaki tüm kategoriler ve restoranlar, dosyadaki sırayla. */
public record Katalog(List<Kategori> kategoriler, List<Restoran> restoranlar) {

    public Katalog {
        kategoriler = List.copyOf(kategoriler);
        restoranlar = List.copyOf(restoranlar);
    }

    public Optional<Kategori> kategori(String id) {
        return kategoriler.stream().filter(k -> k.id().equals(id)).findFirst();
    }

    public Optional<Restoran> restoran(String id) {
        return restoranlar.stream().filter(r -> r.id().equals(id)).findFirst();
    }

    public List<Restoran> kategoridekiRestoranlar(String kategoriId) {
        return restoranlar.stream().filter(r -> r.kategoriId().equals(kategoriId)).toList();
    }
}
