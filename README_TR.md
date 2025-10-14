# Ekle Gelsin - Masaüstü Yemek Sipariş Uygulaması

`Ekle Gelsin`, Java Swing kullanılarak geliştirilmiş bir masaüstü yemek sipariş simülasyonudur. Kullanıcıların farklı yemek türleri (döner, pizza, burger) arasından seçim yapmasına, restoranları listelemesine, menüleri görüntülemesine ve sipariş sürecini tamamlamasına olanak tanır.

Bu proje, başlangıçta tek bir dosyada bulunan prosedürel kodun, Nesne Yönelimli Programlama (OOP) prensiplerine uygun olarak yeniden yapılandırılmış halidir. Bu sayede kodun okunabilirliği, sürdürülebilirliği ve genişletilebilirliği artırılmıştır.

## ✨ Özellikler

- **Yemek Türü Seçimi:** Ana menüden döner, pizza veya burger gibi kategoriler seçebilme.
- **Restoran Listeleme:** Seçilen kategoriye ait restoranları dinamik olarak listeleme.
- **Menü Görüntüleme:** Restoran seçimi sonrası ürünleri ve fiyatları görüntüleme.
- **Ürün Özelleştirme:** Yiyeceklere ekstra malzeme ekleme veya çıkarma imkanı.
- **Sepet İşlemleri:** Ürünleri sepete ekleme, sepetten çıkarma ve toplam tutarı anlık olarak görme.
- **Çoklu Ödeme Seçenekleri:** Kapıda Nakit veya Kredi Kartı ile ödeme yapabilme.
- **Kredi Kartı Doğrulama:** Girilen kart bilgilerinin formatını (16 hane, son kullanma tarihi, CVV) doğrulama.
- **Sipariş Süreci Simülasyonu:** Ödeme sonrası "Sipariş alındı", "Hazırlanıyor", "Yolda" ve "Teslim edildi" gibi durumları gösteren animasyonlu bir süreç.
- **OOP Tabanlı Modüler Mimari:** Sorumlulukların `model`, `ui` ve `service` katmanlarına ayrıldığı temiz ve anlaşılır kod yapısı.

## 🛠️ Kullanılan Teknolojiler

- **Dil:** Java
- **Arayüz:** Java Swing
- **IDE:** IntelliJ IDEA / Eclipse / VS Code

## 📂 Proje Mimarisi

Proje, Tek Sorumluluk Prensibi (Single Responsibility Principle) göz önünde bulundurularak mantıksal katmanlara ayrılmıştır:

- `eklegelsin.main`
  - `EkleGelsin.java`: Uygulamayı başlatan ana sınıf.
- `eklegelsin.model`
  - `Yemek.java`, `Dukkan.java`, `Sepet.java`: Uygulamanın veri yapılarını ve temel nesnelerini temsil eden sınıflar.
- `eklegelsin.ui`
  - `EkleGelsinUI.java`, `UI.java`: Kullanıcı arayüzünü (pencereler, butonlar vb.) oluşturan ve yöneten sınıflar.
- `eklegelsin.service`
  - `VeriServisi.java`, `OdemeServisi.java`, `SiparisServisi.java`: Dükkan verilerini oluşturma, ödeme doğrulama ve sipariş simülasyonu gibi iş mantığını yürüten sınıflar.

## 🚀 Kurulum ve Çalıştırma

Projeyi yerel makinenizde çalıştırmak için aşağıdaki adımları izleyebilirsiniz.

### Gereksinimler

- Java Development Kit (JDK) 11 veya daha yeni bir sürüm.

### Adımlar

1.  **Projeyi Klonlayın:**
    ```bash
    git clone [https://github.com/kullanici-adiniz/proje-adiniz.git](https://github.com/kullanici-adiniz/proje-adiniz.git)
    ```

2.  **Dizine Gidin:**
    ```bash
    cd proje-adiniz
    ```

3.  **Derleme ve Çalıştırma (Terminal Üzerinden):**
    *Tüm `.java` dosyalarınızın `src` klasörü altında olduğunu varsayarsak:*
    ```bash
    # Class dosyaları için bir 'bin' klasörü oluşturun
    mkdir bin

    # Tüm java dosyalarını derleyip 'bin' klasörüne atın
    javac -d bin src/eklegelsin/**/*.java

    # Ana sınıfı çalıştırın
    java -cp bin eklegelsin.main.EkleGelsin
    ```

4.  **Çalıştırma (IDE Üzerinden):**
    - Projeyi tercih ettiğiniz bir IDE'de (IntelliJ, Eclipse vb.) açın.
    - `eklegelsin.main` paketindeki `EkleGelsin.java` dosyasını bulun ve çalıştırın.

## 👥 Geliştiriciler

- Mehmet Ali Salman
- Mert Mak
- Miraç Arda Seçkin
- Yusuf Demirci
- Yağız Demirci

Projeyi incelediğiniz için teşekkürler!
