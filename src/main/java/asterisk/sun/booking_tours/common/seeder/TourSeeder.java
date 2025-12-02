package asterisk.sun.booking_tours.common.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import asterisk.sun.booking_tours.common.helper.SlugifyHelper;
import asterisk.sun.booking_tours.core.category.Category;
import asterisk.sun.booking_tours.core.category.CategoryRepository;
import asterisk.sun.booking_tours.core.tour.Tour;
import asterisk.sun.booking_tours.core.tour.TourRepository;
import asterisk.sun.booking_tours.core.user.Role;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Order(3)
public class TourSeeder implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(TourSeeder.class);
    private final TourRepository tourRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    public TourSeeder(TourRepository tourRepository, CategoryRepository categoryRepository,
            UserRepository userRepository) {
        this.tourRepository = tourRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedTours();
    }

    private void seedTours() {
        if (tourRepository.count() == 0) {
            logger.info("Seeding tours...");

            List<Category> categories = categoryRepository.findAll();
            List<User> admins = userRepository.findByRole(Role.ADMIN);

            if (categories.isEmpty()) {
                logger.warn("No categories found. Please seed categories first.");
                return;
            }

            if (admins.isEmpty()) {
                logger.warn("No admin users found. Please seed admin users first.");
                return;
            }

            User creator = admins.get(0);

            String[][] tourData = {
                    { "Du lịch Hạ Long - Vịnh Di Sản", "Khám phá vẻ đẹp kỳ vĩ của Vịnh Hạ Long với hàng ngàn hòn đảo đá vôi",
                            "Hà Nội", "Vịnh Hạ Long", "VND", "2", "1" },
                    { "Tour Đà Nẵng - Hội An", "Tận hưởng bãi biển đẹp và phố cổ Hội An lãng mạn",
                            "TP. Hồ Chí Minh", "Đà Nẵng - Hội An", "VND", "3", "2" },
                    { "Khám phá Sapa - Fansipan", "Chinh phục nóc nhà Đông Dương và thưởng ngoạn ruộng bậc thang",
                            "Hà Nội", "Sapa - Lào Cai", "VND", "3", "2" },
                    { "Tour Phú Quốc - Thiên đường biển đảo", "Nghỉ dưỡng tại đảo ngọc Phú Quốc với biển xanh cát trắng",
                            "TP. Hồ Chí Minh", "Phú Quốc", "VND", "4", "3" },
                    { "Đà Lạt - Thành phố ngàn hoa", "Khám phá thành phố sương mù với khí hậu mát mẻ quanh năm",
                            "TP. Hồ Chí Minh", "Đà Lạt", "VND", "3", "2" },
                    { "Nha Trang - Thiên đường biển", "Tận hưởng kỳ nghỉ tại một trong những vịnh đẹp nhất thế giới",
                            "TP. Hồ Chí Minh", "Nha Trang", "VND", "3", "2" },
                    { "Tour Mekong Delta - Miền Tây sông nước", "Khám phá đời sống miền Tây sông nước và chợ nổi Cái Răng",
                            "TP. Hồ Chí Minh", "Cần Thơ - Tiền Giang", "VND", "2", "1" },
                    { "Ninh Bình - Tràng An", "Khám phá danh thắng Tràng An - Di sản thế giới",
                            "Hà Nội", "Ninh Bình", "VND", "2", "1" },
                    { "Côn Đảo - Đảo thiêng liêng", "Tham quan đảo Côn Đảo với lịch sử anh hùng và biển đẹp nguyên sơ",
                            "TP. Hồ Chí Minh", "Côn Đảo", "VND", "3", "2" },
                    { "Huế - Cố đô ngàn năm", "Khám phá kinh đô xưa với Di sản văn hóa thế giới",
                            "Đà Nẵng", "Huế", "VND", "2", "1" },
                    { "Bangkok - Pattaya", "Khám phá thủ đô sôi động và thành phố biển Pattaya của Thái Lan",
                            "TP. Hồ Chí Minh", "Bangkok - Pattaya", "USD", "4", "3" },
                    { "Singapore - Vườn quốc gia trong thành phố", "Trải nghiệm sự hiện đại và sạch đẹp của đảo quốc sư tử",
                            "TP. Hồ Chí Minh", "Singapore", "USD", "4", "3" },
                    { "Bali - Đảo của các vị thần", "Khám phá văn hóa độc đáo và bãi biển tuyệt đẹp tại Bali, Indonesia",
                            "TP. Hồ Chí Minh", "Bali", "USD", "5", "4" },
                    { "Tokyo - Osaka", "Trải nghiệm văn hóa Nhật Bản từ hiện đại đến truyền thống",
                            "Hà Nội", "Tokyo - Osaka", "USD", "6", "5" },
                    { "Seoul - Jeju", "Khám phá xứ sở Kim chi và đảo Jeju kỳ ảo",
                            "Hà Nội", "Seoul - Jeju", "USD", "5", "4" },
                    { "Dubai - Abu Dhabi", "Trải nghiệm sự xa hoa và sa mạc bí ẩn tại UAE",
                            "TP. Hồ Chí Minh", "Dubai", "USD", "6", "5" },
                    { "Paris - Thành phố ánh sáng", "Khám phá thủ đô lãng mạn nhất thế giới",
                            "Hà Nội", "Paris", "EUR", "7", "6" },
                    { "Maldives - Thiên đường hạ giới", "Nghỉ dưỡng tại resort sang trọng trên những đảo san hô tuyệt đẹp",
                            "TP. Hồ Chí Minh", "Maldives", "USD", "5", "4" },
                    { "Santorini - Hy Lạp", "Tận hưởng hoàng hôn đẹp nhất thế giới tại đảo Santorini",
                            "Hà Nội", "Santorini", "EUR", "6", "5" },
                    { "Tour Tây Bắc - Điện Biên - Mộc Châu", "Khám phá vùng cao Tây Bắc với văn hóa dân tộc đặc sắc",
                            "Hà Nội", "Điện Biên - Sơn La", "VND", "4", "3" }
            };

            String[] itineraries = {
                    "Ngày 1: Khởi hành - Tham quan điểm A\nNgày 2: Tham quan điểm B - C - Về",
                    "Ngày 1: Đón khách - Check in khách sạn - Tự do khám phá\nNgày 2: Tour trong ngày\nNgày 3: Tham quan - Trở về",
                    "Ngày 1: Đón tại sân bay - Tham quan thành phố\nNgày 2: Tour trọn ngày\nNgày 3: Tự do - Trả phòng - Về",
                    "Ngày 1-2: Tham quan các điểm nổi bật\nNgày 3: Nghỉ dưỡng\nNgày 4: Về",
                    "Tour đầy đủ tiện nghi với các điểm tham quan hấp dẫn"
            };

            String termsAndConditions = "1. Giá tour đã bao gồm: Vé tham quan, khách sạn, bữa ăn theo chương trình, hướng dẫn viên, bảo hiểm du lịch.\n" +
                    "2. Không bao gồm: Chi phí cá nhân, đồ uống có cồn, vé tham quan ngoài chương trình.\n" +
                    "3. Chính sách hủy tour: Hủy trước 7 ngày hoàn 70%, hủy trước 3 ngày hoàn 50%, hủy trong vòng 24h không hoàn tiền.\n" +
                    "4. Trẻ em dưới 5 tuổi miễn phí, từ 5-10 tuổi tính 75% giá tour.\n" +
                    "5. Du khách cần mang theo CMND/Passport còn hạn.";

            List<Tour> tours = new ArrayList<>();
            for (int i = 0; i < tourData.length; i++) {
                String[] data = tourData[i];
                Category category = categories.get(random.nextInt(categories.size()));

                Tour tour = new Tour();
                String name = data[0];
                tour.setName(name);
                tour.setTitle(name);
                tour.setDescription(data[1]);
                tour.setSlug(SlugifyHelper.toSlug(name));
                tour.setDepartureLocation(data[2]);
                tour.setMainDestination(data[3]);
                tour.setCurrency(data[4]);
                tour.setDurationDays(Integer.parseInt(data[5]));
                tour.setDurationNights(Integer.parseInt(data[6]));

                // Random giá tour từ 2-20 triệu VND hoặc 200-2000 USD/EUR
                BigDecimal basePrice;
                if ("VND".equals(data[4])) {
                    basePrice = new BigDecimal((2000000 + random.nextInt(18000000)));
                } else {
                    basePrice = new BigDecimal((200 + random.nextInt(1800)));
                }

                tour.setPrice(basePrice);
                tour.setPriceAdult(basePrice);
                tour.setPriceChild(basePrice.multiply(new BigDecimal("0.75")));

                tour.setItinerary(itineraries[random.nextInt(itineraries.length)]);
                tour.setTermsAndConditions(termsAndConditions);
                tour.setThumbnailUrl("https://picsum.photos/800/600?random=" + i);
                tour.setCreator(creator);
                tour.setCategory(category);

                tours.add(tour);
            }

            tourRepository.saveAll(tours);
            logger.info("Seeded {} tours successfully!", tours.size());
        } else {
            logger.info("Tours already exist. Skipping seeding.");
        }
    }
}
