package asterisk.sun.booking_tours.common.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import asterisk.sun.booking_tours.core.user.Role;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import asterisk.sun.booking_tours.core.user.UserStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Order(3) // Chạy trước ReviewSeeder (Order 5)
public class ClientUserSeeder implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(ClientUserSeeder.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    public ClientUserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        seedClientUsers();
    }

    private void seedClientUsers() {
        // Kiểm tra xem đã có user nào với role USER chưa (không tính admin)
        long clientCount = userRepository.findByRole(Role.USER).size();

        if (clientCount == 0) {
            logger.info("Seeding client users...");

            List<User> clientUsers = new ArrayList<>();

            // Danh sách tên Việt Nam
            String[][] vietnameseNames = {
                {"Nguyễn", "Văn", "An"},
                {"Trần", "Thị", "Bình"},
                {"Lê", "Minh", "Châu"},
                {"Phạm", "Thu", "Dung"},
                {"Hoàng", "Văn", "Em"},
                {"Huỳnh", "Thị", "Phượng"},
                {"Phan", "Thanh", "Giang"},
                {"Vũ", "Hồng", "Hạnh"},
                {"Đặng", "Quốc", "Huy"},
                {"Bùi", "Thị", "Lan"},
                {"Đỗ", "Minh", "Khoa"},
                {"Ngô", "Thu", "Linh"},
                {"Dương", "Văn", "Minh"},
                {"Lý", "Thị", "Nga"},
                {"Mai", "Xuân", "Oanh"},
                {"Trương", "Hải", "Phong"},
                {"Đinh", "Thị", "Quỳnh"},
                {"Võ", "Minh", "Tuấn"},
                {"Hồ", "Thị", "Uyên"},
                {"Tô", "Văn", "Vinh"}
            };

            // Danh sách tên quốc tế
            String[][] internationalNames = {
                {"John", "", "Smith"},
                {"Emma", "", "Johnson"},
                {"Michael", "", "Williams"},
                {"Sarah", "", "Brown"},
                {"David", "", "Jones"},
                {"Lisa", "", "Garcia"},
                {"James", "", "Miller"},
                {"Maria", "", "Davis"},
                {"Robert", "", "Rodriguez"},
                {"Jennifer", "", "Martinez"}
            };

            // Danh sách địa chỉ Việt Nam
            String[] vietnameseAddresses = {
                "123 Nguyễn Huệ, Quận 1, TP.HCM",
                "456 Lê Lợi, Quận 3, TP.HCM",
                "789 Trần Hưng Đạo, Quận 5, TP.HCM",
                "321 Võ Văn Tần, Quận 3, TP.HCM",
                "654 Hai Bà Trưng, Quận 1, TP.HCM",
                "147 Hoàng Văn Thụ, Phú Nhuận, TP.HCM",
                "258 Phan Xích Long, Phú Nhuận, TP.HCM",
                "369 Cách Mạng Tháng 8, Quận 10, TP.HCM",
                "159 Lý Thường Kiệt, Quận 11, TP.HCM",
                "753 Lạc Long Quân, Quận 11, TP.HCM",
                "15 Hoàng Diệu, Hà Nội",
                "22 Tràng Tiền, Hà Nội",
                "38 Láng Hạ, Đống Đa, Hà Nội",
                "45 Giảng Võ, Ba Đình, Hà Nội",
                "67 Nguyễn Thái Học, Ba Đình, Hà Nội",
                "89 Trần Phú, Đà Nẵng",
                "101 Lê Duẩn, Đà Nẵng",
                "202 Ngô Quyền, Hải Phòng",
                "303 Trần Hưng Đạo, Cần Thơ",
                "404 Hùng Vương, Nha Trang"
            };

            // Danh sách ảnh avatar mẫu
            String[] avatarUrls = {
                "https://i.pravatar.cc/150?img=1",
                "https://i.pravatar.cc/150?img=2",
                "https://i.pravatar.cc/150?img=3",
                "https://i.pravatar.cc/150?img=5",
                "https://i.pravatar.cc/150?img=8",
                "https://i.pravatar.cc/150?img=11",
                "https://i.pravatar.cc/150?img=12",
                "https://i.pravatar.cc/150?img=13",
                "https://i.pravatar.cc/150?img=14",
                "https://i.pravatar.cc/150?img=16",
                "https://i.pravatar.cc/150?img=17",
                "https://i.pravatar.cc/150?img=20",
                "https://i.pravatar.cc/150?img=23",
                "https://i.pravatar.cc/150?img=25",
                "https://i.pravatar.cc/150?img=27",
                null, // Một số user không có avatar
                null,
                null
            };

            int userCounter = 1;

            // Tạo 20 user Việt Nam
            for (int i = 0; i < vietnameseNames.length; i++) {
                String[] name = vietnameseNames[i];
                User user = new User();

                String username = "user" + userCounter;
                user.setUsername(username);
                user.setLastName(name[0]);
                user.setFirstName(name[1] + " " + name[2]);
                user.setEmail(username + "@example.com");
                user.setPassword(passwordEncoder.encode("123456")); // Default password
                user.setPhone(generateVietnamesePhone());
                user.setAddress(vietnameseAddresses[i % vietnameseAddresses.length]);
                user.setDateOfBirth(generateRandomDateOfBirth());
                user.setRole(Role.USER);

                // 80% ACTIVE, 15% PENDING, 5% INACTIVE
                int statusRand = random.nextInt(100);
                if (statusRand < 80) {
                    user.setStatus(UserStatus.ACTIVE);
                    user.setIsVerified(true);
                } else if (statusRand < 95) {
                    user.setStatus(UserStatus.PENDING);
                    user.setIsVerified(false);
                } else {
                    user.setStatus(UserStatus.INACTIVE);
                    user.setIsVerified(false);
                }

                // Thêm avatar ngẫu nhiên
                String avatar = avatarUrls[random.nextInt(avatarUrls.length)];
                user.setAvatarUrl(avatar);

                clientUsers.add(user);
                userCounter++;
            }

            // Tạo 10 user quốc tế
            for (int i = 0; i < internationalNames.length; i++) {
                String[] name = internationalNames[i];
                User user = new User();

                String username = "user" + userCounter;
                user.setUsername(username);
                user.setFirstName(name[0]);
                user.setLastName(name[2]);
                user.setEmail(username + "@example.com");
                user.setPassword(passwordEncoder.encode("123456")); // Default password
                user.setPhone(generateInternationalPhone());
                user.setAddress(generateInternationalAddress());
                user.setDateOfBirth(generateRandomDateOfBirth());
                user.setRole(Role.USER);

                // 80% ACTIVE, 15% PENDING, 5% INACTIVE
                int statusRand = random.nextInt(100);
                if (statusRand < 80) {
                    user.setStatus(UserStatus.ACTIVE);
                    user.setIsVerified(true);
                } else if (statusRand < 95) {
                    user.setStatus(UserStatus.PENDING);
                    user.setIsVerified(false);
                } else {
                    user.setStatus(UserStatus.INACTIVE);
                    user.setIsVerified(false);
                }

                // Thêm avatar ngẫu nhiên
                String avatar = avatarUrls[random.nextInt(avatarUrls.length)];
                user.setAvatarUrl(avatar);

                clientUsers.add(user);
                userCounter++;
            }

            userRepository.saveAll(clientUsers);
            logger.info("Seeded {} client users successfully!", clientUsers.size());
            logger.info("Default password for all users: 123456");
        } else {
            logger.info("Client users already exist (count: {}). Skipping seeding.", clientCount);
        }
    }

    private String generateVietnamesePhone() {
        // Số điện thoại Việt Nam: 10 chữ số, bắt đầu bằng 03, 05, 07, 08, 09
        String[] prefixes = {"03", "05", "07", "08", "09"};
        String prefix = prefixes[random.nextInt(prefixes.length)];

        StringBuilder phone = new StringBuilder(prefix);
        for (int i = 0; i < 8; i++) {
            phone.append(random.nextInt(10));
        }

        return phone.toString();
    }

    private String generateInternationalPhone() {
        // Số điện thoại quốc tế: 10-11 chữ số
        StringBuilder phone = new StringBuilder();
        int length = 10 + random.nextInt(2); // 10 or 11 digits

        for (int i = 0; i < length; i++) {
            phone.append(random.nextInt(10));
        }

        return phone.toString();
    }

    private String generateInternationalAddress() {
        String[] streets = {"Main Street", "Oak Avenue", "Park Road", "High Street", "Church Lane"};
        String[] cities = {"New York, USA", "London, UK", "Sydney, Australia", "Toronto, Canada", "Singapore"};

        int number = 1 + random.nextInt(999);
        String street = streets[random.nextInt(streets.length)];
        String city = cities[random.nextInt(cities.length)];

        return number + " " + street + ", " + city;
    }

    private LocalDate generateRandomDateOfBirth() {
        // Sinh ngày từ 18-65 tuổi
        int yearOffset = 18 + random.nextInt(48); // 18 to 65 years old
        int year = LocalDate.now().getYear() - yearOffset;
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28); // Để tránh lỗi ngày không hợp lệ

        return LocalDate.of(year, month, day);
    }
}
