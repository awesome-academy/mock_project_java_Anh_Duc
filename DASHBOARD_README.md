# Dashboard Module với WebSocket và ReactJS

Module dashboard đã được xây dựng thành công với các tính năng real-time sử dụng WebSocket.

## 📁 Cấu trúc Files

### Backend (Java Spring Boot)

1. **WebSocket Configuration**
   - `src/main/java/asterisk/sun/booking_tours/config/WebSocketConfig.java`
   - Cấu hình WebSocket với STOMP protocol
   - Endpoint: `/ws`
   - Topic prefix: `/topic`

2. **Dashboard Module**
   - `src/main/java/asterisk/sun/booking_tours/application/rest/dashboard/DashboardController.java`
     - REST API endpoint: `GET /api/dashboard/users/count`
     - Scheduled task push updates mỗi 5 giây
   
   - `src/main/java/asterisk/sun/booking_tours/application/rest/dashboard/DashboardService.java`
     - Service xử lý logic count users
   
   - `src/main/java/asterisk/sun/booking_tours/application/rest/dashboard/dto/UserCountDTO.java`
     - DTO chứa thông tin: totalUsers, activeUsers, inactiveUsers

3. **Repository Update**
   - `src/main/java/asterisk/sun/booking_tours/core/user/UserRepository.java`
   - Thêm method: `Long countByStatus(UserStatus status)`

4. **Security Update**
   - `src/main/java/asterisk/sun/booking_tours/config/SecurityConfig.java`
   - Cho phép public access cho WebSocket endpoints `/ws/**`

### Frontend (ReactJS)

- `Dashboard.jsx` - React component example
  - Kết nối WebSocket sử dụng SockJS và STOMP
  - Subscribe topic: `/topic/dashboard/users`
  - Hiển thị real-time statistics

## 🚀 Cách sử dụng

### Backend

1. **Cài đặt dependencies**
   ```bash
   mvn clean install
   ```

2. **Chạy application**
   ```bash
   mvn spring-boot:run
   ```

3. **API Endpoints**
   - REST API: `GET http://localhost:8080/api/dashboard/users/count`
   - WebSocket: `ws://localhost:8080/ws`
   - Topic subscribe: `/topic/dashboard/users`

### Frontend (ReactJS)

1. **Cài đặt dependencies**
   ```bash
   npm install sockjs-client @stomp/stompjs
   # hoặc
   yarn add sockjs-client @stomp/stompjs
   ```

2. **Import component**
   ```jsx
   import Dashboard from './Dashboard';
   
   function App() {
     return <Dashboard />;
   }
   ```

## 📊 Tính năng

### REST API
- **Endpoint**: `GET /api/dashboard/users/count`
- **Response**:
  ```json
  {
    "totalUsers": 100,
    "activeUsers": 85,
    "inactiveUsers": 15
  }
  ```

### WebSocket Real-time Updates
- Tự động push updates mỗi 5 giây
- Client subscribe vào topic `/topic/dashboard/users`
- Nhận real-time data về số lượng users

### Statistics
- **Total Users**: Tổng số users trong hệ thống
- **Active Users**: Số users có status ACTIVE
- **Inactive Users**: Số users có status INACTIVE

## 🔧 Cấu hình

### WebSocket Configuration
```java
// Endpoint connection
registry.addEndpoint("/ws")
    .setAllowedOriginPatterns("*")
    .withSockJS();

// Message broker
config.enableSimpleBroker("/topic");
config.setApplicationDestinationPrefixes("/app");
```

### Scheduled Task
```java
@Scheduled(fixedRate = 5000) // Mỗi 5 giây
public void pushUserCountUpdate() {
    // Push updates tới tất cả connected clients
}
```

## 🔒 Security

- WebSocket endpoint `/ws/**` được cho phép public access
- API endpoint `/api/dashboard/**` yêu cầu authentication
- CSRF disabled cho `/ws/**` và `/api/**`

## 📝 Notes

- WebSocket tự động reconnect nếu mất kết nối
- Sử dụng SockJS để fallback cho browsers không support WebSocket
- Real-time updates không cần phải call API liên tục
- Component tự động cleanup khi unmount

## 🧪 Testing

### Test WebSocket connection
1. Mở browser console
2. Connect tới `http://localhost:8080/ws`
3. Subscribe topic `/topic/dashboard/users`
4. Sẽ nhận được updates mỗi 5 giây

### Test REST API
```bash
curl http://localhost:8080/api/dashboard/users/count
```

## 📚 Dependencies được thêm

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```
