package com.example.final_project.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoyasarPayment {

        private String name;
        private String number;
        private String cvc;
        private String month;
        private String year;
        private String description;
        private String postalCode;
        private String country;
        private Integer taxPayerId;


}
