package com.example.final_project.Repository;

import com.example.final_project.Model.Accountant;
import com.example.final_project.Model.Branch;
import com.example.final_project.Model.Business;
import com.example.final_project.Model.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import java.time.LocalDate;
import java.util.List;

import java.util.List;
import java.util.Set;

@Repository
public interface SalesRepository extends JpaRepository<Sales,Integer> {

    Sales findSalesById(Integer id);

    // NOW() + INTERVAL 7 DAY
//    @Query("select s from Sales s where s.invoiceDate")
//    List<Sales> findSalesByDateThreeMonthsBefore();

    @Query("select s from Sales s where s.branch.id=?1 and s.saleDate> now()- 90 day")
    Set<Sales> findSalesByBranchId(Integer branchId);

    @Query("SELECT SUM(s.grand_amount) FROM Sales s")
    Double getTotalGrandAmount();

    @Query("SELECT s FROM Sales s WHERE (:branchId IS NULL OR s.branch.id = :branchId)")
    List<Sales> findSalesByBranch(@Param("branchId") Integer branchId);

    @Query(value = """
    SELECT s.*
    FROM sales s
    JOIN branch b ON s.branch_id = b.id
    JOIN business biz ON b.business_id = biz.id
    JOIN tax_payer t ON biz.tax_payer_id = t.id
    WHERE t.id = :taxPayerId
""", nativeQuery = true)
    List<Sales> findSalesByTaxPayerId(@Param("taxPayerId") Integer taxPayerId);



    List<Sales> findSalesByBranch(Branch branch);



    @Query("select s from Sales s where s.branch.business.id=?1 and s.saleDate between ?2 and ?3")
    List<Sales> findSalesByBranch_BusinessAndSaleDateBetween(Business branchBusiness, LocalDateTime saleDateAfter, LocalDateTime saleDateBefore);


}
