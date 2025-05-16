# 🧠 Thử Thách Trí Nhớ - Memory Challenge Game

## 📌 Mô tả dự án

Đây là một hệ thống trò chơi 2 người chơi trực tuyến mang tên **"Thử Thách Trí Nhớ"**. Người chơi sẽ đăng nhập, mời người chơi khác để tham gia vào trận đấu đối kháng theo luật chơi so tài trí nhớ.

---

## 🖥️ Kiến trúc hệ thống

- Hệ thống bao gồm một **server trung tâm** và nhiều **client**.
- Server chịu trách nhiệm:
  - Quản lý đăng nhập, danh sách người chơi trực tuyến.
  - Tạo và điều phối trận đấu.
  - Ghi nhận điểm và xếp hạng.
- Mỗi client là một máy người chơi, giao tiếp với server để gửi/nhận thông tin và hiển thị giao diện.

---

## 🔐 Chức năng chính

### 1. Đăng nhập
- Người chơi đăng nhập bằng tài khoản cá nhân.
- Sau khi đăng nhập, hiển thị:
  - Danh sách người chơi trực tuyến.
  - Tên người chơi, điểm tích lũy, trạng thái (đang chơi / online / offline).

### 2. Bắt đầu trận đấu
- Người chơi gửi lời mời chơi đến một người chơi khác đang online (không đang chơi).
- Nếu người được mời chấp nhận, trận đấu bắt đầu.

---

## 🕹️ Luật chơi: "Thử Thách Trí Nhớ"

- Ma trận 4x4, gồm 16 ô ảnh, với 8 cặp ảnh giống nhau (được xáo trộn).
- Hai người chơi cùng nhìn một ma trận giống nhau.
- Trò chơi diễn ra trong **30 giây**.
- Mỗi lượt, người chơi chọn **2 ô ảnh** để lật:
  - Nếu giống nhau → ảnh được giữ nguyên, người chơi được **+1 điểm**.
  - Nếu khác nhau → ảnh sẽ úp lại.
- Trò chơi kết thúc khi:
  - Một người chơi lật hết toàn bộ cặp ảnh → người đó thắng, đối thủ dừng chơi.
  - Hết 30 giây → người có **nhiều điểm hơn** sẽ thắng.
  - Nếu điểm bằng nhau → hòa.

---

## 💻 Giao diện người chơi

- Lưới ảnh 4x4, các ảnh ban đầu bị úp.
- Khi click chọn ảnh:
  - Ảnh sẽ hiển thị.
  - Hai ảnh giống nhau thì sẽ mở, khác thì úp lại sau giây lát.
- Bên dưới có:
  - **Điểm hiện tại** của người chơi.
  - **Thời gian còn lại** của ván chơi.

---

## 🏁 Kết thúc trận đấu

- Hệ thống hiển thị:
  - Điểm số mỗi người chơi đạt được (tổng số cặp đúng).
  - Kết quả: Thắng / Thua / Hòa.
- Điểm sẽ được cộng vào tổng điểm người chơi (trừ khi thoát trận).

---

## 🚪 Thoát trận đấu

- Người chơi có thể thoát trận bất cứ lúc nào:
  - Người thoát **mặc định thua**, không nhận được điểm.
  - Đối thủ nhận đúng số điểm đã đạt được tại thời điểm đó.

---

## 🏆 Xếp hạng

- Hệ thống duy trì **bảng xếp hạng tổng**:
  - Hiển thị thứ hạng theo tổng điểm tích lũy.
  - Người chơi có thể tra cứu hạng của mình và những người chơi khác.

---

## 🚧 Công nghệ 


- Ngôn ngữ: Java 
- Giao tiếp mạng: Socket ( TCP)
- Database: MySQL 

---

