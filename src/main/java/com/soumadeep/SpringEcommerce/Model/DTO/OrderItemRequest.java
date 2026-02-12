package com.soumadeep.SpringEcommerce.Model.DTO;

import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
        @Positive
        int productId,
        @Positive(message = "Quantity must be greater than zero")int quantity){
}