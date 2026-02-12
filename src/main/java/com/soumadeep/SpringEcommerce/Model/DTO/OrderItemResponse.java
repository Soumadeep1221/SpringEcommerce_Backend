package com.soumadeep.SpringEcommerce.Model.DTO;

import java.math.BigDecimal;

public record OrderItemResponse(String productName, int quantity, BigDecimal totalPrice) {
}
