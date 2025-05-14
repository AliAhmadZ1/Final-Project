//package com.example.final_project;
//
//
//
//import com.example.final_project.Model.Auditor;
//import com.example.final_project.Model.MyUser;
//import com.example.final_project.Repository.AuditorRepository;
//import com.example.final_project.Repository.MyUserRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.hamcrest.Matchers.is;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class AuditorControllerTest{
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private AuditorRepository auditorRepository;
//
//    @Autowired
//    private MyUserRepository myUserRepository;
//
//
//    @Test
//    public void testGetAuditorById() throws Exception {
//        // إنشاء وحفظ MyUser
//        MyUser myUser = new MyUser();
//        myUser.setName("Mohammed");
//        myUser.setUsername("mohammed@gmail.com");
//        myUser.setPassword("12345678");
//        myUser.setEmail("mohammed@gmail.com");
//        myUser.setRole("AUDITOR");
//        myUserRepository.save(myUser);
//
//        // إعادة تحميل MyUser من قاعدة البيانات لتفادي Detached Entity
//        MyUser savedUser = myUserRepository.findById(myUser.getId()).orElseThrow();
//
//        // إنشاء Auditor وربطه مع النسخة المرتبطة من MyUser
//        Auditor auditor = new Auditor();
//        auditor.setSOCPA("M12");
//        auditor.setMyUser(savedUser); // هنا الفرق
//        auditorRepository.save(auditor);
//
//        // تنفيذ طلب GET لاستخدام ID الخاص بالـ auditor
//        mockMvc.perform(get("/api/v1/auditor/get/" + auditor.getId()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.myUser.name", is("Mohammed")))
//                .andExpect(jsonPath("$.myUser.username", is("mohammed@gmail.com")))
//                .andExpect(jsonPath("$.myUser.password", is("12345678")));
//    }
//
//
//
//
//
//
//}
//
//
//
