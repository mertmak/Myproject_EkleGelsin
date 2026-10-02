
# Ekle Gelsin - Desktop Food Ordering App


`Ekle Gelsin` is a desktop food ordering simulation developed using Java Swing. It allows users to choose from different food categories (doner, pizza, burger), list restaurants, view menus, and complete the order process.

This project is the refactored version of an initial procedural code, restructured according to Object-Oriented Programming (OOP) principles. This refactoring significantly improves the code's readability, maintainability, and scalability.

> **🚧 Rewrite in progress.** Ekle Gelsin is being rebuilt from scratch with Java Swing. The sections below describe the original Swing version, which lives in the `EkleGelsin/` folder and under the `v1-swing` tag. See the [roadmap](docs/YOL_HARITASI.md) (Turkish).


## ✨ Features

- **Food Category Selection:** Choose from categories like doner, pizza, or burger from the main menu.
- **Restaurant Listing:** Dynamically list restaurants based on the selected category.
- **Menu Display:** View products and prices after selecting a restaurant.
- **Product Customization:** Add or remove extra ingredients for food items.
- **Cart Management:** Add/remove items from the cart and view the total amount in real-time.
- **Multiple Payment Options:** Pay with Cash on Delivery or Credit Card.
- **Credit Card Validation:** Basic format validation for card number (16 digits), expiration date, and CVV.
- **Order Process Simulation:** An animated process showing order statuses like "Order received," "Preparing," "On the way," and "Delivered."
- **OOP-Based Modular Architecture:** A clean and understandable code structure with responsibilities separated into `model`, `ui`, and `service` layers.


## 🛠️ Tech Stack

- **Language:** Java
- **UI Framework:** Java Swing
- **Build:** Maven (via the included Maven Wrapper)

## 📂 Project Architecture

The project is divided into logical layers based on the Single Responsibility Principle:

- `eklegelsin.main`
  - `EkleGelsin.java`: The main class that launches the application.
- `eklegelsin.model`
  - `Yemek.java`, `Dukkan.java`, `Sepet.java`: Classes representing the core data structures and objects.
- `eklegelsin.ui`
  - `EkleGelsinUI.java`, `UI.java`: Classes responsible for creating and managing the user interface (windows, buttons, etc.).
- `eklegelsin.service`
  - `VeriServisi.java`, `OdemeServisi.java`, `SiparisServisi.java`: Classes that handle the business logic, such as creating shop data, validating payments, and simulating the order process.

## 🚀 Installation and Usage

### Prerequisites

- JDK 25 or newer. Maven does not need to be installed; the included wrapper (`mvnw`) downloads it on first use.

### Steps

```bash
git clone https://github.com/mertmak/Myproject_EkleGelsin
cd Myproject_EkleGelsin

# Run the app
./mvnw compile exec:exec

# Build and run the tests
./mvnw verify
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

### Running the original Swing version

```bash
cd EkleGelsin
javac -d bin $(find src -name '*.java')
java -cp bin eklegelsin.main.EkleGelsin
```

## 👥 Contributors

- Mehmet Ali Salman
- Mert Mak
- Miraç Arda Seçkin
- Yusuf Demirci
- Yağız Demirci

Thank you for checking out the project!









