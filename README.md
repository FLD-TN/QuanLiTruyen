# 📖 Quản Lí Truyện - Manga Manager

This is a personal Android application project built with Java, designed as a powerful offline manga/comic reader and library management tool. The application allows users to import comics from `.cbz` files, manage their own library, customize the reading experience, and track their personal progress.

## ✨ Demo & Screenshots

For example:
| Main Screen | Chapter List | Reader View |
| :---: | :---: | :---: |
| <img width="441" height="305" alt="image" src="https://github.com/user-attachments/assets/5d28a352-a301-412c-8550-337af20e2b7a" />| 
 | 
 |


## 🚀 Key Features

### Library Management
- **Add, Edit, & Delete Comics:** An intuitive interface to manage the basic information of each comic series (title, description, cover image).
- **Import from `.cbz` Files:** Easily add new comics from `.cbz` files stored on the device.
- **Genre Management:** Create and assign custom genres to comics for better sorting and searching.
- **Chapter Management:**
    - Bulk chapter creation based on an Arc/Season name and chapter count.
    - Allows manual editing of individual chapter names for greater flexibility.

### Reading Experience
- **Multiple Reading Modes:** Supports two main reading modes:
    - **Horizontal Flip:** Provides a real-book feel.
    - **Vertical Scroll:** Optimized for long-strip content like webtoons.
- **Image Quality Customization:** Allows selecting image quality (High, Medium, Low) to fit storage needs or save mobile data.
- **Progress Saving:** The app automatically saves the last page you read in each chapter and prompts you to continue from where you left off.

### Statistics & History
- **Reading History:** Logs your reading sessions with timestamps converted to your local timezone.
- **Detailed Statistics:** Displays charts of your reading habits:
    - Reading counts by day, week, and month.
    - Top 5 most-read comics and genres.
- **Search:** Quickly find comics in your library by title or genre.

## 🛠️ Tech Stack

- **Language:** **Java**
- **Platform:** **Android SDK (Native)**
- **Architecture:** **Layered Architecture** (Presentation - Business Logic - Data Access)
- **Database:** **SQLite** (managed via a custom `DatabaseHelper` class).
- **Key Libraries:**
    - **AndroidX:** AppCompat, RecyclerView, CardView, ConstraintLayout, ViewPager2.
    - **Material Components:** Provides modern UI components like Buttons, TextInputLayouts, and Toolbars.
    - **Glide:** For efficient image loading and manipulation.
    - **Retrofit & Gson:** For the online search feature.
    - **Chart.js:** Renders statistical charts via a `WebView`.

## ⚙️ Setup & Installation

1.  Clone this repository to your local machine:
    ```bash
    git clone [https://github.com/FLD-TN/QuanLiTruyen.git]
    ```
2.  Open the project in **Android Studio**.
3.  Android Studio will automatically download the required dependencies via Gradle.
4.  **Note:** The project uses a search API. You need to provide your own API key for this feature to work. Navigate to the `SearchActivity.java` file and replace the value of the `API_KEY` variable.
5.  Build and run the application on an emulator or a physical device.

## 🎯 Future Roadmap

- [ ] **Cloud Sync:** Integrate Google Drive or Firebase to sync the library and reading progress across multiple devices.
- [ ] **Automatic Metadata Scraping:** Automatically fetch and fill comic information (description, author, etc.) from online sources.
- [ ] **Support for More Formats:** Add support for other file formats like `.cbr`, `PDF`.
- [ ] **Tablet UI:** Optimize the layout for tablets, including a two-page spread reading mode.

## 📝 License

This project is licensed under the MIT License. See the `LICENSE` file for more details.

## 👤 Author

- **FLD-TN** - *https://github.com/FLD-TN*
