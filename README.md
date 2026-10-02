
# Ekle Gelsin - Desktop Food Ordering App


`Ekle Gelsin` is a desktop food ordering simulation developed using Java Swing. It allows users to choose from different food categories (doner, pizza, burger), list restaurants, view menus, and complete the order process.

This project is the refactored version of an initial procedural code, restructured according to Object-Oriented Programming (OOP) principles. This refactoring significantly improves the code's readability, maintainability, and scalability.

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
- **IDE:** IntelliJ IDEA / Eclipse / VS Code

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

To run this project on your local machine, follow the steps below.

### Prerequisites

- Java Development Kit (JDK) 11 or newer.

### Steps

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/mertmak/Myproject_EkleGelsin
    ```

2.  **Navigate to the Directory:**
    ```bash
    cd Myproject_EkleGelsin/EkleGelsin
    ```

3.  **Compile & Run (Via Terminal):**
    *Assuming all your `.java` files are under a `src` folder:*
    ```bash
    # Create a 'bin' directory for the compiled class files
    mkdir bin

    # Compile all java files into the 'bin' directory
    javac -d bin $(find src -name '*.java')

    # Run the main class
    java -cp bin eklegelsin.main.EkleGelsin
    ```

4.  **Run (Via IDE):**
    - Open the project in your favorite IDE (IntelliJ, Eclipse, etc.).
    - Locate and run the `EkleGelsin.java` file in the `eklegelsin.main` package.

## 👥 Contributors

- Mehmet Ali Salman
- Mert Mak
- Miraç Arda Seçkin
- Yusuf Demirci
- Yağız Demirci

Thank you for checking out the project!









