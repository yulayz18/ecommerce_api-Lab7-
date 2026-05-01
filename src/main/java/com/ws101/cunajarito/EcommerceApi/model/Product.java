package com.ws101.cunajarito.EcommerceApi.model;

import lombok.*;
import jakarta.validation.constraints.*;

@NotBlank
private String name;

@Positive
private double price;

@NotBlank
private String category;

@Min(0)
private int stockQuantity;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private Long id;
    private String name;
    private String description;
    private double price;
    private String category;
    private int stockQuantity;
    private String imageUrl;
}