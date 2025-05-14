//package com.example.final_project;
//
//
//import com.example.final_project.Model.ItemSale;
//import com.example.final_project.Model.MyUser;
//import com.example.final_project.Model.Product;
//import com.example.final_project.Repository.ItemSaleRepository;
//import com.example.final_project.Repository.MyUserRepository;
//import com.example.final_project.Repository.ProductRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@ExtendWith(SpringExtension.class)
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//public class ItemSaleRepositoryTest {
//
//    @Autowired
//    ItemSaleRepository itemSaleRepository;
//
//    @Autowired
//    MyUserRepository myUserRepository;
//
//    @Autowired
//    ProductRepository productRepository;
//    ItemSale item1, item2 ;
//
//    MyUser myUser;
//
//    Product product1,product2;
//
//
//    @BeforeEach
//    void setUp() {
//        item1 = new ItemSale();
//        item1.setProductName("Sony headphone");
//        item1.setQuantity(1);
//        item1.setUnitPrice(999.0);
//        item1.setTotalPrice(999.0);
//
//        item2 = new ItemSale();
//        item2.setProductName("Bose wireless headphones");
//        item2.setQuantity(3);
//        item2.setUnitPrice(375.0);
//        item2.setTotalPrice(1125.0);
//
//        myUser = new MyUser(1,"myuser","admin","123456789","myuser@gmail.com","ADMIN",null,null,null);
//
//        product1 = new Product(1,"water",12.0,24,"123456789",null,null);
//
//        productRepository.save(product1);
//        myUserRepository.save(myUser);
//        itemSaleRepository.save(item1);
//        itemSaleRepository.save(item2);
//
//    }
//
//    @Test
//    public void testFindItemSaleById() {
//        ItemSale found = itemSaleRepository.findItemSaleById(item2.getId());
//
//        assertThat(found).isNotNull();
//        assertThat(found.getProductName()).isEqualTo("Bose wireless headphones");
//        assertThat(found.getQuantity()).isEqualTo(3);
//        assertThat(found.getUnitPrice()).isEqualTo(375.0);
//        assertThat(found.getTotalPrice()).isEqualTo(1125.0);
//    }
//
//    @Test
//    public void findUserByIdAndRoleTest(){
//        MyUser user = myUserRepository.findUserByIdAndRole(1,"ADMIN");
//
//        assertThat(user).isEqualTo(myUser);
//    }
//
//    @Test
//    public void findProductByBarcode(){
//        Product product = productRepository.findProductByBarcode("123456789");
//
//        assertThat(product).isEqualTo(product1);
//    }
//
//
//
//}
