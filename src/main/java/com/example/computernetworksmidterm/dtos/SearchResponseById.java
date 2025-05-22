package com.example.computernetworksmidterm.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class SearchResponseById{
    private String message;
    private AccountInfo accountInfo;
}
