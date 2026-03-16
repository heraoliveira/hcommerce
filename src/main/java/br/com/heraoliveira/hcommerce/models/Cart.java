package br.com.heraoliveira.hcommerce.models;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.exception.ProductNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart {
    private final List<CartItem> items = new ArrayList<>();

    public void addItem(CartItem item) {
        validateCartItem(item);
        var optionalItem = items.stream().filter(i -> i.equals(item)).findFirst();

        if (optionalItem.isPresent()) {
            optionalItem.get().addQuantity(item.getQuantity());
        } else {
            items.add(item);
        }
    }

    private static void validateCartItem(CartItem item) {
        if (item == null) throw new InvalidDataException("CartItem cannot be null.");
    }

    public boolean removeItem(CartItem item) {
        validateCartItem(item);
        return items.remove(item);
    }

    public void updateQuantity(CartItem item, int quantity) {
        validateCartItem(item);
        if (quantity < 0) throw new InvalidDataException("Quantity cannot be < 0.");

        var optionalItem = items.stream().filter(i -> i.equals(item)).findFirst();
        if (optionalItem.isPresent()) {
            if (quantity == 0) {
                removeItem(item);
            }  else {
                optionalItem.get().updateQuantity(quantity);
            }
        } else  {
            throw new ProductNotFoundException("Product not found.");
        }
    }

    public BigDecimal calculateTotal() {
        return items.stream().map(i -> i.calculateSubtotal())
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b)) ;
    }

    public BigDecimal applyDiscount(BigDecimal discount) {
        if (discount == null || discount.compareTo(BigDecimal.ZERO) <= 0 || discount.compareTo(BigDecimal.valueOf(100)) > 0)
            throw new InvalidDataException("Discount is invalid.");

        var oldTotal = calculateTotal();
        var multiplier = discount.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_EVEN);
        var valueDiscount = oldTotal.multiply(multiplier);

        return oldTotal.subtract(valueDiscount);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
