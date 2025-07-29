# 📖 Quản Lí Truyện - Manga Manager

Đây là một dự án ứng dụng Android cá nhân được xây dựng bằng Java, đóng vai trò là một công cụ quản lý và đọc truyện tranh (manga/comic) offline mạnh mẽ. Ứng dụng cho phép người dùng nhập các bộ truyện từ file `.cbz`, tự quản lý thư viện, tùy chỉnh trải nghiệm đọc và theo dõi tiến trình của bản thân.

## ✨ Demo & Hình ảnh

*(Bạn hãy thêm các ảnh chụp màn hình hoặc ảnh GIF demo ứng dụng của mình tại đây để README thêm phần sinh động!)*

Ví dụ:
| Màn hình chính | Danh sách chương | Màn hình đọc |
| :---: | :---: | :---: |
| *(Ảnh màn hình chính)* | *(Ảnh danh sách chương)* | *(Ảnh màn hình đọc)* |


## 🚀 Các Tính Năng Chính

###  quản lý Thư viện
- **Thêm, Sửa, Xóa Truyện:** Giao diện trực quan để quản lý thông tin cơ bản của từng bộ truyện (tên, mô tả, ảnh bìa).
- **Nhập truyện từ file `.cbz`:** Dễ dàng thêm truyện mới từ các tệp `.cbz` lưu trên thiết bị.
- **Quản lý Thể loại:** Tự tạo và gán các thể loại cho truyện để tiện phân loại và tìm kiếm.
- **Quản lý Chương:**
    - Tự động tạo hàng loạt chương theo Tên Arc/Phần và số lượng.
    - Cho phép sửa tên thủ công cho từng chương riêng lẻ để tăng tính linh hoạt.

### Trải nghiệm Đọc
- **Chế độ đọc đa dạng:** Hỗ trợ 2 chế độ đọc chính:
    - **Lật trang ngang:** Mang lại cảm giác như đọc một cuốn sách thật.
    - **Cuộn dọc:** Tối ưu cho các loại truyện dài (webtoon).
- **Tùy chỉnh chất lượng ảnh:** Cho phép chọn chất lượng ảnh (Cao, Trung bình, Thấp) để phù hợp với nhu_cầu lưu trữ và tốc độ mạng.
- **Lưu tiến trình đọc:** Ứng dụng tự động lưu lại trang cuối cùng bạn đã đọc của mỗi chương và đề xuất đọc tiếp khi bạn quay lại.

### Thống kê & Lịch sử
- **Lịch sử đọc:** Ghi lại các lần bạn đọc truyện theo đúng múi giờ Việt Nam (GMT+7).
- **Thống kê chi tiết:** Hiển thị các biểu đồ về thói quen đọc của bạn:
    - Số lần đọc theo ngày, tuần, tháng.
    - Top 5 truyện và thể loại được đọc nhiều nhất.
- **Tìm kiếm:** Tìm kiếm truyện trong thư viện nhanh chóng theo tên truyện hoặc thể loại.

## 🛠️ Công nghệ sử dụng

- **Ngôn ngữ:** **Java**
- **Nền tảng:** **Android SDK (Native)**
- **Kiến trúc:** **Layered Architecture** (Presentation - Business Logic - Data Access)
- **Cơ sở dữ liệu:** **SQLite** (quản lý thông qua lớp `DatabaseHelper` tùy chỉnh).
- **Thư viện chính:**
    - **AndroidX:** AppCompat, RecyclerView, CardView, ConstraintLayout, ViewPager2.
    - **Material Components:** Cung cấp các thành phần giao diện hiện đại như Button, TextInputLayout, Toolbar.
    - **Glide:** Để tải và hiển thị hình ảnh một cách hiệu quả.
    - **Retrofit & Gson:** Cho tính năng tìm kiếm online.
    - **Chart.js:** Render biểu đồ thống kê thông qua `WebView`.

## ⚙️ Cài đặt & Chạy thử

1.  Clone kho lưu trữ này về máy của bạn:
    ```bash
    git clone [URL-GITHUB-CUA-BAN]
    ```
2.  Mở project bằng **Android Studio**.
3.  Android Studio sẽ tự động tải các dependency cần thiết thông qua Gradle.
4.  **Lưu ý:** Dự án có sử dụng API tìm kiếm. Bạn cần cung cấp API key của riêng mình để tính năng này hoạt động. Hãy tìm đến file `SearchActivity.java` và thay thế giá trị của biến `API_KEY`.
5.  Build và chạy ứng dụng trên máy ảo hoặc thiết bị thật.

## 🎯 Hướng phát triển trong tương lai

- [ ] **Đồng bộ hóa đám mây:** Tích hợp Google Drive hoặc Firebase để đồng bộ hóa thư viện và tiến trình đọc giữa nhiều thiết bị.
- [ ] **Tự động lấy thông tin truyện (Metadata Scraping):** Tự động tìm và điền thông tin truyện (mô tả, tác giả,...) từ các trang web online.
- [ ] **Hỗ trợ thêm định dạng:** Hỗ trợ các định dạng file khác như `.cbr`, `PDF`.
- [ ] **Giao diện cho máy tính bảng:** Tối ưu hóa giao diện, hỗ trợ chế độ đọc 2 trang.
