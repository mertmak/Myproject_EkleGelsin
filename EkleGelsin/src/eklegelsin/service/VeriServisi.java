package eklegelsin.service;

import eklegelsin.model.Dukkan;
import eklegelsin.model.Yemek;
import java.util.ArrayList;
import java.util.List;

public class VeriServisi {

    public List<Dukkan> getDonerDukkanlari() {
        List<Dukkan> donerDukkanlari = new ArrayList<>();

        Dukkan aliUsta = new Dukkan("Dönerci Ali Usta");
        aliUsta.menuEkle(new Yemek("Kola", 50.0, true)); // Güncellendi
        aliUsta.menuEkle(new Yemek("Ayran", 40.0, true)); // Güncellendi
        aliUsta.menuEkle(new Yemek("Su", 15.0, true));   // Güncellendi
        aliUsta.menuEkle(new Yemek("Et Döner", 150.0));
        aliUsta.menuEkle(new Yemek("Tavuk Döner", 120.0));
        aliUsta.menuEkle(new Yemek("İskender", 180.0));

        Dukkan donerDunyasi = new Dukkan("Döner Dünyası");
        donerDunyasi.menuEkle(new Yemek("Kola", 50.0, true)); // Güncellendi
        donerDunyasi.menuEkle(new Yemek("Ayran", 40.0, true)); // Güncellendi
        donerDunyasi.menuEkle(new Yemek("Su", 15.0, true));   // Güncellendi
        donerDunyasi.menuEkle(new Yemek("Sade Döner", 130.0));
        donerDunyasi.menuEkle(new Yemek("Pilav Üstü Döner", 160.0));

        donerDukkanlari.add(aliUsta);
        donerDukkanlari.add(donerDunyasi);
        return donerDukkanlari;
    }

    public List<Dukkan> getPizzaDukkanlari() {
        List<Dukkan> pizzaDukkanlari = new ArrayList<>();

        Dukkan pizzaHut = new Dukkan("Pizza Hut");
        pizzaHut.menuEkle(new Yemek("Kola", 50.0, true));       // Güncellendi
        pizzaHut.menuEkle(new Yemek("Soğuk Çay", 50.0, true)); // Güncellendi
        pizzaHut.menuEkle(new Yemek("Margherita", 200.0));
        pizzaHut.menuEkle(new Yemek("Pepperoni", 220.0));
        pizzaHut.menuEkle(new Yemek("Vegetarian", 190.0));

        Dukkan littleItaly = new Dukkan("Little Italy");
        littleItaly.menuEkle(new Yemek("Kola", 50.0, true)); // Güncellendi
        littleItaly.menuEkle(new Yemek("Su", 15.0, true));   // Güncellendi
        littleItaly.menuEkle(new Yemek("Napolitan", 230.0));
        littleItaly.menuEkle(new Yemek("Quattro Formaggi", 250.0));

        pizzaDukkanlari.add(pizzaHut);
        pizzaDukkanlari.add(littleItaly);
        return pizzaDukkanlari;
    }

    public List<Dukkan> getBurgerDukkanlari() {
        List<Dukkan> burgerDukkanlari = new ArrayList<>();

        Dukkan burgerKing = new Dukkan("Burger King");
        burgerKing.menuEkle(new Yemek("Kola", 50.0, true)); // Güncellendi
        burgerKing.menuEkle(new Yemek("Whopper", 170.0));
        burgerKing.menuEkle(new Yemek("Cheeseburger", 150.0));
        burgerKing.menuEkle(new Yemek("Chicken Royale", 160.0));

        Dukkan bigBurger = new Dukkan("Big Burger");
        bigBurger.menuEkle(new Yemek("Ayran", 40.0, true)); // Güncellendi
        bigBurger.menuEkle(new Yemek("Double Burger", 190.0));
        bigBurger.menuEkle(new Yemek("Classic Burger", 140.0));
        
        burgerDukkanlari.add(burgerKing);
        burgerDukkanlari.add(bigBurger);
        return burgerDukkanlari;
    }
}