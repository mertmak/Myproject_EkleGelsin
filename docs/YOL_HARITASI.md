# Ekle Gelsin — Yol Haritası

Bu yol haritası [MEVCUT_DURUM.md](MEVCUT_DURUM.md) raporundaki bulgulara dayanır. Hata kodları (H1–H8) o rapora atıftır.

## Hedef ve kapsam

Ekle Gelsin, **sıfırdan yeniden yazılarak** modern görünümlü, gerçekçi restoran ve ürünlere sahip, verileri kalıcı olan bir masaüstü yemek sipariş **simülasyonuna** dönüştürülecek.

- **Kapsamda:** modern arayüz, gerçekçi katalog (restoran, ürün, görsel, açıklama), eksiksiz sipariş akışı, uygulama kapatılınca kaybolmayan veriler (siparişler, adresler, sepet), kurulabilir paket.
- **Kapsam dışı:** gerçek kullanıcıya açılış, sunucu, gerçek ödeme, kullanıcı hesapları. Ödeme ve teslimat simüle edilir.

Eski kod düzeltilmeyecek, yerine yenisi yazılacak. Eski koddaki hatalar (H1–H8) yeni tasarımda baştan önlenecek ve her biri için bir test yazılacak (bkz. [Eski hataların karşılığı](#eski-hataların-karşılığı)).

**Boyut ölçeği:** S = birkaç saat · M = 1–3 gün · L = 1 hafta+ (tek geliştirici, yarı zamanlı varsayımıyla kaba tahmin)

---

## Teknoloji kararı

| Konu | Seçim | Neden |
|------|-------|-------|
| Dil | **Java 25 (LTS)** | Ekibin bildiği dil. Masaüstü uygulaması için olgun ve yeterli. Güncel uzun süreli destekli sürüm. |
| Arayüz | **JavaFX 25** (Swing yerine) | CSS ile tasarım, görsel/animasyon desteği, modern bileşenler. Swing'de kart tabanlı modern bir tasarım çok zahmetli. |
| Ekran tanımı | FXML + CSS | Görünüm koddan ayrılır, tasarım tek bir stil dosyasından yönetilir. |
| Build | **Maven** (Maven Wrapper ile) + `javafx-maven-plugin` | Maven kurmaya gerek yok. Tek komutla çalıştırma: `./mvnw javafx:run` |
| Kalıcı veri | **SQLite** (`sqlite-jdbc`) | Tek dosyalık veritabanı, kurulum gerektirmez. Uygulama kapanınca veriler korunur. |
| Katalog verisi | JSON (`src/main/resources`) + **Jackson** | Restoran/ürün eklemek kod değişikliği gerektirmez. |
| Test | **JUnit 6**, arayüz için isteğe bağlı TestFX | Model, servis ve veritabanı katmanı arayüzsüz test edilir. |
| Paketleme | `jlink` + `jpackage` | macOS `.dmg` / Windows `.msi`. Kullanıcının Java kurmasına gerek kalmaz. |

> **Neden Java'da kalıyoruz?** Uygulama gerçek kullanıcıya açılmayacak ve tek bilgisayarda çalışacak. Bu durumda dil değiştirmenin getirisi yok. Modern görünüm sorunu Swing'den kaynaklanıyor, Java'dan değil. En modern görünüm web teknolojileriyle (React vb.) elde edilir, ama bu tamamen yeni bir dil ve araç seti öğrenmek demek. JavaFX, Java'da kalarak bu farkın büyük kısmını kapatıyor.

---

## Faz 0 — Proje iskeleti · *S–M* · ✅ Tamamlandı

- [x] Eski kod git'te etiketlensin (`git tag v1-swing`). Böylece silinse bile her zaman geri bakılabilir.
- [x] Yeni Maven projesi (Java 25, JavaFX 25), repo kökünde. Paket yapısı (paketler, içlerine kod eklendikçe oluşur):
  - `model`: domain sınıfları
  - `katalog`: JSON'dan restoran/ürün okuma
  - `veri`: SQLite depoları
  - `servis`: sepet, sipariş ve ödeme mantığı. `javafx` import etmez.
  - `arayuz`: ekranlar, FXML, CSS
- [x] JUnit 6 kurulumu ve GitHub Actions CI (her push'ta `./mvnw verify`).
- [x] Mimari kural testi (`KatmanKuraliTest`): `model`, `katalog`, `veri` ve `servis` paketleri JavaFX import ederse build kırılır.
- [x] Boş bir JavaFX penceresi `./mvnw javafx:run` ile açılıyor.
- [x] README ve `.gitignore` yeni yapıya göre güncellendi. Eski Swing projesinin Eclipse dosyaları, eski kodla birlikte Faz 5'te kaldırılacak.

**Bitti sayılır:** `./mvnw verify` CI'da yeşil, `./mvnw javafx:run` boş ana pencereyi açıyor.

## Faz 1 — Domain modeli ve iş kuralları · *M*

Arayüzden tamamen bağımsız, baştan testli çekirdek.

- [ ] **Para:** `BigDecimal` tabanlı `Para` sınıfı. Tüm biçimlendirme `tr-TR` ile tek yerden yapılsın (`₺150,00`) (H6, H7).
- [ ] **Model:**
  - `Kategori`
  - `Restoran`: ad, logo, kapak görseli, puan, tahmini süre, minimum sepet tutarı, teslimat ücreti, çalışma saatleri
  - `Urun`: ad, açıklama, görsel, fiyat, menü bölümü (ör. "Dönerler", "İçecekler")
  - `SecenekGrubu` / `Secenek`: ürüne özel seçenekler, ör. "Porsiyon: 1 / 1,5", "Ekstralar: +Kaşar ₺15", "Çıkarılacaklar: Soğan"
  - `SepetKalemi`: ürün, seçimler, adet
  - `Adres`
  - `Siparis`: numara, restoran, kalemler, adres, ödeme yöntemi, tutarlar, zaman damgaları
- [ ] **Sepet servisi:** sepet tek bir restorana bağlı. Başka restorandan ürün eklenince "sepet temizlensin mi?" kararı istenir (H3). Aynı ürün ve aynı seçimler tekrar eklenince adet artar. Ara toplam, teslimat ücreti ve minimum tutar kontrolü burada yapılır.
- [ ] **Ödeme doğrulama (simülasyon):** boşluk temizleme, Luhn kontrolü, 13–19 hane, Amex için 4 haneli CVV. Son kullanma tarihi `YearMonth` ile kontrol edilir (H1, H4). Sonuç arayüze bağımlı olmayan bir doğrulama sonucu nesnesi olarak dönülür (H8).
- [ ] **Sipariş durumu zamana bağlı hesaplansın:** durum, sipariş anından geçen süreye göre bulunur (alındı → hazırlanıyor → yolda → teslim edildi). Uygulama kapatılıp açılsa da takip doğru yerden devam eder; zamanlayıcıya bağımlı değildir.

**Bitti sayılır:** model ve servis katmanında %80+ test kapsamı. H1, H3, H4, H6, H7, H8 için yazılan testler geçiyor.

## Faz 2 — Gerçekçi katalog · *M*

- [ ] **Kategoriler:** Döner, Pizza, Burger, Lahmacun & Pide, Tatlı, Kahve (en az 5).
- [ ] **Restoranlar:** her kategoride 3–4 adet, toplam ~20. Her birinin kendine özgü adı, kısa tanıtımı, logosu, puanı, teslimat süresi, minimum tutarı ve teslimat ücreti olsun.
- [ ] **Ürünler:** restoran başına 8–15 adet, menü bölümlerine ayrılmış. Her üründe gerçekçi açıklama ("Yaprak döner, lavaş, domates, soğan, sumak"), güncel ve gerçekçi fiyat ve görsel olsun.
- [ ] **Görseller:** lisansı serbest kaynaklardan (Unsplash, Pexels) alınsın, ~600 px genişliğe küçültülüp `resources/gorseller/` altına konsun. Kaynaklar `resources/gorseller/KAYNAKLAR.md` dosyasında listelensin. Görseli olmayan ürün için şık bir yer tutucu kullanılsın.
- [ ] **Marka adları:** gerçek zincir adları ("Pizza Hut", "Burger King") ve logoları yerine gerçekçi ama kurgusal adlar kullanılsın. Repo herkese açık olduğu için marka hakkı sorununu önler.
- [ ] Katalog yüklenirken doğrulama yapılsın (eksik görsel, negatif fiyat, tekrar eden id). Test, gerçek katalog dosyasının geçerli olduğunu doğrulasın.

**Bitti sayılır:** yeni bir restoran eklemek sadece JSON dosyası ve görsel eklemekten ibaret.

## Faz 3 — Kalıcı veri · *M*

Uygulama kapatılınca hiçbir şey kaybolmasın.

- [ ] SQLite veritabanı kullanıcı klasöründe dursun (`~/.eklegelsin/eklegelsin.db`). Proje klasöründe veya kurulum dizininde tutulmasın.
- [ ] **Kaydedilenler:**
  - Siparişler: kalemler, seçimler, fiyatlar, adres, ödeme yöntemi, zaman damgaları
  - Kayıtlı adresler
  - Açık sepet: uygulama kapanıp açılınca sepet yerinde olsun
  - Favori restoranlar
- [ ] **Siparişte fiyatlar o anki haliyle saklansın.** Katalogda fiyat sonradan değişse de geçmiş siparişin tutarı değişmez.
- [ ] **Kart bilgisi kaydedilmesin.** Siparişte sadece "Kart •••• 1111" saklansın.
- [ ] Şema sürümlemesi yapılsın: basit bir `schema_version` tablosu ve sıralı SQL dosyaları. Gelecekteki değişiklikler mevcut veriyi bozmaz.
- [ ] Depo sınıfları (`SiparisDeposu`, `AdresDeposu`, `SepetDeposu`) arayüz arkasında dursun. Testler geçici bir veritabanı dosyasıyla çalışsın.

**Bitti sayılır:** sipariş ver → uygulamayı kapat → aç: sipariş geçmişte duruyor, takip doğru aşamadan devam ediyor, yarım bırakılan sepet yerinde.

## Faz 4 — Arayüz ve sipariş akışı · *L*

Tek pencere. Ekranlar bu pencerenin içinde değişir, pencere sızıntısı yapısal olarak imkânsız (H2). Arayüz JavaFX uygulama thread'inde başlar (H5).

**Tasarım sistemi**
- [ ] Tek bir `tema.css`: marka rengi, nötr tonlar, boşluk ölçeği, köşe yuvarlaklığı, gölge, tipografi. (Faz 0'da temel renklerle başlatıldı.)
- [ ] **Yazı tipi uygulamayla paketlensin** (ör. OFL lisanslı Inter). JavaFX'in "System" yazı tipi bu macOS sürümünde Gill Sans'a düşüyor ve kalın yazı çalışmıyor. Paketlenen yazı tipi her bilgisayarda aynı görünüm sağlar.
- [ ] Açık ve koyu tema.
- [ ] Tekrar kullanılan bileşenler: restoran kartı, ürün kartı, adet seçici, rozet, boş durum görünümü, bildirim (toast).
- [ ] Logo ve uygulama ikonu.

**Ekranlar ve akış**
1. [ ] **Ana sayfa:** kategori şeridi (görselli), "Popüler restoranlar", arama kutusu.
2. [ ] **Restoran listesi:** kartlarda kapak görseli, puan, süre, minimum tutar ve teslimat ücreti. Sıralama (puan, süre, fiyat) ve "şu an açık" filtresi.
3. [ ] **Restoran sayfası:** kapak ve bilgiler, menü bölümleri arası sekme ile geçiş, ürün kartları (görsel, ad, açıklama, fiyat, "+" butonu).
4. [ ] **Ürün detayı (pencere üstü panel):** büyük görsel, seçenek grupları (zorunlu ve isteğe bağlı), adet, canlı güncellenen fiyat.
5. [ ] **Sepet (yan panel):** her ekrandan açılabilsin. Adet artır/azalt, sil, ara toplam, teslimat ücreti, genel toplam. Minimum tutara ne kadar kaldığı gösterilsin. Üst barda ürün sayısı ve tutar rozeti olsun.
6. [ ] **Ödeme adımı:**
   - Teslimat adresi: kayıtlı adreslerden seçme veya yeni ekleme, form doğrulamalı
   - Telefon ve sipariş notu
   - Ödeme yöntemi: kapıda nakit, kapıda kart veya online kart (simülasyon). Kart formunda 4'lü gruplama, AA/YY maskesi ve gizli CVV olsun. Hata mesajları alanın altında gösterilsin.
7. [ ] **Sipariş özeti ve onay:** son kontrol ekranı. Sonrasında sipariş numarası verilir.
8. [ ] **Sipariş takibi:** aşama göstergesi, tahmini teslim saati, aşamalar arası animasyon.
9. [ ] **Siparişlerim:** geçmiş siparişler, detay görüntüleme, "Tekrar sipariş ver" (ürünleri sepete ekler).
10. [ ] **Adreslerim ve Favorilerim.**

- [ ] Dolu sepetle uygulamadan çıkarken uyarı gösterilmesin. Sepet zaten kaydediliyor.
- [ ] Pencere makul bir boyutta açılsın, yeniden boyutlandırılabilsin ve yüksek DPI ekranlarda düzgün görünsün.

**Bitti sayılır:** baştan sona sipariş fare ile rahatça verilebiliyor. README'de ekran görüntüleri ve kısa bir GIF var.

## Faz 5 — Cilalama ve paketleme · *M*

- [ ] Loglama (`~/.eklegelsin/loglar/`). Beklenmeyen hatalarda kullanıcıya anlaşılır bir mesaj gösterilsin, uygulama çökmesin.
- [ ] İlk açılışta örnek bir adres ve kısa karşılama ekranı.
- [ ] Ayarlar: tema seçimi, "tüm verileri sıfırla".
- [ ] Performans: görseller arka planda ve önbellekli yüklensin, uzun listeler takılmasın.
- [ ] `jpackage` ile macOS `.dmg` ve Windows `.msi`. GitHub Actions üzerinde üretilsin, GitHub Releases'a yüklensin.
- [ ] README (EN + TR) baştan yazılsın: ekran görüntüleri, kurulum, mimari özeti.
- [ ] Eski Swing kodu repodan kaldırılsın (`v1-swing` etiketiyle erişilebilir kalır).

**Bitti sayılır:** Java kurulu olmayan bir bilgisayarda yükleyiciyle kurulup baştan sona sipariş verilebiliyor ve veriler yeniden açılışta duruyor. Proje burada tamamlanmış sayılır.

---

## Eski hataların karşılığı

| Hata | Yeni tasarımda nasıl önleniyor | Faz |
|------|-------------------------------|-----|
| H1 Bu ay geçerli kart reddi | `YearMonth` ile ay sonuna kadar geçerlilik + test | 1 |
| H2 Pencere sızıntısı | Tek pencere, ekranlar içeride değişiyor | 4 |
| H3 Restoranlar arası karışık sepet | Sepet tek restorana bağlı | 1 |
| H4 Kart numarası kontrolü | Boşluk temizleme, Luhn, Amex desteği | 1 |
| H5 Arayüz yanlış thread'de | JavaFX `Application` yaşam döngüsü ✅ | 0 |
| H6 Çift boşluk | Ürün adı ve seçimler ayrı alanlarda gösteriliyor | 1 / 4 |
| H7 Sistem diline bağlı fiyat | `Para` sınıfı, sabit `tr-TR` biçimi | 1 |
| H8 Servis içinde dialog | Servis doğrulama sonucu döner, mesajı arayüz gösterir | 1 |

## Sıra özeti

| Sıra | Faz | Çıktı |
|------|-----|-------|
| 1 | 0 · İskelet | Derlenen, test edilen, boş pencere açan proje |
| 2 | 1 · Domain | Testli iş kuralları, eski hataların çözümü |
| 3 | 2 · Katalog | ~20 restoran, yüzlerce gerçekçi ürün ve görsel |
| 4 | 3 · Kalıcı veri | Kapatınca kaybolmayan siparişler, adresler, sepet |
| 5 | 4 · Arayüz + akış | Modern görünüm ve eksiksiz sipariş deneyimi |
| 6 | 5 · Cilalama + paket | Kurulabilir, bitmiş uygulama |

Faz 2 ve Faz 3 birbirinden bağımsızdır, paralel yürütülebilir. Faz 4, ikisine de ihtiyaç duyar.
