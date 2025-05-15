package com.example.final_project.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TaxReports {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(columnDefinition = "double not null")
    @PositiveOrZero(message = "Total tax must be zero or positive")
    private Double totalTax;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start_date;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end_date;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;


    @NotEmpty(message = "satatus of tax report must be not empty")
    @Pattern(regexp = "Pending|Approved|Paid|Unpaid|Under Legal Action|Rejected")
    private String status;


    @OneToOne(cascade = CascadeType.ALL,mappedBy = "taxReports")
    @PrimaryKeyJoinColumn
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "business_id",referencedColumnName = "id")
    @JsonIgnore
    private Business business;

    @ManyToOne
    @JoinColumn(name = "audit_id",referencedColumnName = "id")
    @JsonIgnore
    private Auditor auditor;


    @OneToMany(mappedBy = "taxReports")
    private Set<Sales> sales;
}
