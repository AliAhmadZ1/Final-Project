package com.example.final_project.Service;

import com.example.final_project.Api.ApiException;
import com.example.final_project.DTO.ItemsDTO;
import com.example.final_project.DTO.SaleDTO;
import com.example.final_project.Model.Branch;
import com.example.final_project.Model.CounterBox;
import com.example.final_project.Model.Product;
import com.example.final_project.Model.Sales;
import com.example.final_project.Repository.BranchRepository;
import com.example.final_project.Repository.CounterBoxRepository;
import com.example.final_project.Repository.ProductRepository;
import com.example.final_project.Repository.SalesRepository;
import com.example.final_project.DTO.ProductDTO;
import com.example.final_project.Model.*;
import com.example.final_project.Repository.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.hibernate.id.IntegralDataTypeHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDate;
import java.util.*;



@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesRepository;
    private final CounterBoxRepository counterBoxRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final AccountantRepository accountantRepository;
    private final ItemSaleRepository itemSaleRepository;
    private final MyUserRepository myUserRepository;

    public List<Sales> getAllSales() {
        return salesRepository.findAll();
    }



    public void addSales(Integer accountantId, Integer counterBox_id,  SaleDTO saleDTO) {


        Accountant accountant = accountantRepository.findAccountantById(accountantId);
        if (accountant == null) {
            throw new ApiException("accountant is not found ");
        }


        CounterBox counterBox = counterBoxRepository.findCounterBoxById(counterBox_id);
        Branch branch = branchRepository.findBranchesById(accountant.getBranch().getId());

        if (counterBox == null && branch == null) {
            throw new ApiException("Branch or Counter Box not found ");
        }

        if (!accountant.getCounterBoxes().contains(counterBox)) {
            throw new ApiException("The counter does not belong to the accountant");
        }


        if (counterBox.getStatus().equals("Closed")) {
            throw new ApiException("The counter box is closed");
        }

        Sales newSale=new Sales();
        newSale.setSalesStatus("DRAFT");
        newSale.setBranch(branch);
        newSale.setSale_invoice(saleDTO.getSale_invoice());
        newSale.setCounterBox(counterBox);
        salesRepository.save(newSale);
    }


    public void addProductInSale(Integer accountantId, Integer saleId, ItemsDTO item) {

        Accountant accountant = accountantRepository.findAccountantById(accountantId);
        if (accountant == null) {
            throw new ApiException("accountant is not found or does not belong to the mentioned branch");
        }

        Sales currentSale=salesRepository.findSalesById(saleId);
        if (currentSale == null) {
            throw new ApiException("the sale is not found ");
        }

        if (currentSale.getCounterBox().getAccountant().getId()!=accountantId){
            throw new ApiException("the counter box with this sale does not belong to this accountant ");

        }

        Product product = productRepository.findProductByBarcode(item.getBarcode());
        if (product==null){
            throw new ApiException("Sorry the product is not found . check the  product name again ");

        }


        if(product.getStock()< item.getQuantity()){
            throw new ApiException("Sorry the product is out of stock");

        }

        if (!currentSale.getSalesStatus().equals("DRAFT")){
            throw new ApiException("Sorry can not add product to confirmed invoice ");

        }

        /// create new item sale
        ItemSale itemSale=new ItemSale();
        itemSale.setSales(currentSale);
        itemSale.setProductName(product.getName());
        itemSale.setProduct(product);
        itemSale.setQuantity(item.getQuantity());
        itemSale.setUnitPrice(product.getPrice());
        itemSale.setTotalPrice(product.getPrice()* item.getQuantity());

        productRepository.save(product);
        itemSaleRepository.save(itemSale);

    }



    public void updateCalculations(Integer saleId){

        Sales sale=salesRepository.findSalesById(saleId);

        List<ItemSale> items =itemSaleRepository.findItemSaleBySalesId(saleId);
        if (items.isEmpty()){
            throw new ApiException("can not confirm an empty invoice ");
        }

        /// sub total before tax
        double subTotal=0.0;

        for(ItemSale i:items){
            subTotal+= i.getTotalPrice();
        }

        /// calc grand with tax
        double tax =subTotal*0.15;
        double grandTotal =subTotal+tax;

        sale.setTotal_amount(subTotal);
        sale.setTax_amount(tax);
        sale.setGrand_amount(grandTotal);
        salesRepository.save(sale);


    }


    public void confirmSale(Integer accountantId, Integer saleId){
        Accountant accountant = accountantRepository.findAccountantById(accountantId);
        if (accountant == null) {
            throw new ApiException("accountant is not found or does not belong to the mentioned branch");
        }

        Sales currentSale=salesRepository.findSalesById(saleId);
        if (currentSale == null) {
            throw new ApiException("the sale is not found ");
        }

        if (currentSale.getCounterBox().getAccountant().getId()!=accountantId){
            throw new ApiException("the counter box with this sale does not belong to this accountant ");

        }

        if (!currentSale.getSalesStatus().equals("DRAFT")){
            throw new ApiException("Sorry can not add product to confirmed invoice ");
        }

        List<ItemSale> items =itemSaleRepository.findItemSaleBySalesId(saleId);
        if (items.isEmpty()){
            throw new ApiException("can not confirm an empty invoice ");
        }


        for(ItemSale i : items){
         Product p = i.getProduct();
            p.setStock(p.getStock()-i.getQuantity());
            productRepository.save(p);
        }

        updateCalculations(saleId);
        currentSale.setSalesStatus("CONFIRMED");
        currentSale.setSaleDate(LocalDateTime.now());
        salesRepository.save(currentSale);

    }



    public ItemSale updateProductQuantity(Integer accountantId, Integer saleId, Integer itemId, Integer newQuantity) {

        Accountant accountant = accountantRepository.findAccountantById(accountantId);
        if (accountant == null) {
            throw new ApiException("Accountant not found");
        }

        Sales sale = salesRepository.findSalesById(saleId);
        if (sale == null) {
            throw new ApiException("Sale not found");
        }

        if (!sale.getCounterBox().getAccountant().getId().equals(accountantId)) {
            throw new ApiException("This sale does not belong to this accountant");
        }

        if (sale.getSalesStatus().equals("CONFIRMED")) {
            throw new ApiException("Cannot update product quantity for a confirmed invoice");
        }
        ItemSale itemSale = itemSaleRepository.findById(itemId)
            .orElseThrow(() -> new ApiException("Item not found"));

        if (!itemSale.getSales().getId().equals(saleId)) {
            throw new ApiException("This item is not part of the specified sale");
        }

        Product product = itemSale.getProduct();
        int quantityDifference = newQuantity - itemSale.getQuantity();

        if (product.getStock() < quantityDifference) {
            throw new ApiException("Insufficient stock for the new quantity");
        }

        itemSale.setQuantity(newQuantity);
        itemSale.setTotalPrice(newQuantity * product.getPrice());
         product.setStock(product.getStock() - quantityDifference);

         productRepository.save(product);
         itemSaleRepository.save(itemSale);

        return itemSale;
    }





    public byte[] printInvoice(Integer accountantId, Integer saleId) {

        Accountant accountant = accountantRepository.findAccountantById(accountantId);
        if (accountant == null) {
            throw new ApiException("accountant is not found or does not belong to the mentioned branch");
        }

        Sales currentSale = salesRepository.findSalesById(saleId);
        if (currentSale == null) {
            throw new ApiException("the Invoice is not found ");
        }

        if (currentSale.getCounterBox().getAccountant().getId() != accountantId) {
            throw new ApiException("the counter box with this sale does not belong to this accountant ");

        }

        if (!currentSale.getSalesStatus().equals("CONFIRMED")) {
            throw new ApiException("Cannot print not confirmed invoice");
        }


        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();


            try {
                InputStream is = getClass().getResourceAsStream("/logo.png");
                if (is != null) {
                    Image logo = Image.getInstance(is.readAllBytes());
                    logo.scaleToFit(120, 120);
                    logo.setAlignment(Element.ALIGN_CENTER);
                    document.add(logo);
                    document.add(Chunk.NEWLINE);
                }
            } catch (Exception e) {
                System.out.println("Logo not found or failed to load.");
            }


            Paragraph title = new Paragraph("SALE INVOICE", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);


            document.add(new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

            document.add(new Paragraph("------------------------------------------------------------"));

            document.add(new Paragraph("INVOICE ID: " + currentSale.getSale_invoice()));
            document.add(new Paragraph("Date: " + currentSale.getSaleDate()));
            document.add(new Paragraph("Branch: " + currentSale.getBranch().getBranchNumber()));
            document.add(new Paragraph("------------------------------------------------------------"));


            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Products:"));
            document.add(new Paragraph("------------------------------------------------------------"));


            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell("Product Name:");
            table.addCell(" Quantity:");
            table.addCell("Unit Price:");
            table.addCell("Total Price:");

           List <ItemSale> items=itemSaleRepository.findItemSaleBySalesId(saleId);

            for (ItemSale item : items) {

                table.addCell(item.getProductName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(String.valueOf(item.getUnitPrice()));
                Double total = item.getTotalPrice();
                table.addCell(String.valueOf(total));
            }


            document.add(table);




            /// the total prices tax +grand total after tax
            document.add(new Paragraph("------------------------------------------------------------"));
            document.add(new Paragraph("Subtotal :" + String.valueOf(currentSale.getTotal_amount())));
            document.add(new Paragraph("Tax (15%) :" + String.valueOf(currentSale.getTax_amount())));
            document.add(new Paragraph("Total :" + String.valueOf(currentSale.getGrand_amount())));
            document.add(new Paragraph("------------------------------------------------------------"));
            document.add(Chunk.NEWLINE);


            document.add(new Paragraph("Thank you for shopping with us!"));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Mohasil Team", new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC)));

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }



    public void updateSales(Integer accountantId,Integer saleId,Sales sales){
        Accountant accountant1 = accountantRepository.findAccountantById(accountantId);
        if (accountant1==null)
            throw new ApiException("accountant not found");
        Sales oldSales=salesRepository.findSalesById(saleId);

        if(oldSales==null){

        }
        oldSales.setSale_invoice(sales.getSale_invoice());
        oldSales.setTax_amount(sales.getTax_amount());
        oldSales.setTotal_amount(sales.getTotal_amount());
        oldSales.setGrand_amount(sales.getGrand_amount());

        salesRepository.save(oldSales);
    }


    public void deleteSales(Integer accountantId,Integer saleId){
        Accountant accountant = accountantRepository.findAccountantById(accountantId);
        if (accountant==null)
            throw new ApiException("you don't have permission");
        Sales sales=salesRepository.findSalesById(saleId);
        if(sales==null){
            throw new ApiException("Sales not found");
        }

        Set<ItemSale> itemSale = sales.getItemSales();

        itemSaleRepository.deleteAll(itemSale);
        salesRepository.delete(sales);
    }


    public Product getSingleProductByBarcode(String barcode) {
        Product product = productRepository.findProductByBarcode(barcode);
        if (product==null) {
            throw new ApiException("No product found with barcode: " + barcode);
        }
        return product;
    }

//    public void addProductToSales(Integer salesId, String barcode) {
//        Sales sales = salesRepository.findSalesById(salesId);
//        if (sales == null) {
//            throw new ApiException("Sales not found");
//        }
//
//        Product product = productRepository.findProductByBarcode(barcode);
//        if (product==null) {
//            throw new ApiException("Product not found with barcode: " + barcode);
//        }
//
//        sales.getProducts().add(product);
//        productRepository.save(product);
//        salesRepository.save(sales);
//    }


//    public void calculateSalesAmounts(Integer salesId) {
//        Sales sales = salesRepository.findSalesById(salesId);
//        if (sales == null) {
//            throw new ApiException("Sales not found");
//        }
//
//        double total = 0;
//        for (Product product : sales.getProducts()) {
//            total += product.getPrice();
//        }
//
//        double tax = total * 0.15;
//        double grand = total + tax;
//
//        sales.setTotal_amount(total);
//        sales.setTax_amount(tax);
//        sales.setGrand_amount(grand);
//
//        salesRepository.save(sales);
//    }



    public Map<String, Object> addSales2(Integer counterBox_id, Integer branch_id, Sales sales) {
        CounterBox counterBox = counterBoxRepository.findCounterBoxById(counterBox_id);
        Branch branch = branchRepository.findBranchesById(branch_id);

        if (counterBox == null || branch == null) {
            throw new ApiException("Branch or Counter Box not found ");
        }



        sales.setDate(LocalDateTime.now());
        LocalDate today = LocalDate.now();

        List<LocalDate> discountDates = List.of(
                LocalDate.of(2025, 5, 10),
                LocalDate.of(2025, 5, 20)
        );


        double originalAmount = sales.getTotal_amount();
        double finalAmount = originalAmount;
        double discountPercentage = 0.20;
        double discountAmount = 0.0;
        boolean discountApplied = false;

        if (discountDates.contains(today)) {
            discountAmount = originalAmount * discountPercentage;
            finalAmount = originalAmount - discountAmount;
            discountApplied = true;
        }


        double tax = finalAmount * 0.15;
        double grand = finalAmount + tax;

        sales.setTotal_amount(finalAmount);
        sales.setGrand_amount(grand);
        salesRepository.save(sales);

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("original_price", originalAmount);

        if (discountApplied) {
            result.put("discount_percentage", discountPercentage * 100 + "%");
            result.put("discount_amount", discountAmount);
            result.put("price_after_discount", finalAmount);
        }

        result.put("tax", tax);
        result.put("grand_total", grand);

        return result;
    }

    public List<Sales> getSalesByTaxPayerId(Integer taxPayerId) {
        return salesRepository.findSalesByTaxPayerId(taxPayerId);
    }






    // Endpoint
    public void printSale(Integer saleId){
        Sales sales = salesRepository.findSalesById(saleId);
    }


    // Endpoint 33
    public void calcuateTaxBySaleNumber(Integer salesNumber){

    }


    // Ali Ahmed Alshehri
    public void refundSale(Integer accountantId,Integer saleId){
        Accountant accountant = accountantRepository.findAccountantById(accountantId);
        if (accountant==null)
            throw new ApiException("cannot access you should be an accountant");
        Sales sales = salesRepository.findSalesById(saleId);
        if (sales==null)
            throw new ApiException("Sale not found ");
        if (sales.getSalesStatus().equals("Refunded"))
            throw new ApiException("This sale is already refunded");

        List<ItemSale> itemSale = (List<ItemSale>) sales.getItemSales();
        for (ItemSale is:itemSale){
            Product p = is.getProduct();
            p.setStock(p.getStock()+is.getQuantity());
            p.getItemSales().remove(is);
            productRepository.save(p);
        }

        sales.setSalesStatus("Refunded");
        salesRepository.save(sales);
    }



}
