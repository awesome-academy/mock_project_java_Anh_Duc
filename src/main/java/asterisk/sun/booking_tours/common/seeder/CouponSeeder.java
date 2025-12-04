package asterisk.sun.booking_tours.common.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import asterisk.sun.booking_tours.core.coupon.Coupon;
import asterisk.sun.booking_tours.core.coupon.CouponRepository;
import asterisk.sun.booking_tours.core.coupon.CouponStatus;
import asterisk.sun.booking_tours.core.coupon.CouponType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(7)
public class CouponSeeder implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CouponSeeder.class);
    private final CouponRepository couponRepository;

    public CouponSeeder(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedCoupons();
    }

    private void seedCoupons() {
        if (couponRepository.count() == 0) {
            logger.info("Seeding coupons...");

            List<Coupon> coupons = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            // ===== COUPONS ĐANG HOẠT ĐỘNG (ACTIVE) =====

            // 1. Giảm giá phần trăm cho khách hàng mới - 15%
            Coupon newCustomer15 = new Coupon();
            newCustomer15.setCode("NEWCUSTOMER15");
            newCustomer15.setDescription("Giảm 15% cho khách hàng mới đặt tour lần đầu. Áp dụng cho đơn hàng từ 5 triệu VNĐ.");
            newCustomer15.setType(CouponType.PERCENTAGE);
            newCustomer15.setDiscountValue(new BigDecimal("15"));
            newCustomer15.setMaxDiscountAmount(new BigDecimal("1000000")); // Giảm tối đa 1 triệu
            newCustomer15.setMinPurchaseAmount(new BigDecimal("5000000")); // Đơn tối thiểu 5 triệu
            newCustomer15.setUsageLimit(100);
            newCustomer15.setUsedCount(35); // Đã được sử dụng 35 lần
            newCustomer15.setValidFrom(now.minusDays(30));
            newCustomer15.setValidTo(now.plusDays(60));
            newCustomer15.setStatus(CouponStatus.ACTIVE);
            coupons.add(newCustomer15);

            // 2. Giảm giá cố định cho mùa hè - 500k
            Coupon summer500 = new Coupon();
            summer500.setCode("SUMMER2024");
            summer500.setDescription("Ưu đãi mùa hè! Giảm ngay 500,000 VNĐ cho tour du lịch biển. Áp dụng cho đơn từ 3 triệu.");
            summer500.setType(CouponType.FIXED_AMOUNT);
            summer500.setDiscountValue(new BigDecimal("500000"));
            summer500.setMinPurchaseAmount(new BigDecimal("3000000"));
            summer500.setUsageLimit(200);
            summer500.setUsedCount(87);
            summer500.setValidFrom(now.minusDays(15));
            summer500.setValidTo(now.plusDays(45));
            summer500.setStatus(CouponStatus.ACTIVE);
            coupons.add(summer500);

            // 3. Flash Sale - 20%
            Coupon flashSale20 = new Coupon();
            flashSale20.setCode("FLASH20");
            flashSale20.setDescription("Flash Sale! Giảm 20% cho tất cả các tour. Giảm tối đa 2 triệu. Có hạn số lượng!");
            flashSale20.setType(CouponType.PERCENTAGE);
            flashSale20.setDiscountValue(new BigDecimal("20"));
            flashSale20.setMaxDiscountAmount(new BigDecimal("2000000"));
            flashSale20.setMinPurchaseAmount(new BigDecimal("7000000"));
            flashSale20.setUsageLimit(50);
            flashSale20.setUsedCount(42);
            flashSale20.setValidFrom(now.minusDays(5));
            flashSale20.setValidTo(now.plusDays(10));
            flashSale20.setStatus(CouponStatus.ACTIVE);
            coupons.add(flashSale20);

            // 4. Combo gia đình - 1 triệu
            Coupon family1m = new Coupon();
            family1m.setCode("FAMILY1M");
            family1m.setDescription("Ưu đãi đặc biệt cho tour gia đình! Giảm 1,000,000 VNĐ. Áp dụng từ 10 triệu trở lên.");
            family1m.setType(CouponType.FIXED_AMOUNT);
            family1m.setDiscountValue(new BigDecimal("1000000"));
            family1m.setMinPurchaseAmount(new BigDecimal("10000000"));
            family1m.setUsageLimit(150);
            family1m.setUsedCount(62);
            family1m.setValidFrom(now.minusDays(20));
            family1m.setValidTo(now.plusDays(70));
            family1m.setStatus(CouponStatus.ACTIVE);
            coupons.add(family1m);

            // 5. Giảm giá cuối tuần - 10%
            Coupon weekend10 = new Coupon();
            weekend10.setCode("WEEKEND10");
            weekend10.setDescription("Giảm 10% cho tour khởi hành cuối tuần. Áp dụng từ thứ 6 đến chủ nhật.");
            weekend10.setType(CouponType.PERCENTAGE);
            weekend10.setDiscountValue(new BigDecimal("10"));
            weekend10.setMaxDiscountAmount(new BigDecimal("800000"));
            weekend10.setMinPurchaseAmount(new BigDecimal("4000000"));
            weekend10.setUsageLimit(null); // Không giới hạn
            weekend10.setUsedCount(125);
            weekend10.setValidFrom(now.minusDays(10));
            weekend10.setValidTo(now.plusDays(90));
            weekend10.setStatus(CouponStatus.ACTIVE);
            coupons.add(weekend10);

            // 6. VIP - 25%
            Coupon vip25 = new Coupon();
            vip25.setCode("VIP25");
            vip25.setDescription("Mã VIP độc quyền! Giảm 25% cho tour cao cấp. Giảm tối đa 3 triệu VNĐ.");
            vip25.setType(CouponType.PERCENTAGE);
            vip25.setDiscountValue(new BigDecimal("25"));
            vip25.setMaxDiscountAmount(new BigDecimal("3000000"));
            vip25.setMinPurchaseAmount(new BigDecimal("15000000"));
            vip25.setUsageLimit(30);
            vip25.setUsedCount(18);
            vip25.setValidFrom(now.minusDays(7));
            vip25.setValidTo(now.plusDays(30));
            vip25.setStatus(CouponStatus.ACTIVE);
            coupons.add(vip25);

            // 7. Đặt sớm - 300k
            Coupon earlyBird300 = new Coupon();
            earlyBird300.setCode("EARLYBIRD300");
            earlyBird300.setDescription("Ưu đãi đặt tour trước 30 ngày! Giảm 300,000 VNĐ cho mọi tour.");
            earlyBird300.setType(CouponType.FIXED_AMOUNT);
            earlyBird300.setDiscountValue(new BigDecimal("300000"));
            earlyBird300.setMinPurchaseAmount(new BigDecimal("2000000"));
            earlyBird300.setUsageLimit(null);
            earlyBird300.setUsedCount(94);
            earlyBird300.setValidFrom(now.minusDays(25));
            earlyBird300.setValidTo(now.plusDays(120));
            earlyBird300.setStatus(CouponStatus.ACTIVE);
            coupons.add(earlyBird300);

            // 8. Tour miền Bắc - 12%
            Coupon north12 = new Coupon();
            north12.setCode("NORTH12");
            north12.setDescription("Khám phá miền Bắc! Giảm 12% cho các tour Hà Nội, Sapa, Hạ Long.");
            north12.setType(CouponType.PERCENTAGE);
            north12.setDiscountValue(new BigDecimal("12"));
            north12.setMaxDiscountAmount(new BigDecimal("1500000"));
            north12.setMinPurchaseAmount(new BigDecimal("6000000"));
            north12.setUsageLimit(80);
            north12.setUsedCount(45);
            north12.setValidFrom(now.minusDays(12));
            north12.setValidTo(now.plusDays(50));
            north12.setStatus(CouponStatus.ACTIVE);
            coupons.add(north12);

            // 9. Nhóm đông người - 2 triệu
            Coupon group2m = new Coupon();
            group2m.setCode("GROUP2M");
            group2m.setDescription("Ưu đãi đặc biệt cho nhóm từ 10 người trở lên. Giảm ngay 2,000,000 VNĐ!");
            group2m.setType(CouponType.FIXED_AMOUNT);
            group2m.setDiscountValue(new BigDecimal("2000000"));
            group2m.setMinPurchaseAmount(new BigDecimal("20000000"));
            group2m.setUsageLimit(40);
            group2m.setUsedCount(12);
            group2m.setValidFrom(now.minusDays(8));
            group2m.setValidTo(now.plusDays(60));
            group2m.setStatus(CouponStatus.ACTIVE);
            coupons.add(group2m);

            // 10. Sinh nhật công ty - 30%
            Coupon birthday30 = new Coupon();
            birthday30.setCode("HAPPY5YEARS");
            birthday30.setDescription("Kỷ niệm 5 năm thành lập! Giảm 30% tất cả tour. Có giới hạn số lượng!");
            birthday30.setType(CouponType.PERCENTAGE);
            birthday30.setDiscountValue(new BigDecimal("30"));
            birthday30.setMaxDiscountAmount(new BigDecimal("4000000"));
            birthday30.setMinPurchaseAmount(new BigDecimal("8000000"));
            birthday30.setUsageLimit(25);
            birthday30.setUsedCount(20);
            birthday30.setValidFrom(now.minusDays(3));
            birthday30.setValidTo(now.plusDays(7));
            birthday30.setStatus(CouponStatus.ACTIVE);
            coupons.add(birthday30);

            // 11. Tour quốc tế - 8%
            Coupon international8 = new Coupon();
            international8.setCode("INTL8");
            international8.setDescription("Du lịch quốc tế giá tốt! Giảm 8% cho tour nước ngoài.");
            international8.setType(CouponType.PERCENTAGE);
            international8.setDiscountValue(new BigDecimal("8"));
            international8.setMaxDiscountAmount(new BigDecimal("5000000"));
            international8.setMinPurchaseAmount(new BigDecimal("25000000"));
            international8.setUsageLimit(60);
            international8.setUsedCount(28);
            international8.setValidFrom(now.minusDays(18));
            international8.setValidTo(now.plusDays(80));
            international8.setStatus(CouponStatus.ACTIVE);
            coupons.add(international8);

            // 12. Học sinh sinh viên - 200k
            Coupon student200 = new Coupon();
            student200.setCode("STUDENT200");
            student200.setDescription("Ưu đãi đặc biệt cho học sinh, sinh viên. Giảm 200,000 VNĐ với thẻ học sinh.");
            student200.setType(CouponType.FIXED_AMOUNT);
            student200.setDiscountValue(new BigDecimal("200000"));
            student200.setMinPurchaseAmount(new BigDecimal("1500000"));
            student200.setUsageLimit(null);
            student200.setUsedCount(156);
            student200.setValidFrom(now.minusDays(40));
            student200.setValidTo(now.plusDays(100));
            student200.setStatus(CouponStatus.ACTIVE);
            coupons.add(student200);

            // ===== COUPONS KHÔNG HOẠT ĐỘNG (INACTIVE) =====

            // 13. Coupon tạm ngưng - 18%
            Coupon paused18 = new Coupon();
            paused18.setCode("PAUSED18");
            paused18.setDescription("Coupon tạm thời ngưng hoạt động. Giảm 18% - Sẽ được kích hoạt lại sau.");
            paused18.setType(CouponType.PERCENTAGE);
            paused18.setDiscountValue(new BigDecimal("18"));
            paused18.setMaxDiscountAmount(new BigDecimal("2000000"));
            paused18.setMinPurchaseAmount(new BigDecimal("5000000"));
            paused18.setUsageLimit(100);
            paused18.setUsedCount(45);
            paused18.setValidFrom(now.minusDays(60));
            paused18.setValidTo(now.plusDays(30));
            paused18.setStatus(CouponStatus.INACTIVE);
            coupons.add(paused18);

            // 14. Sự kiện đặc biệt - 1.5 triệu
            Coupon event15m = new Coupon();
            event15m.setCode("EVENT1M5");
            event15m.setDescription("Sự kiện đặc biệt tạm hoãn. Giảm 1,500,000 VNĐ cho tour cao cấp.");
            event15m.setType(CouponType.FIXED_AMOUNT);
            event15m.setDiscountValue(new BigDecimal("1500000"));
            event15m.setMinPurchaseAmount(new BigDecimal("12000000"));
            event15m.setUsageLimit(50);
            event15m.setUsedCount(22);
            event15m.setValidFrom(now.minusDays(45));
            event15m.setValidTo(now.plusDays(15));
            event15m.setStatus(CouponStatus.INACTIVE);
            coupons.add(event15m);

            // ===== COUPONS ĐÃ HẾT HẠN (EXPIRED) =====

            // 15. Tết 2024 - 35%
            Coupon tet35 = new Coupon();
            tet35.setCode("TET2024");
            tet35.setDescription("Khuyến mãi Tết Nguyên Đán 2024. Giảm 35% cho mọi tour.");
            tet35.setType(CouponType.PERCENTAGE);
            tet35.setDiscountValue(new BigDecimal("35"));
            tet35.setMaxDiscountAmount(new BigDecimal("5000000"));
            tet35.setMinPurchaseAmount(new BigDecimal("10000000"));
            tet35.setUsageLimit(200);
            tet35.setUsedCount(200); // Đã hết lượt
            tet35.setValidFrom(now.minusDays(120));
            tet35.setValidTo(now.minusDays(90)); // Đã hết hạn
            tet35.setStatus(CouponStatus.EXPIRED);
            coupons.add(tet35);

            // 16. Black Friday - 40%
            Coupon blackFriday40 = new Coupon();
            blackFriday40.setCode("BLACKFRIDAY40");
            blackFriday40.setDescription("Black Friday Sale! Giảm giá khủng 40%! Đã kết thúc.");
            blackFriday40.setType(CouponType.PERCENTAGE);
            blackFriday40.setDiscountValue(new BigDecimal("40"));
            blackFriday40.setMaxDiscountAmount(new BigDecimal("8000000"));
            blackFriday40.setMinPurchaseAmount(new BigDecimal("15000000"));
            blackFriday40.setUsageLimit(100);
            blackFriday40.setUsedCount(100);
            blackFriday40.setValidFrom(now.minusDays(100));
            blackFriday40.setValidTo(now.minusDays(95));
            blackFriday40.setStatus(CouponStatus.EXPIRED);
            coupons.add(blackFriday40);

            // 17. Lễ 30/4 - 1 triệu
            Coupon apr30 = new Coupon();
            apr30.setCode("APR301M");
            apr30.setDescription("Khuyến mãi lễ 30/4 - 1/5. Giảm 1,000,000 VNĐ. Đã hết hạn.");
            apr30.setType(CouponType.FIXED_AMOUNT);
            apr30.setDiscountValue(new BigDecimal("1000000"));
            apr30.setMinPurchaseAmount(new BigDecimal("7000000"));
            apr30.setUsageLimit(150);
            apr30.setUsedCount(138);
            apr30.setValidFrom(now.minusDays(200));
            apr30.setValidTo(now.minusDays(180));
            apr30.setStatus(CouponStatus.EXPIRED);
            coupons.add(apr30);

            // 18. Giáng sinh - 22%
            Coupon christmas22 = new Coupon();
            christmas22.setCode("XMAS22");
            christmas22.setDescription("Merry Christmas! Giảm 22% mùa Giáng sinh. Chương trình đã kết thúc.");
            christmas22.setType(CouponType.PERCENTAGE);
            christmas22.setDiscountValue(new BigDecimal("22"));
            christmas22.setMaxDiscountAmount(new BigDecimal("3000000"));
            christmas22.setMinPurchaseAmount(new BigDecimal("8000000"));
            christmas22.setUsageLimit(80);
            christmas22.setUsedCount(76);
            christmas22.setValidFrom(now.minusDays(150));
            christmas22.setValidTo(now.minusDays(130));
            christmas22.setStatus(CouponStatus.EXPIRED);
            coupons.add(christmas22);

            // 19. Valentine - 14%
            Coupon valentine14 = new Coupon();
            valentine14.setCode("LOVE14");
            valentine14.setDescription("Tình yêu Valentine! Giảm 14% cho tour lãng mạn dành cho đôi lứa.");
            valentine14.setType(CouponType.PERCENTAGE);
            valentine14.setDiscountValue(new BigDecimal("14"));
            valentine14.setMaxDiscountAmount(new BigDecimal("1200000"));
            valentine14.setMinPurchaseAmount(new BigDecimal("5000000"));
            valentine14.setUsageLimit(120);
            valentine14.setUsedCount(118);
            valentine14.setValidFrom(now.minusDays(280));
            valentine14.setValidTo(now.minusDays(270));
            valentine14.setStatus(CouponStatus.EXPIRED);
            coupons.add(valentine14);

            // 20. Tour mùa thu - 600k
            Coupon autumn600 = new Coupon();
            autumn600.setCode("AUTUMN600");
            autumn600.setDescription("Mùa thu vàng! Giảm 600,000 VNĐ cho tour ngắm cảnh mùa thu. Đã hết hạn.");
            autumn600.setType(CouponType.FIXED_AMOUNT);
            autumn600.setDiscountValue(new BigDecimal("600000"));
            autumn600.setMinPurchaseAmount(new BigDecimal("4000000"));
            autumn600.setUsageLimit(100);
            autumn600.setUsedCount(92);
            autumn600.setValidFrom(now.minusDays(210));
            autumn600.setValidTo(now.minusDays(150));
            autumn600.setStatus(CouponStatus.EXPIRED);
            coupons.add(autumn600);

            couponRepository.saveAll(coupons);
            logger.info("Seeded {} coupons successfully!", coupons.size());
            logger.info("  - {} ACTIVE coupons", coupons.stream().filter(c -> c.getStatus() == CouponStatus.ACTIVE).count());
            logger.info("  - {} INACTIVE coupons", coupons.stream().filter(c -> c.getStatus() == CouponStatus.INACTIVE).count());
            logger.info("  - {} EXPIRED coupons", coupons.stream().filter(c -> c.getStatus() == CouponStatus.EXPIRED).count());
        } else {
            logger.info("Coupons already exist (count: {}). Skipping seeding.", couponRepository.count());
        }
    }
}
