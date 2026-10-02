# Ekle Gelsin — Mevcut Durum Raporu

*İnceleme tarihi: 2 Ekim 2026 · İncelenen commit: `0a7af30` · Test ortamı: macOS, JDK 25*

## Özet

Uygulama derleniyor ve "kategori → dükkan → menü → özelleştirme → sepet → ödeme → teslimat animasyonu" akışı baştan sona çalışıyor. Ancak proje bir **sınıf ödevi / prototip** seviyesinde:

- Doğrulanmış **8 hata** var. En ciddileri: bu ay sonuna kadar geçerli kartların reddedilmesi, her siparişte pencerelerin bellekte birikmesi ve farklı restoranların ürünlerinin tek sepette tek sipariş olabilmesi.
- Gerçek bir sipariş uygulamasının temel parçaları eksik: adres, telefon, adet, sipariş kaydı, kalıcı veri.
- Build aracı, otomatik test ve CI yok.
- Arayüz, tam ekrana yayılmış varsayılan Swing butonlarından oluşuyor. Görsel, logo, ürün kartı ve tutarlı tasarım yok.

Kod tabanı küçük (8 sınıf, ~720 satır). Bu yüzden yeniden yapılandırma maliyeti düşük.

## Nasıl test edildi

1. **Derleme:** `javac -d bin $(find src -name '*.java')` hatasız derleniyor.
2. **Birim testleri:** Model sınıfları ve `OdemeServisi` için 15 kontrol yazıldı. Sonuç: **6 geçti, 9 kaldı**.
3. **Uçtan uca arayüz testi:** Gerçek pencereler açılıp butonlara programla tıklandı. Akış: Döner → Dönerci Ali Usta → Kola + Et Döner (ekstra peynirli) → geri → Pizza Hut → Kola → Sepet → Kapıda Ödeme → teslimat animasyonu → ana menü. Akış **iki kez üst üste** çalıştırıldı ve her adımda açık pencere sayısı ölçüldü.

> Bu testler henüz projeye eklenmedi; yol haritasının 1. fazında JUnit testi olarak eklenecekler.

## Doğrulanmış hatalar

| # | Önem | Hata | Kanıt |
|---|------|------|-------|
| H1 | Yüksek | **Bu ay sonuna kadar geçerli kart reddediliyor.** `OdemeServisi` son kullanma tarihini ayın 1'i olarak alıp bugünle karşılaştırıyor. Oysa kart ayın son gününe kadar geçerlidir. | Bugün 02.10.2026: `10/26` → "Kartınızın son kullanma tarihi geçmiş!" |
| H2 | Yüksek | **Her siparişte pencereler sızıyor.** Ekranlar `dispose()` yerine `setVisible(false)` ile gizleniyor. `resetToMainMenu()` eski ana pencereyi kapatmadan yenisini açıyor. | 1. sipariş sonrası 5, 2. sipariş sonrası 9 canlı `JFrame` (her siparişte +4). |
| H3 | Yüksek | **Farklı restoranlardan ürünler aynı sepette, tek siparişte.** `Sepet` uygulama genelinde tek bir singleton. `Yemek` hangi dükkana ait olduğunu bilmiyor. | Sepet: Dönerci Ali Usta'dan Kola + Et Döner ve Pizza Hut'tan Kola → tek ödeme. Hangi Kola'nın nereden geldiği görünmüyor. |
| H4 | Orta | **Kart numarası kontrolü hem fazla katı hem fazla gevşek.** Boşluklu (`4111 1111 1111 1111`) ve başında/sonunda boşluk olan numaralar reddediliyor. Amex (15 hane, 4 haneli CVV) desteklenmiyor. Luhn kontrolü olmadığı için `1234567812345678` gibi geçersiz numaralar kabul ediliyor. | Birim testi |
| H5 | Orta | **Swing kuralları ihlal ediliyor.** Arayüz `main` thread'inde oluşturuluyor (`SwingUtilities.invokeLater` yok). Bu durum seyrek ve tekrarlanması zor arayüz hatalarına yol açabilir. | `EkleGelsin.main` |
| H6 | Düşük | **Özelleştirmesiz ürünlerde çift boşluk:** `"Kola  - 50,00₺"`. `getTamAd()` her zaman boşluk ekliyor. | Birim testi, sepet ekranı |
| H7 | Düşük | **Fiyat formatı sistem diline bağlı.** Türkçe sistemde `50,00₺`, İngilizce sistemde `50.00₺` görünüyor. | `String.format("%.2f₺")` varsayılan Locale'i kullanıyor |
| H8 | Düşük | **Validasyon servisi arayüze bağımlı.** `OdemeServisi` hata durumunda kendisi `JOptionPane` açıyor. Bu yüzden arayüzsüz test edilemiyor (headless modda `HeadlessException`). | Birim testi |

### Kod okumasıyla tespit edilen (çalıştırılarak doğrulanmadı)

- Sipariş durumu penceresi X ile kapatılırsa ekranda hiç pencere kalmıyor. Zamanlayıcılar ~15 sn sonra ana menüyü kendiliğinden geri açıyor.
- Diğer tüm pencerelerde `EXIT_ON_CLOSE` var: sepet veya menü penceresini kapatmak uygulamadan uyarısız çıkıyor.
- Teslimat animasyonu `java.util.Timer` kullanıyor. Swing için doğru araç olan `javax.swing.Timer` kullanılmıyor. Ölçümde thread'ler zamanla sonlandı, kalıcı bir sızıntı görülmedi.
- Kart CVV'si düz `JTextField` ile alınıyor, maskelenmiyor.

## Eksik temel özellikler

Gerçek bir yemek sipariş uygulamasında olması beklenip burada olmayanlar:

- **Teslimat bilgisi:** adres, telefon, sipariş notu yok. "Kapıda ödeme" seçilince sipariş doğrudan alınıyor.
- **Adet:** Aynı ürün iki kez eklenince sepette iki ayrı satır oluşuyor. Adet artırma/azaltma yok.
- **Sipariş özeti ve onay:** Ödemeden önce özet ekranı, sipariş numarası ve sipariş geçmişi yok.
- **Kalıcılık:** Tüm dükkan/menü verisi `VeriServisi` içinde koda gömülü. Sipariş hiçbir yere kaydedilmiyor.
- **Ürüne özel özelleştirme:** Her yemeğe aynı 4 seçenek sunuluyor (ör. İskender'e "Ketçap", Pizza'ya "Soğansız"). Seçenekler ve fiyatları arayüz kodunda sabit.
- **Restoran kuralları:** minimum sepet tutarı, teslimat ücreti, çalışma saatleri, tahmini süre yok.
- **Arama/filtre, ürün açıklaması, görsel** yok.

## Mimari ve kod kalitesi

**İyi olanlar**
- `model` / `service` / `ui` ayrımı ve `UI` arayüzü doğru yönde atılmış adımlar.
- `Sepet.getUrunler()` savunmacı kopya döndürüyor, `Yemek` kopya kurucusuyla özelleştirme orijinal menüyü bozmuyor (testle doğrulandı).
- `Yemek.equals/hashCode` özelleştirmeleri hesaba katıyor, sepetten doğru ürün kaldırılıyor.

**Sorunlar**
- **Her ekran yeni bir tam ekran `JFrame`.** Görev çubuğu karışıyor ve geçişlerde titreme oluyor. H2'deki sızıntının da kaynağı bu. Normalde tek pencere içinde `CardLayout` ile ekran değiştirilir.
- **`EkleGelsinUI` 354 satırlık tek sınıf.** Ekranların hepsi, iş kuralları (özelleştirme fiyatları) ve navigasyon tek yerde duruyor.
- **Para `double` ile tutuluyor.** Şu anki tam sayı fiyatlarda sorun çıkmıyor. Ancak indirim/KDV gibi hesaplarda yuvarlama hatası verir; `BigDecimal` kullanılmalı.
- **Global durum:** `Sepet` singleton olduğu için test ve çoklu sepet zorlaşıyor (H3).
- **İsimlendirme tutarsız:** model ve servisler Türkçe (`urunEkle`), arayüz metotları İngilizce (`showCart`, `cardNumber`). Ayrıca `// YENİ CONSTRUCTOR`, `// Güncellendi` gibi eski not yorumları duruyor.
- **Hata yönetimi:** `catch (Exception ex)` ile her şey yutuluyor, loglama yok.

## Görünüm (arayüz)

- Varsayılan Swing "Metal" teması kullanılıyor; macOS/Windows'ta eski görünüyor.
- `GridLayout` butonları tüm ekran boyunca uzatıyor. 3 butonlu bir ana menü tam ekranı kaplıyor.
- Logo, ürün görseli, renk paleti, ikon yok. Sabit "Arial" fontu kullanılıyor.
- Sepet her ekranda görünmüyor (ürün sayısı/tutar rozeti yok).
- Pencere boyutları sabit (`setSize(300, 250)`). Yüksek DPI ekranlarda özelleştirme penceresi taşabilir.

## Altyapı

| Konu | Durum |
|------|-------|
| Build aracı (Maven/Gradle) | Yok. Sadece Eclipse proje dosyaları (`.classpath`, `.settings`) var. |
| Testler | Yok. |
| CI | Yok. |
| Paketleme | Yok. Çalıştırmak için JDK ve terminal gerekiyor. |
| Java sürümü | README "JDK 11+" diyor, Eclipse ayarı JavaSE-22 (LTS olmayan sürüm). |
| Dokümantasyon | README İngilizce ve Türkçe mevcut, bu turda düzeltildi. |

## Canlıya çıkış açısından engeller

Bu maddeler bir hata değil. Ancak proje gerçek kullanıcıya açılacaksa bunlar çözülmeden açılamaz:

1. **Kart bilgisi uygulamada toplanıyor.** Gerçek ödemede kart numarası/CVV uygulamaya hiç girilmemeli. Ödeme, PCI DSS uyumlu bir sağlayıcının (iyzico, PayTR, Stripe vb.) güvenli ödeme sayfası üzerinden alınmalı.
2. **Gerçek marka adları** ("Pizza Hut", "Burger King") kullanılıyor. Demo için sorun değil, yayında marka hakkı sorunu doğurur.
3. **Sunucu yok.** Siparişler restorana ulaşmıyor. Gerçek bir sipariş sistemi müşteri uygulaması, sunucu ve restoran tarafından oluşur.
4. **KVKK:** Adres/telefon toplanmaya başlandığında aydınlatma metni ve veri saklama politikası gerekir.
