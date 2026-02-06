# Ganesh Seat App

Ganesh Seat is an Android application developed for efficient car seat inventory and management. The app allows managing car brands, their models, and model-wise seat data with advanced filtering and bulk operations.

---

## 🚀 Features

### 🔹 Brand Management

* Add, edit, and delete car brands
* Each brand includes:

  * Brand name
  * Brand image
* Display list of all available brands

### 🔹 Model Management (Brand-wise)

* Add, edit, and delete models based on the selected brand
* Each model includes:

  * Model name
  * Model image
* Display models according to the selected brand

### 🔹 Seat Management (Model-wise)

* Display seats data in a **GridView** based on the selected model
* **Quality-based filtering** using TabLayout
* Additional availability filters:

  * Available seats
  * Out of Stock seats

### 🔹 Bulk Seat Operations

* Select multiple seats at once
* Perform bulk actions:

  * Share selected seat images
  * Delete selected seat data

### 🔹 Seat Detail Screen

* Zoom-in feature for detailed seat image viewing
* Manufacturer list displayed for each seat
* Individual seat actions:

  * Share seat details
  * Delete seat data

---

## 🛠 Tech Stack

* **Programming Language:** Kotlin
* **Architecture Pattern:** Clean Architecture with MVVM
* **Dependency Injection:** Dagger-Hilt
* **Networking:** Retrofit
* **Asynchronous Handling:** Kotlin Coroutines
* **State Management:** LiveData & ViewModel
* **Lifecycle Management:** Android Lifecycle-aware components

---

## 🧱 Architecture Overview

The project follows Clean Architecture principles for better scalability, testability, and maintainability:

### Data Layer

* API services using Retrofit
* DTOs and data models
* Repository implementations

### Domain Layer

* UseCases containing business logic
* Domain models

### Presentation Layer

* ViewModels
* UI components (Activities / Fragments)
* LiveData observers

---

## 📱 Screens

* Brand List Screen
* Model List Screen (Brand-wise)
* Seats Grid Screen (Model-wise)
* Seat Detail Screen (Image Zoom + Manufacturer Info)

---

## 📦 Installation & Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/ganesh-seat.git](https://github.com/rjmngr929/Ganesh-Seats.git
   ```
2. Open the project in Android Studio
3. Allow Gradle to sync completely
4. Run the application on an emulator or physical device

---

## 🤝 Contribution

Contributions are welcome.

* Raise issues for bugs or feature requests
* Submit pull requests for improvements

---

## 📄 License

This project is developed for educational and internal usage purposes.

---

## 🙏 Acknowledgements

Ganesh Seat is designed using modern Android development best practices, ensuring clean code, scalability, and ease of future enhancements.

---

**Happy Coding! 🚀**

Developed By Rahul Prajapat
