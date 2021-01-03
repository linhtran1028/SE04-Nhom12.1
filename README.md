# Tìm hiểu về Environmental understanding Arcore, một ứng dụng có sử lý detect mặt bằng hoặc detect điểm trên Android
# Teacher: Mr. Bùi Sỹ Nguyên
# Student
- Trần Thị Khánh Linh
- Vũ Thị Thu Hằng
- Nguyễn Thị Thu Hà
- Phí Thị Hồng Huế
# 1. Cài đặt và sử dụng
- Android Studio bản preview mới nhất tại: https://developer.android.com/studio/preview
- Ngôn ngữ sử dụng: Java
- Thiết bị hỗ trợ ARCore: Yêu cầu Android 8.1 (API 27) trở lên
- Danh sách các thiết bị thật hỗ trợ ARCore: https://developers.google.com/ar/discover/supported-devices
# 2. ARCore với Environmental understanding
- ARCore là một nền tảng của Google dùng để trải nghiệm thực tế ảo tăng cường. ARCore sử dụng các API khác nhau, giúp điện thoại của bạn có thể cảm nhận được môi trường xung quanh, hiểu được thế giới thực và tương tác với các thông tin trong thế giới thực
- ARCore sử dụng ba khả năng chính để tích hợp nội dung ảo với thế giới thực khi được nhìn thấy qua camera trên điện thoại: Motion tracking, Environmental understanding, Light estimation
- ARCore Environmental understanding cho phép điện thoại phát hiện kích thước và vị trí của tất cả các loại bề mặt: bề mặt ngang, dọc và góc cạnh như mặt đất, bàn cà phê hoặc tường, tìm kiếm các cụm feature points xuất hiện nằm trên các bề mặt và làm các bề mặt này xuất hiện trong ứng dụng như là các mặt phẳng và sử dụng thông tin đó để đặt các vật thể ảo trên bề mặt phẳng
- Nguồn tham khảo: 
     + Hướng dẫn cơ bản về AR: https://developers.google.com/ar/discover
     + Cách tính đo khoảng cách và ý tưởng: https://medium.com/@shibuiyusuke/measuring-distance-with-arcore-6eb15bf38a8f
# 3. Khái quát về ứng dụng 
- Đây là ứng dụng có xử lý detect mặt hoặc detect điểm trên Android
- Ứng dụng này tạo ra một không gian 3D , cho phép người dùng quét ra mặt phẳng trong không gian đó . Thực hiện lấy 2 điểm bất kỳ trên mặt phẳng đã detect được để đo khoảng cách của 2 điểm 
- Demo: https://youtu.be/90YPNMsOl_Y
