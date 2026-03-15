package br.com.heraoliveira.hcommerce.models;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;

import java.math.BigDecimal;

public class CartItem {
    private final Product product;
    private final int quantity;

    public CartItem(Product product, int quantity) {
        validateProduct(product);
        validateQuantity(quantity);

        this.product = product;
        this.quantity = quantity;
    }

    private static void validateProduct(Product product) {
        if (product == null) throw new InvalidDataException("Product cannot be null");
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0) throw new InvalidDataException("Quantity cannot be <= 0.");
    }

    public BigDecimal calculateSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return String.format("CartItem[product=%s, price=%s, quantity=%s, subtotal=%s]"
                , product.getName(), product.getPrice() , quantity, calculateSubtotal());
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}
