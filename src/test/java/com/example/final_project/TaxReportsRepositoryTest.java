//package com.example.final_project;
//
//import com.example.final_project.Model.Auditor;
//import com.example.final_project.Model.MyUser;
//import com.example.final_project.Model.TaxReports;
//import com.example.final_project.Repository.AuditorRepository;
//import com.example.final_project.Repository.MyUserRepository;
//import com.example.final_project.Repository.TaxReportsRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@ExtendWith(SpringExtension.class)
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//public class TaxReportsRepositoryTest {
//
//
//    @Autowired
//    TaxReportsRepository taxReportsRepository;
//
//    @Autowired
//    AuditorRepository auditorRepository;
//
//    @Autowired
//    MyUserRepository myUserRepository;
//
//    Auditor auditor;
//    TaxReports report1, report2, report3;
//
//    @BeforeEach
//    void setUp() {
//        // إنشاء المستخدم أولاً
//        MyUser user = new MyUser(null, "auditor1", "khalid.1", "123456", "Khalid1@gmail.com", "AUDITOR", null, null, null);
//        myUserRepository.save(user);
//
//        // ثم إنشاء المدقق وربطه بالمستخدم
//        auditor = new Auditor();
//        auditor.setSOCPA("123456789");
//        auditor.setMyUser(user);
//        auditorRepository.save(auditor);
//
//        // إنشاء تقارير ضريبية وربطها بالمدقق، مع التأكد من صحة كل الحقول
//        report1 = new TaxReports();
//        report1.setTotalTax(1000.0);
//        report1.setStart_date(LocalDateTime.now().minusDays(5));
//        report1.setEnd_date(LocalDateTime.now().minusDays(3));
//        report1.setPaymentDate(LocalDate.now());
//        report1.setStatus("Approved");
//        report1.setAuditor(auditor);
//
//        report2 = new TaxReports();
//        report2.setTotalTax(2000.0);
//        report2.setStart_date(LocalDateTime.now().minusDays(10));
//        report2.setEnd_date(LocalDateTime.now().minusDays(8));
//        report2.setPaymentDate(LocalDate.now());
//        report2.setStatus("Pending");
//        report2.setAuditor(auditor);
//
//        report3 = new TaxReports();
//        report3.setTotalTax(1500.0);
//        report3.setStart_date(LocalDateTime.now().minusDays(15));
//        report3.setEnd_date(LocalDateTime.now().minusDays(12));
//        report3.setPaymentDate(null); // هذا متعمد لاختبار دالة معينة
//        report3.setStatus("Rejected");
//        report3.setAuditor(auditor);
//
//        taxReportsRepository.saveAll(List.of(report1, report2, report3));
//    }
//
//
//    @Test
//    void testFindAllByAuditorId() {
//        List<TaxReports> reports = taxReportsRepository.findAllByAuditorId(auditor.getId());
//        assertThat(reports.size()).isEqualTo(3);
//        assertThat(reports.get(0).getAuditor().getId()).isEqualTo(auditor.getId());
//    }
//
//
//
//
//
//}
