package com.jel.spys.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinanceUserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String credentialNumber;
    private BigDecimal balance;
    private BigDecimal movementDay;
    private BigDecimal movementWeek;
    private BigDecimal movementMonth;
    private BigDecimal movementYTD;
    private List<TransactionDTO> transactions;
}
