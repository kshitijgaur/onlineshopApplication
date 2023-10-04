package com.kshitij.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {//good to seperate model and dto's,better not to expose models outside
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
}
