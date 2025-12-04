package asterisk.sun.booking_tours.common.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import asterisk.sun.booking_tours.core.review.Review;
import asterisk.sun.booking_tours.core.review.ReviewRepository;
import asterisk.sun.booking_tours.core.review.ReviewStatus;
import asterisk.sun.booking_tours.core.review.ReviewableType;
import asterisk.sun.booking_tours.core.tour.Tour;
import asterisk.sun.booking_tours.core.tour.TourRepository;
import asterisk.sun.booking_tours.core.user.Role;
import asterisk.sun.booking_tours.core.user.User;
import asterisk.sun.booking_tours.core.user.UserRepository;
import asterisk.sun.booking_tours.core.user.UserStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Order(6)
public class ReviewSeeder implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(ReviewSeeder.class);
    private final ReviewRepository reviewRepository;
    private final TourRepository tourRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    public ReviewSeeder(ReviewRepository reviewRepository, TourRepository tourRepository,
            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.tourRepository = tourRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedReviews();
    }

    private void seedReviews() {
        if (reviewRepository.count() == 0) {
            logger.info("Seeding reviews...");

            List<Tour> tours = tourRepository.findAll();
            List<User> allUsers = userRepository.findAll();

            if (tours.isEmpty()) {
                logger.warn("No tours found. Please seed tours first.");
                return;
            }

            if (allUsers.isEmpty()) {
                logger.warn("No users found. Please seed users first.");
                return;
            }

            // Lọc chỉ lấy users có role USER và status ACTIVE để review
            List<User> activeClientUsers = allUsers.stream()
                    .filter(u -> u.getRole() == Role.USER && u.getStatus() == UserStatus.ACTIVE)
                    .collect(Collectors.toList());

            if (activeClientUsers.isEmpty()) {
                logger.warn("No active client users found. Please seed client users first.");
                return;
            }

            // Các mẫu review với rating khác nhau - Đa dạng hơn
            String[][] reviewData = {
                    // 5 sao reviews - Rất hài lòng (30 mẫu)
                    { "5", "Tour tuyệt vời! Hướng dẫn viên rất nhiệt tình và chuyên nghiệp. Các điểm tham quan đều rất đẹp và ấn tượng. Khách sạn sạch sẽ, đồ ăn ngon. Tôi rất hài lòng với chuyến đi này!" },
                    { "5", "Chuyến đi tuyệt vời nhất từ trước đến nay! Mọi thứ đều được sắp xếp chu đáo. Cảm ơn đội ngũ tổ chức tour đã mang đến cho gia đình tôi những kỷ niệm đáng nhớ." },
                    { "5", "Rất đáng giá! Giá cả hợp lý, dịch vụ tốt, lịch trình hợp lý. Đặc biệt ấn tượng với thái độ phục vụ của hướng dẫn viên. Sẽ giới thiệu cho bạn bè!" },
                    { "5", "Tour rất chuyên nghiệp! Từ việc đón tiếp, hướng dẫn, đến các dịch vụ đều xuất sắc. Những điểm tham quan đều như mô tả. Chắc chắn sẽ quay lại!" },
                    { "5", "Tuyệt vời không gì để chê! Cảnh đẹp, người đẹp, thức ăn ngon. Hướng dẫn viên vui vẻ, nhiệt tình. Lần sau sẽ đặt tour dài hơn." },
                    { "5", "Chuyến du lịch gia đình rất vui vẻ! Con trẻ rất thích. Cảm ơn đội ngũ đã tổ chức tốt!" },
                    { "5", "Tôi đã có một trải nghiệm tuyệt vời! Mọi thứ đều hoàn hảo từ đầu đến cuối. Thank you!" },
                    { "5", "Đây là lần thứ 2 tôi đặt tour ở đây và vẫn rất hài lòng! Chất lượng ổn định, đáng tin cậy." },
                    { "5", "Perfect! Everything was well organized. Beautiful places, delicious food, friendly guide!" },
                    { "5", "Highly recommended! One of the best tours I've ever joined. Will definitely come back!" },
                    { "5", "Chuyến đi hoàn hảo cho kỳ nghỉ honeymoon! Lãng mạn, đẹp, đáng nhớ. Cảm ơn team rất nhiều!" },
                    { "5", "Amazing trip! The guide was knowledgeable and friendly. Accommodation was excellent!" },
                    { "5", "Best vacation ever! My family loved every moment. Thank you for the wonderful memories!" },
                    { "5", "Xuất sắc! Từ khâu tư vấn đến tổ chức đều rất chuyên nghiệp. Sẽ ủng hộ dài dài!" },
                    { "5", "Không còn gì để nói! Hoàn hảo 100%. Đây chính là kỳ nghỉ mà tôi đã mơ ước!" },
                    { "5", "Cảnh quan tuyệt đẹp, dịch vụ 5 sao! Rất đáng tiền. Chắc chắn sẽ quay lại trong tương lai gần." },
                    { "5", "Tour này vượt quá mong đợi của tôi! Mọi chi tiết đều được chăm chút kỹ lưỡng. Tuyệt vời!" },
                    { "5", "Đội ngũ hướng dẫn viên rất chuyên nghiệp, nhiệt tình. Lịch trình hợp lý, không quá gò bó. Hoàn hảo!" },
                    { "5", "Gia đình tôi đã có những ngày nghỉ tuyệt vời! Các em nhỏ rất thích. Cảm ơn rất nhiều!" },
                    { "5", "Excellent service from start to finish! The tour guide was amazing and very helpful throughout the trip." },
                    { "5", "Chuyến đi đáng nhớ với những trải nghiệm tuyệt vời! Cảm ơn đội ngũ đã làm cho chuyến đi này trở nên đặc biệt." },
                    { "5", "Outstanding tour! Every aspect was perfect. The hotels were luxurious and the food was delicious." },
                    { "5", "Một trải nghiệm khó quên! Tour guide rất am hiểu và thân thiện. Sẽ giới thiệu cho nhiều người!" },
                    { "5", "Fantastic experience! The itinerary was well-planned and we got to see all the highlights. Highly recommend!" },
                    { "5", "Chất lượng dịch vụ tuyệt hảo! Mọi thứ đều được sắp xếp hoàn hảo. Rất đáng để trải nghiệm!" },
                    { "5", "This was the best tour I've ever been on! Professional, organized, and so much fun!" },
                    { "5", "Tour đáng giá từng đồng! Cảnh đẹp, đồ ăn ngon, dịch vụ chu đáo. Sẽ đặt tour tiếp!" },
                    { "5", "Absolutely wonderful! The guide was excellent and the destinations were breathtaking." },
                    { "5", "Tuyệt vời từ A đến Z! Không có gì phải chê. Đội ngũ rất chuyên nghiệp và tận tâm!" },
                    { "5", "Perfect holiday! Everything exceeded our expectations. We'll be back for sure!" },

                    // 4 sao reviews - Hài lòng (25 mẫu)
                    { "4", "Tour tốt, tuy nhiên thời gian di chuyển hơi dài. Nhưng nhìn chung mọi thứ đều ổn, hướng dẫn viên tận tâm và các điểm tham quan rất đẹp." },
                    { "4", "Chuyến đi khá ok, chỉ có điều thức ăn hơi ít. Các dịch vụ khác đều tốt, khách sạn đẹp, hướng dẫn viên nhiệt tình." },
                    { "4", "Tổng thể tốt, tuy nhiên có một vài điểm chưa được như mong đợi. Nhưng nhìn chung vẫn đáng để trải nghiệm." },
                    { "4", "Tour hay, giá hợp lý. Nếu cải thiện thêm về bữa ăn và thời gian nghỉ ngơi thì sẽ hoàn hảo hơn." },
                    { "4", "Khá hài lòng với chuyến đi. Cảnh đẹp, lịch trình hợp lý. Chỉ có điều khách sạn hơi xa trung tâm một chút." },
                    { "4", "Cảnh đẹp, không khí trong lành. Đáng để trải nghiệm một lần. Nên đi vào mùa này." },
                    { "4", "Khá ổn, phù hợp cho những ai muốn nghỉ ngơi thư giãn. Dịch vụ tốt, giá cả hợp lý." },
                    { "4", "Điểm cộng là hướng dẫn viên rất vui tính và am hiểu. Điểm trừ là thời gian hơi gấp. Overall: Good!" },
                    { "4", "Good tour with reasonable price. Hotel was nice and clean. Transportation was comfortable." },
                    { "4", "Enjoyed the trip overall. The scenery was breathtaking. Just wish we had more free time." },
                    { "4", "Tour tốt, phù hợp cho người lớn tuổi. Lịch trình không quá gấp, có thời gian nghỉ ngơi." },
                    { "4", "Nice experience overall. Good organization and friendly staff. Would recommend to friends." },
                    { "4", "Tour hay, cảnh đẹp. Nếu có dịp sẽ đi lại. Cảm ơn hướng dẫn viên đã chăm sóc tốt!" },
                    { "4", "Pretty good tour! Only minor complaint is the early morning wake-up calls. Otherwise great!" },
                    { "4", "Chuyến đi thú vị! Chỉ tiếc là thời tiết không được như kỳ vọng. Nhưng đội ngũ đã cố gắng làm cho mọi thứ tốt nhất có thể." },
                    { "4", "Great value for money! The tour covered all the major attractions. Just a bit rushed at times." },
                    { "4", "Hướng dẫn viên rất nhiệt tình và vui vẻ. Khách sạn ổn. Chỉ có đồ ăn chưa đa dạng lắm." },
                    { "4", "Good experience! The tour was well-organized. Would be perfect with a few minor improvements." },
                    { "4", "Tour khá hay, phù hợp cho gia đình có trẻ nhỏ. Lịch trình linh hoạt, không quá gò bó." },
                    { "4", "Enjoyable trip with beautiful scenery. Guide was helpful though sometimes hard to understand." },
                    { "4", "Chuyến đi đáng nhớ! Cảnh đẹp, người thân thiện. Chỉ cần cải thiện thêm về giờ giấc là hoàn hảo." },
                    { "4", "Solid tour! Met expectations. The accommodations were comfortable and locations were scenic." },
                    { "4", "Tốt! Hướng dẫn viên có kinh nghiệm. Khách sạn sạch sẽ. Chỉ có thời gian tham quan hơi ngắn." },
                    { "4", "Very good tour overall. A few hiccups here and there but nothing major. Would go again." },
                    { "4", "Tour ổn, giá cả phải chăng. Dịch vụ tốt, chỉ cần cải thiện thêm về phương tiện di chuyển." },

                    // 3 sao reviews - Trung bình (20 mẫu)
                    { "3", "Tour bình thường, không có gì đặc biệt. Giá cả tạm ổn nhưng dịch vụ chưa thực sự tốt. Cần cải thiện thêm về chất lượng bữa ăn." },
                    { "3", "Tạm được, nhưng có nhiều điểm cần cải thiện. Hướng dẫn viên thiếu kinh nghiệm, lịch trình hơi gấp gáp." },
                    { "3", "Giá hơi cao so với chất lượng dịch vụ. Một số điểm tham quan không như mô tả. Cần cải thiện hơn nữa." },
                    { "3", "Trung bình, không tệ nhưng cũng không xuất sắc. Kỳ vọng cao hơn ở mức giá này." },
                    { "3", "Tour bình thường, phù hợp với giá tiền. Không có gì quá ấn tượng nhưng cũng không tệ." },
                    { "3", "Average experience. Some improvements needed especially in food quality and schedule." },
                    { "3", "Okay tour but expected more for the price. Some activities were cancelled due to weather." },
                    { "3", "Tạm ổn. Một số dịch vụ chưa đạt như quảng cáo. Hy vọng sẽ cải thiện trong tương lai." },
                    { "3", "Decent tour but nothing special. Met basic expectations but could be much better." },
                    { "3", "Tour được, nhưng cần nâng cao chất lượng dịch vụ. Hướng dẫn viên chưa thực sự nhiệt tình." },
                    { "3", "It was okay. Had both good and bad moments. Average overall experience." },
                    { "3", "Chuyến đi tạm ổn. Có những điểm tốt nhưng cũng có những điểm cần cải thiện." },
                    { "3", "Average tour. Nothing really stood out. Service was mediocre at best." },
                    { "3", "Bình thường. Không như kỳ vọng ban đầu. Cần cải thiện nhiều về tổ chức và dịch vụ." },
                    { "3", "The tour was alright. Some parts were good, others not so much. Mixed feelings overall." },
                    { "3", "Tạm được. Giá có vẻ hơi cao so với chất lượng dịch vụ thực tế nhận được." },
                    { "3", "Moderate experience. Tour could use some improvements in several areas." },
                    { "3", "Không tệ nhưng cũng không hay. Nhiều thứ cần cải thiện để đáng đồng tiền bát gạo." },
                    { "3", "Passable tour. Met minimum expectations but nothing more. Could be better organized." },
                    { "3", "Chuyến đi bình thường. Một số điểm hay nhưng nhìn chung chưa thực sự ấn tượng." },

                    // 2 sao reviews - Không hài lòng (10 mẫu)
                    { "2", "Khá thất vọng với tour này. Lịch trình không đúng như quảng cáo, thức ăn kém chất lượng. Hướng dẫn viên thiếu nhiệt tình." },
                    { "2", "Không hài lòng lắm. Xe đưa đón xuống cấp, khách sạn cũ kỹ. Giá cả không xứng đáng với chất lượng dịch vụ." },
                    { "2", "Tour không như kỳ vọng. Nhiều điểm tham quan bị hủy, thời gian chờ đợi quá lâu. Rất thất vọng!" },
                    { "2", "Disappointing experience. Many things went wrong. Would not recommend to others." },
                    { "2", "Chất lượng dịch vụ kém. Nhiều điều không như lời hứa. Rất thất vọng với chuyến đi này." },
                    { "2", "Poor organization. The guide seemed unprepared. Hotels were substandard for the price." },
                    { "2", "Không đáng tiền. Nhiều vấn đề phát sinh mà không được giải quyết tốt. Thất vọng!" },
                    { "2", "Below expectations. Several issues with timing and quality of services provided." },
                    { "2", "Tour tệ. Lịch trình lộn xộn, dịch vụ kém. Không recommend cho ai cả!" },
                    { "2", "Not worth the money. Many promised activities were cancelled or changed last minute." },

                    // 1 sao reviews - Rất không hài lòng (5 mẫu)
                    { "1", "Tệ! Hoàn toàn không như quảng cáo. Lãng phí tiền bạc và thời gian. Sẽ không bao giờ đặt tour ở đây nữa." },
                    { "1", "Rất tệ! Từ dịch vụ đến thái độ của nhân viên đều không chuyên nghiệp. Khách sạn bẩn, đồ ăn kém. Không recommend!" },
                    { "1", "Terrible experience! Everything was wrong from start to finish. Complete waste of money!" },
                    { "1", "Thảm họa! Không có gì đúng như lời quảng cáo. Dịch vụ tệ hại, thái độ kém. Tránh xa!" },
                    { "1", "Worst tour ever! Unprofessional staff, dirty accommodation, terrible food. Never again!" }
            };

            List<Review> reviews = new ArrayList<>();

            // Tạo review cho mỗi tour với số lượng và chất lượng đa dạng
            for (Tour tour : tours) {
                // Mỗi tour có từ 5-15 reviews
                int numReviews = 5 + random.nextInt(11);

                for (int i = 0; i < numReviews && activeClientUsers.size() > 0; i++) {
                    // Chọn ngẫu nhiên một review data với phân bố thiên về đánh giá tích cực
                    // 50% rating 5, 30% rating 4, 15% rating 3, 4% rating 2, 1% rating 1
                    String[] data;
                    int ratingDistribution = random.nextInt(100);

                    if (ratingDistribution < 50) {
                        // Rating 5 - chọn từ 30 mẫu đầu tiên
                        data = reviewData[random.nextInt(30)];
                    } else if (ratingDistribution < 80) {
                        // Rating 4 - chọn từ 25 mẫu tiếp theo
                        data = reviewData[30 + random.nextInt(25)];
                    } else if (ratingDistribution < 95) {
                        // Rating 3 - chọn từ 20 mẫu tiếp theo
                        data = reviewData[55 + random.nextInt(20)];
                    } else if (ratingDistribution < 99) {
                        // Rating 2 - chọn từ 10 mẫu tiếp theo
                        data = reviewData[75 + random.nextInt(10)];
                    } else {
                        // Rating 1 - chọn từ 5 mẫu cuối
                        data = reviewData[85 + random.nextInt(5)];
                    }

                    // Chọn ngẫu nhiên một user (không trùng lặp trong cùng tour nếu có thể)
                    User user = activeClientUsers.get(random.nextInt(activeClientUsers.size()));

                    Review review = new Review();
                    review.setCreatedBy(user);
                    review.setReviewableType(ReviewableType.TOUR);
                    review.setReviewableId(tour.getId());
                    review.setRating(Integer.parseInt(data[0]));
                    review.setContent(data[1]);

                    // Phân bố status: 85% APPROVED, 10% PENDING, 3% REJECTED, 2% HIDDEN
                    int statusRandom = random.nextInt(100);
                    if (statusRandom < 85) {
                        review.setStatus(ReviewStatus.APPROVED);
                    } else if (statusRandom < 95) {
                        review.setStatus(ReviewStatus.PENDING);
                    } else if (statusRandom < 98) {
                        review.setStatus(ReviewStatus.REJECTED);
                    } else {
                        review.setStatus(ReviewStatus.HIDDEN);
                    }

                    reviews.add(review);
                }
            }

            reviewRepository.saveAll(reviews);
            logger.info("Seeded {} reviews successfully!", reviews.size());
        } else {
            logger.info("Reviews already exist. Skipping seeding.");
        }
    }
}
