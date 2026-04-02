package br.com.heraoliveira.hcommerce.service;

import br.com.heraoliveira.hcommerce.models.Cart;

import java.math.BigDecimal;

public class CartService {

    private static final BigDecimal DISCOUNT_THRESHOLD = new BigDecimal("200.00");
    private static final BigDecimal DISCOUNT_PERCENTAGE = new BigDecimal("10");

    public void recalculateDiscount(Cart cart) {
        if (cart.calculateSubtotal().compareTo(DISCOUNT_THRESHOLD) > 0) {
            cart.applyDiscount(DISCOUNT_PERCENTAGE);
        } else {
            cart.clearDiscount();
        }
    }
}