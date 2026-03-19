package br.com.heraoliveira.hcommerce.models;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart {
    private final List<CartItem> items = new ArrayList<>();

    private static void validateProduct(Product product) {
        if (product == null) throw new InvalidDataException("Product is null");
    }

    public void addItem(Product product, int quantity) {
        validateProduct(product);
        if (quantity <= 0) throw new InvalidDataException("Quantity cannot be <= 0.");

        var optionalCartItem = items.stream().filter(item -> item.getProduct().equals(product)).findFirst();

        if (optionalCartItem.isPresent()) {
            optionalCartItem.get().addQuantity(quantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
    }

    public boolean removeItem(Product product) {
        validateProduct(product);
        return items.removeIf(item -> item.getProduct().equals(product));
    }

    public boolean updateQuantity(Product product, int quantity) {
        validateProduct(product);
        if (quantity < 0) throw new InvalidDataException("Quantity cannot be < 0.");

        var optionalCartItem = items.stream().filter(item -> item.getProduct().equals(product)).findFirst();

        if (optionalCartItem.isPresent()) {
            if (quantity == 0) {
                return items.remove(optionalCartItem.get());
            } else {
                optionalCartItem.get().updateQuantity(quantity);
                return true;
            }
        } else  {
            return false;
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
