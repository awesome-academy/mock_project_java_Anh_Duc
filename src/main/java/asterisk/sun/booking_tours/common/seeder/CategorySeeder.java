package asterisk.sun.booking_tours.common.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import asterisk.sun.booking_tours.common.helper.SlugifyHelper;
import asterisk.sun.booking_tours.core.category.Category;
import asterisk.sun.booking_tours.core.category.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CategorySeeder implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(CategorySeeder.class);
    private final CategoryRepository categoryRepository;

    public CategorySeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedCategories();
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            logger.info("Seeding categories...");

            String[][] data = {
                    { "Adventure Tours", "Trải nghiệm những chuyến đi đầy mạo hiểm và thử thách." },
                    { "Beach Holidays", "Thư giãn trên những bãi biển cát trắng tuyệt đẹp." },
                    { "Cultural Heritage", "Khám phá lịch sử và văn hóa địa phương." },
                    { "Ecotourism", "Du lịch xanh, hòa mình vào thiên nhiên hoang dã." },
                    { "Family Packages", "Các tour thiết kế riêng cho gia đình và trẻ em." },
                    { "Luxury Escapes", "Tận hưởng dịch vụ đẳng cấp 5 sao và sự riêng tư." },
                    { "Honeymoon Specials", "Kỳ nghỉ lãng mạn dành cho các cặp đôi mới cưới." },
                    { "Solo Travel", "Hành trình tự do khám phá dành cho người độc hành." },
                    { "Culinary Tours", "Thưởng thức ẩm thực đặc sắc tại các vùng miền." },
                    { "Wildlife Safaris", "Quan sát động vật hoang dã trong môi trường tự nhiên." },
                    { "Cruise Vacations", "Hải trình sang trọng trên những con tàu lớn." },
                    { "City Breaks", "Khám phá nhịp sống sôi động của các đô thị lớn." },
                    { "Mountain Trekking", "Leo núi và chinh phục những đỉnh cao." },
                    { "Photography Tours", "Tour chuyên biệt dành cho nhiếp ảnh gia săn ảnh đẹp." },
                    { "Spiritual Journeys", "Du lịch tâm linh, thiền định và tìm sự bình an." },
                    { "Backpacking Trips", "Du lịch bụi với chi phí tiết kiệm và trải nghiệm chân thực." },
                    { "Wellness & Spa", "Nghỉ dưỡng kết hợp chăm sóc sức khỏe và làm đẹp." },
                    { "Historical Tours", "Tham quan các di tích và bảo tàng lịch sử." },
                    { "Island Hopping", "Khám phá vẻ đẹp của các quần đảo hoang sơ." },
                    { "Road Trips", "Những chuyến đi phượt bằng xe máy hoặc ô tô." },
                    { "Camping Adventures", "Cắm trại qua đêm và sinh tồn giữa thiên nhiên." },
                    { "Scuba Diving", "Lặn biển ngắm san hô và sinh vật đại dương." },
                    { "Skiing & Snowboarding", "Tour trượt tuyết tại các khu nghỉ dưỡng mùa đông." },
                    { "Festival Tours", "Tham gia các lễ hội văn hóa đặc sắc trên thế giới." },
                    { "Educational Trips", "Vừa du lịch vừa học hỏi kiến thức thực tế." },
                    { "Corporate Retreats", "Du lịch kết hợp team building cho doanh nghiệp." },
                    { "Gap Year Travel", "Kỳ nghỉ dài hạn để trải nghiệm cuộc sống." },
                    { "Senior Travel", "Tour nghỉ dưỡng nhẹ nhàng dành cho người cao tuổi." },
                    { "Accessible Tourism", "Du lịch thuận tiện cho người khuyết tật." },
                    { "Sustainable Travel", "Du lịch bền vững, bảo vệ môi trường." },
                    { "Rural Tourism", "Trải nghiệm cuộc sống nông thôn yên bình." },
                    { "Mystery Tours", "Những chuyến đi bí ẩn đến địa điểm không báo trước." },
                    { "Train Journeys", "Ngắm cảnh qua ô cửa sổ trên những chuyến tàu hỏa." },
                    { "Yacht Charters", "Thuê du thuyền riêng tư sang trọng." },
                    { "Jungle Expeditions", "Thám hiểm rừng rậm nhiệt đới." },
                    { "Desert Safaris", "Trải nghiệm cưỡi lạc đà và trượt cát sa mạc." },
                    { "Nightlife Tours", "Khám phá cuộc sống về đêm và các quán bar." },
                    { "Shopping Sprees", "Tour mua sắm tại các trung tâm thương mại lớn." },
                    { "Volunteer Tourism", "Du lịch kết hợp làm tình nguyện viên." },
                    { "Weekend Getaways", "Những chuyến đi ngắn ngày cuối tuần." },
                    { "Winter Sun", "Trốn lạnh tìm đến những vùng đất nắng ấm." },
                    { "Summer Camps", "Trại hè kỹ năng dành cho thanh thiếu niên." },
                    { "Private Jet Tours", "Di chuyển bằng chuyên cơ riêng đẳng cấp." },
                    { "Golf Holidays", "Kỳ nghỉ kết hợp chơi golf tại sân tiêu chuẩn." },
                    { "Wine Tasting", "Tham quan vườn nho và thử rượu vang." },
                    { "Bird Watching", "Tour quan sát các loài chim quý hiếm." },
                    { "Extreme Sports", "Nhảy dù, bungee và các môn thể thao mạo hiểm." },
                    { "Glamping", "Cắm trại sang trọng với đầy đủ tiện nghi." },
                    { "River Cruises", "Du ngoạn trên sông nước êm đềm." },
                    { "Art & Architecture", "Tìm hiểu về kiến trúc và nghệ thuật đương đại." }
            };

            List<Category> categories = new ArrayList<>();
            for (String[] item : data) {
                Category category = new Category();
                String name = item[0];
                String desc = item[1];

                category.setName(name);
                category.setDescription(desc);
                category.setSlug(SlugifyHelper.toSlug(name));

                categories.add(category);
            }

            categoryRepository.saveAll(categories);

            logger.info("Categories seeded successfully!");
        }
    }
}
