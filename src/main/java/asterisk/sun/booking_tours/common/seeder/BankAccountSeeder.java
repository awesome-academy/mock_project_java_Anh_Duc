package asterisk.sun.booking_tours.common.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import asterisk.sun.booking_tours.core.payment.BankAccount;
import asterisk.sun.booking_tours.core.payment.BankAccountRepository;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Order(8)
public class BankAccountSeeder implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(BankAccountSeeder.class);
    private final BankAccountRepository bankAccountRepository;

    public BankAccountSeeder(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedBankAccounts();
    }

    private void seedBankAccounts() {
        if (bankAccountRepository.count() == 0) {
            logger.info("Seeding bank accounts...");

            List<BankAccount> bankAccounts = new ArrayList<>();

            // Vietcombank - Main Account (Priority 1)
            BankAccount vcb = new BankAccount();
            vcb.setBankName("Vietcombank");
            vcb.setAccountNumber("0011234567890");
            vcb.setAccountHolder("CÔNG TY DU LỊCH ASTERISK SUN");
            vcb.setBranch("Hà Nội");
            vcb.setSwiftCode("BFTVVNVX");
            vcb.setDescription("Tài khoản ngân hàng chính dành cho thanh toán tour du lịch. Hỗ trợ chuyển khoản nhanh 24/7.");
            vcb.setIsActive(true);
            vcb.setDisplayOrder(1);
            bankAccounts.add(vcb);

            // // VietinBank - Secondary Account (Priority 2)
            // BankAccount vtb = new BankAccount();
            // vtb.setBankName("VietinBank");
            // vtb.setAccountNumber("123456789012");
            // vtb.setAccountHolder("CÔNG TY DU LỊCH ASTERISK SUN");
            // vtb.setBranch("TP. Hồ Chí Minh");
            // vtb.setSwiftCode("ICBVVNVX");
            // vtb.setDescription("Tài khoản dự phòng cho khách hàng khu vực miền Nam. Hỗ trợ thanh toán trực tuyến.");
            // vtb.setIsActive(true);
            // vtb.setDisplayOrder(2);
            // bankAccounts.add(vtb);

            // // BIDV (Priority 3)
            // BankAccount bidv = new BankAccount();
            // bidv.setBankName("BIDV");
            // bidv.setAccountNumber("98765432101");
            // bidv.setAccountHolder("CÔNG TY DU LỊCH ASTERISK SUN");
            // bidv.setBranch("Đà Nẵng");
            // bidv.setSwiftCode("BIDVVNVX");
            // bidv.setDescription("Tài khoản cho khách hàng khu vực miền Trung. Hỗ trợ Internet Banking.");
            // bidv.setIsActive(true);
            // bidv.setDisplayOrder(3);
            // bankAccounts.add(bidv);

            // // Techcombank (Priority 4)
            // BankAccount tcb = new BankAccount();
            // tcb.setBankName("Techcombank");
            // tcb.setAccountNumber("19036688668866");
            // tcb.setAccountHolder("CÔNG TY DU LỊCH ASTERISK SUN");
            // tcb.setBranch("Hà Nội");
            // tcb.setSwiftCode("VTCBVNVX");
            // tcb.setDescription("Tài khoản giao dịch online. Nhận tiền nhanh chóng qua ứng dụng di động.");
            // tcb.setIsActive(true);
            // tcb.setDisplayOrder(4);
            // bankAccounts.add(tcb);

            // // ACB (Priority 5)
            // BankAccount acb = new BankAccount();
            // acb.setBankName("ACB");
            // acb.setAccountNumber("123456789");
            // acb.setAccountHolder("CÔNG TY DU LỊCH ASTERISK SUN");
            // acb.setBranch("TP. Hồ Chí Minh");
            // acb.setSwiftCode("ASCBVNVX");
            // acb.setDescription("Tài khoản dành cho thanh toán nhanh qua ví điện tử và QR Code.");
            // acb.setIsActive(true);
            // acb.setDisplayOrder(5);
            // bankAccounts.add(acb);

            // // MBBank (Priority 6)
            // BankAccount mb = new BankAccount();
            // mb.setBankName("MBBank");
            // mb.setAccountNumber("0123456789012");
            // mb.setAccountHolder("CÔNG TY DU LỊCH ASTERISK SUN");
            // mb.setBranch("Hà Nội");
            // mb.setSwiftCode("MSCBVNVX");
            // mb.setDescription("Tài khoản thanh toán tour du lịch. Hỗ trợ chuyển khoản liên ngân hàng miễn phí.");
            // mb.setIsActive(true);
            // mb.setDisplayOrder(6);
            // bankAccounts.add(mb);

            // // VPBank - International (Priority 7)
            // BankAccount vpb = new BankAccount();
            // vpb.setBankName("VPBank");
            // vpb.setAccountNumber("123456789012345");
            // vpb.setAccountHolder("ASTERISK SUN TOURISM COMPANY LIMITED");
            // vpb.setBranch("Hà Nội");
            // vpb.setSwiftCode("VPBKVNVX");
            // vpb.setDescription("Tài khoản dành cho khách hàng quốc tế. Hỗ trợ nhận tiền bằng ngoại tệ.");
            // vpb.setIsActive(true);
            // vpb.setDisplayOrder(7);
            // bankAccounts.add(vpb);

            // // Sacombank (Currently Inactive - for testing)
            // BankAccount stb = new BankAccount();
            // stb.setBankName("Sacombank");
            // stb.setAccountNumber("060123456789");
            // stb.setAccountHolder("CÔNG TY DU LỊCH ASTERISK SUN");
            // stb.setBranch("TP. Hồ Chí Minh");
            // stb.setSwiftCode("SGTTVNVX");
            // stb.setDescription("Tài khoản tạm thời ngừng sử dụng. Đang bảo trì hệ thống.");
            // stb.setIsActive(false);
            // stb.setDisplayOrder(8);
            // bankAccounts.add(stb);

            // // TPBank (Priority 9)
            // BankAccount tpb = new BankAccount();
            // tpb.setBankName("TPBank");
            // tpb.setAccountNumber("01234567890123");
            // tpb.setAccountHolder("CÔNG TY DU LỊCH ASTERISK SUN");
            // tpb.setBranch("Hà Nội");
            // tpb.setSwiftCode("TPBVVNVX");
            // tpb.setDescription("Tài khoản thanh toán tự động. Xác nhận thanh toán tức thì qua SMS.");
            // tpb.setIsActive(true);
            // tpb.setDisplayOrder(9);
            // bankAccounts.add(tpb);

            // // SHB (Priority 10)
            // BankAccount shb = new BankAccount();
            // shb.setBankName("SHB");
            // shb.setAccountNumber("1234567890");
            // shb.setAccountHolder("CÔNG TY DU LỊCH ASTERISK SUN");
            // shb.setBranch("Hà Nội");
            // shb.setSwiftCode("SHBAVNVX");
            // shb.setDescription("Tài khoản dự phòng cho các giao dịch đặc biệt.");
            // shb.setIsActive(true);
            // shb.setDisplayOrder(10);
            // bankAccounts.add(shb);

            bankAccountRepository.saveAll(bankAccounts);
            logger.info("Seeded {} bank accounts successfully.", bankAccounts.size());
        } else {
            logger.info("Bank accounts already exist. Skipping seeding.");
        }
    }
}
