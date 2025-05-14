package com.example.final_project.Config;

import com.example.final_project.Service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class ConfigurationSecurity {

    private final MyUserDetailsService myUserDetailsService;

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(myUserDetailsService);
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());

        return authenticationProvider;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                .authenticationProvider(daoAuthenticationProvider())
                .authorizeHttpRequests()
                .requestMatchers(
                        "/api/v1/tax-payer/tax-payer-register","/api/v1/moyasar-payment/pay-tax/tax-report/{{taxReportId}}","/api/v1/moyasar-payment/callback")
                .permitAll()
                .requestMatchers("/api/v1/user/update")
                .hasAnyAuthority("ADMIN","AUDITOR","TAXPAYER","ACCOUNTANT")
                .requestMatchers(
                        "/api/v1/business/add-business/",
                        "/api/v1/branch/add/",
                        "/api/v1/tax-payer/add-accountant/",
                        "/api/v1/accountant/assign-accountant-to-branch/",
//                        "/api/v1/moyasar-payment/pay-tax/tax-report",
                        "/api/v1/tax-payer/tax-payer-register",
                        "/api/v1/tax-payer/update",
                        "/api/v1/tax-payer/delete",
                        "/api/v1/tax-payer/activate-accountant",
                        "/api/v1/tax-payer/de-activate-accountant",
                        "/api/v1/tax-payer/block-inactive-accountant",
                        "/api/v1/business/get-my-business",
                        "/api/v1/business/get-number-of-branches",
                        "/api/v1/business/get-my-businesses",
                        "/api/v1/business/add-business",
                        "/api/v1/business/update",
                        "/api/v1/business/delete",
                        "/api/v1/business/sales-business",
                        "/api/v1/business/business-revenue",
                        "/api/v1/branch/branch-revenue/",
                        "/api/v1/accountant/get-accountant-by-branch/",
                        "/api/v1/accountant/get-accountant-by-business/")  //20
                .hasAuthority("TAXPAYER")
                .requestMatchers(
                        "/api/v1/product/add/",
                        "/api/v1/accountant/update",
                        "/api/v1/accountant/delete",
                        "/api/v1/accountant/restock-product",
                        "/api/v1/counterbox/open",
                        "/api/v1/counterbox/create",
                        "/api/v1/counterbox/get-all",
                        "/api/v1/counterbox/get",
                        "/api/v1/counterbox/update",
                        "/api/v1/counterbox/delete",
                        "api/v1/counterbox/close-opened-counter-box",
                        "/api/v1/sales/add-sale/{boxId}",
                        "/api/v1/sales/add-product-in-sale",
                        "/api/v1/item-sales/remove",
                        "/api/v1/sales/update-quantity",
                        "/api/v1/sales/confirm-sale" ,
                        "/api/v1/sales/print-sale",
                      "/api/v1/product/add-to-branch" +
                              "/api/v1/product/get" +
                              "/api/v1/product/update",
                             "/api/v1/product/delete",
                            "/api/v1/product/barcode",
                        "/api/v1/sales/refund-sale/{{saleId}}")  // 21
                .hasAuthority("ACCOUNTANT")
                .requestMatchers(
                        "/api/v1/taxReports/add",
                        "/api/v1/taxReports/get",
                        "/api/v1/taxReports/update",
                        "/api/v1/taxReports/delete",
                        "/api/v1/taxReports/apply-penalty",
                        "/api/v1/taxReports/apply-2month-penalty",
                        "/api/v1/taxReports/apply-legal-action",
                        "/api/v1/taxReports/due-payment",
                        "/api/v1/taxReports/reports",
                        "/api/v1/taxReports/report-count",
                        "/api/v1/taxReports/approval-rate",
                        "/api/v1/taxReports/bulk-approve",
                        "/api/v1/taxReports/latest-report",
                        "/api/v1/taxReports/unapproved",
                        "/api/v1/auditor/create-tax-report",
                        "/api/v1/auditor/activate-business/{taxPayerId}/{businessId}",
                        "/api/v1/auditor/approve-tax-report",
                        "/api/v1/auditor/reject-tax-report",
                        "/api/v1/tax-payer/get-all-tax-payers",
                        "/api/v1/business/get-all-business")//20+ get all TPs
                .hasAuthority("AUDITOR")
                .requestMatchers(
                        "/api/v1/taxReports/reports/{auditorId}",
                        "/api/v1/taxReports/approval-rate/{auditorId}",
                         "/api/v1/admin/activate/",
                        "/api/v1/auditor/add")
                .hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutUrl("/api/v1/user/logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .and()
                .httpBasic();

        return httpSecurity.build();
    }

}
