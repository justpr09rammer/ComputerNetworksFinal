package com.example.computernetworksmidterm.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeactivateAccountResponse {
    private String message;
    private AccountInfo accountInfo;
}
