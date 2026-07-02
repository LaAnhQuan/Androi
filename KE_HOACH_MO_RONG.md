# KẾ HOẠCH MỞ RỘNG DỰ ÁN SHOPPING APP

> Tài liệu này mô tả kế hoạch mở rộng app: thêm **3 role**, chuyển **sản phẩm sang Firestore**,
> thêm **quản lý sản phẩm**, **admin panel**, **chat**, và (giai đoạn sau) **AI chatbot**.
> Đọc và góp ý trước khi bắt đầu code.

---

## 1. Mục tiêu tổng quát

Biến app từ "app bán hàng 1 loại người dùng, sản phẩm lấy từ API ngoài" thành
"app bán hàng nhiều vai trò (role), tự quản lý sản phẩm trên Firebase của mình".

### 3 Role
| Role | Quyền hạn |
|---|---|
| **Admin** | Toàn quyền: quản lý người dùng (đổi role, khóa), quản lý toàn bộ sản phẩm, quản lý người bán |
| **Seller** (người bán) | Đăng/sửa/xóa sản phẩm **của mình**, nhắn tin với khách |
| **Customer** (khách) | Xem, mua hàng, nhắn tin với người bán |

### Bảng phân quyền
| Chức năng | Admin | Seller | Customer |
|---|:---:|:---:|:---:|
| Xem sản phẩm, mua hàng | ✅ | ✅ | ✅ |
| Đăng / sửa / xóa sản phẩm | ✅ (tất cả) | ✅ (của mình) | ❌ |
| Quản lý người dùng (đổi role, khóa) | ✅ | ❌ | ❌ |
| Nhắn tin | ✅ | ✅ (với khách) | ✅ (với người bán) |

---

## 2. Thay đổi dữ liệu (Firestore)

### 2.1. Collection `users` (đã có — bổ sung trường)
```
users/{uid}
 ├─ uid: string
 ├─ username: string
 ├─ email: string
 ├─ role: "admin" | "seller" | "customer"   ← THÊM MỚI (mặc định "customer")
 └─ createdAt: timestamp                      ← nên thêm
```

### 2.2. Collection `products` (MỚI — thay Fake Store API)
```
products/{productId}
 ├─ id: string           (= document id)
 ├─ title: string
 ├─ price: number
 ├─ description: string
 ├─ image: string        (link URL ảnh — giai đoạn đầu dùng link)
 ├─ category: string
 ├─ sellerId: string     (uid của người đăng bán)
 ├─ stock: number        (tồn kho)
 └─ createdAt: timestamp
```

### 2.3. Collection `basket` (đã có — giữ nguyên logic, chỉ đổi kiểu id)
- Product `id` đổi từ `Int` → `String` nên chỗ lưu giỏ hàng cũng theo `String`.

### 2.4. Collection `chats` + `messages` (MỚI — cho tính năng nhắn tin)
```
chats/{chatId}                 (chatId = tổ hợp 2 uid, vd "uidA_uidB")
 ├─ participants: [uidA, uidB]
 ├─ lastMessage: string
 └─ updatedAt: timestamp
 └─ messages/{messageId}       (sub-collection)
     ├─ senderId: string
     ├─ text: string
     └─ sentAt: timestamp
```

---

## 3. Thay đổi code chính

### 3.1. Model
- `User.kt`: thêm `var role: String = "customer"`.
- `Product.kt`:
  - `id: Int?` → `id: String?`  ⚠️ (ảnh hưởng basket, product detail)
  - Bỏ annotation Gson `@SerializedName` (Firestore không dùng Gson).
  - Thêm `sellerId: String?`, `stock: Int?`.

### 3.2. Repository
- `ProductRepository` / `ProductRepositoryImpl`:
  - Thay Retrofit `Call<List<Product>>` → đọc từ Firestore.
  - Thêm: `addProduct()`, `updateProduct()`, `deleteProduct()`, `getProductsBySeller(uid)`.
- `SearchRepository`: đổi nguồn tìm kiếm sang Firestore.
- `UserRepository`: thêm đọc/ghi `role`.
- **Xóa dần** `ApiService`, `ApiClient` (Retrofit) sau khi chuyển xong — hoặc giữ lại nếu muốn.

### 3.3. ViewModel (đổi cách nhận dữ liệu)
- Retrofit trả `Call` (callback enqueue); Firestore trả bất đồng bộ khác.
- Chuẩn hóa dùng `DataState` (Loading/Success/Error) + LiveData ở:
  `ProductViewModel`, `SearchViewModel`, `ProductDetailViewModel`, `BasketViewModel`.

### 3.4. Điều hướng theo Role
- Sau khi đăng nhập: đọc `role` từ Firestore → điều hướng:
  - `admin` → màn Admin
  - `seller` → màn Seller (quản lý hàng + chat)
  - `customer` → màn mua hàng (như hiện tại)

### 3.5. Màn hình mới (UI)
- **Admin:** danh sách user + đổi role; danh sách toàn bộ sản phẩm.
- **Seller:** danh sách sản phẩm của mình + form thêm/sửa/xóa; danh sách chat.
- **Chat:** danh sách hội thoại + màn chat 1-1.

---

## 4. Firestore Security Rules (siết theo role)

Ý tưởng rule (viết chi tiết ở Phase 6):
- `products`: ai cũng **đọc**; chỉ **admin** hoặc **seller sở hữu** (`sellerId == uid`) mới **ghi**.
- `users`: user đọc/sửa hồ sơ của mình; chỉ **admin** đổi `role` người khác.
- `chats/messages`: chỉ 2 người trong `participants` mới đọc/ghi.

---

## 5. Lộ trình theo giai đoạn (Phase)

| Phase | Nội dung | Kết quả kiểm tra |
|---|---|---|
| **1. Role & phân quyền** | Thêm `role` vào User; lưu khi đăng ký (mặc định customer); đọc khi đăng nhập; điều hướng theo role | Đăng nhập admin vào màn khác customer |
| **2. Sản phẩm trên Firestore** | Đổi `Product` model + `ProductRepository` sang Firestore; thêm data mẫu; sửa các ViewModel | App hiện sản phẩm lấy từ Firestore của bạn |
| **3. Quản lý sản phẩm** | Màn Seller/Admin: thêm/sửa/xóa hàng (gắn `sellerId`) | Seller đăng được 1 sản phẩm, hiện ở trang chủ |
| **4. Admin panel** | Danh sách user, đổi role, quản lý toàn bộ hàng | Admin đổi 1 user thành seller |
| **5. Chat** | Nhắn tin khách ↔ người bán (1-1, text) | 2 tài khoản nhắn qua lại được |
| **6. Firestore Rules** | Siết quyền theo role | Customer không sửa được sản phẩm |
| **7. (Sau) AI Chatbot** | Trợ lý AI tìm/gợi ý sản phẩm, thêm giỏ bằng lời | Hỏi "tìm áo dưới 500k" → AI trả kết quả |

> **Nguyên tắc:** làm xong Phase nào **build + test** Phase đó rồi mới sang Phase kế tiếp,
> để dự án luôn chạy được, không vỡ giữa chừng.

---

## 6. Quyết định đã chốt / cần chốt

**Đã chốt:**
- 3 role: Admin / Seller / Customer.
- Sản phẩm chuyển sang Firestore (bỏ Fake Store API).
- Có tính năng chat khách ↔ người bán.

**Cần chốt (điền vào đây):**
- [ ] Ảnh sản phẩm: **link URL** (đơn giản) hay **upload Firebase Storage**?  → đề xuất: link URL trước
- [ ] Ai được làm Seller: **user tự đăng ký** hay **admin cấp quyền**?  → đề xuất: admin cấp quyền
- [ ] Tài khoản admin đầu tiên tạo thế nào: **sửa tay trong Firestore Console** (đổi role 1 user thành admin) → đề xuất: cách này
- [ ] Chat: **1-1 text** đủ chưa, hay cần gửi ảnh?  → đề xuất: 1-1 text trước

---

## 7. Rủi ro / lưu ý
- Đổi `Product.id` từ Int → String **ảnh hưởng giỏ hàng** → phải test kỹ Phase 2.
- Chuyển Retrofit → Firestore làm đổi luồng bất đồng bộ ở nhiều ViewModel.
- Firestore đang ở **test mode** (mở tự do) → phải làm Phase 6 trước khi dùng thật.
- Nên **commit git sau mỗi Phase** để dễ quay lui nếu lỗi.
