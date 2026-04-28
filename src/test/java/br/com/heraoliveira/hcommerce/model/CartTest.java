package br.com.heraoliveira.hcommerce.model;

import br.com.heraoliveira.hcommerce.exception.InvalidDataException;
import br.com.heraoliveira.hcommerce.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CartTest {

    @Test
    void shouldCalculateDiscountAndTotalUsingCurrentThresholds() {
        Cart cart = new Cart();

        cart.addItem(product(1L, "Notebook", "Portable computer", "300.00"), 2);
        CartPricing.PricingSummary pricingSummary = CartPricing.calculate(cart);

        assertEquals(new BigDecimal("600.00"), cart.calculateSubtotal());
        assertEquals(new BigDecimal("600.00"), pricingSummary.subtotal());
        assertEquals(new BigDecimal("15"), pricingSummary.discountPercentage());
        assertEquals(new BigDecimal("90.00"), pricingSummary.discountAmount());
        assertEquals(new BigDecimal("510.00"), pricingSummary.total());
    }

    @Test
    void shouldRespectDiscountThresholdsAtExactBoundaryValues() {
        Cart twoHundredCart = new Cart();
        twoHundredCart.addItem(product(1L, "Mouse", "Wireless mouse", "200.00"), 1);

        Cart fiveHundredCart = new Cart();
        fiveHundredCart.addItem(product(2L, "Monitor", "Full HD monitor", "500.00"), 1);

        Cart fifteenHundredCart = new Cart();
        fifteenHundredCart.addItem(product(3L, "Notebook", "Portable computer", "1500.00"), 1);

        CartPricing.PricingSummary twoHundredSummary = CartPricing.calculate(twoHundredCart);
        CartPricing.PricingSummary fiveHundredSummary = CartPricing.calculate(fiveHundredCart);
        CartPricing.PricingSummary fifteenHundredSummary = CartPricing.calculate(fifteenHundredCart);

        assertEquals(new BigDecimal("0"), twoHundredSummary.discountPercentage());
        assertEquals(new BigDecimal("0.00"), twoHundredSummary.discountAmount());
        assertEquals(new BigDecimal("200.00"), twoHundredSummary.total());

        assertEquals(new BigDecimal("10"), fiveHundredSummary.discountPercentage());
        assertEquals(new BigDecimal("50.00"), fiveHundredSummary.discountAmount());
        assertEquals(new BigDecimal("450.00"), fiveHundredSummary.total());

        assertEquals(new BigDecimal("15"), fifteenHundredSummary.discountPercentage());
        assertEquals(new BigDecimal("225.00"), fifteenHundredSummary.discountAmount());
        assertEquals(new BigDecimal("1275.00"), fifteenHundredSummary.total());
    }

    @Test
    void shouldMergeQuantitiesForTheSameProduct() {
        Cart cart = new Cart();
        Product notebook = product(1L, "Notebook", "Portable computer", "300.00");

        cart.addItem(notebook, 1);
        cart.addItem(notebook, 2);

        assertEquals(1, cart.getItems().size());
        assertEquals(3, cart.getItems().get(0).getQuantity());
    }

    @Test
    void shouldRemoveProductsById() {
        Cart cart = new Cart();
        cart.addItem(product(1L, "Notebook", "Portable computer", "300.00"), 1);

        cart.removeItem(1L);

        assertThrows(ProductNotFoundException.class, () -> cart.removeItem(1L));
    }

    @Test
    void shouldRejectUpdatingQuantityForANonExistentProduct() {
        Cart cart = new Cart();

        assertThrows(ProductNotFoundException.class, () -> cart.updateQuantity(99L, 2));
    }

    @Test
    void shouldRejectRemovingANonExistentProduct() {
        Cart cart = new Cart();

        assertThrows(ProductNotFoundException.class, () -> cart.removeItem(99L));
    }

    @Test
    void shouldRejectAddingNonPositiveQuantity() {
        Cart cart = new Cart();

        assertThrows(InvalidDataException.class, () -> cart.addItem(
                product(1L, "Notebook", "Portable computer", "300.00"),
                0
        ));
        assertThrows(InvalidDataException.class, () -> cart.addItem(
                product(1L, "Notebook", "Portable computer", "300.00"),
                -1
        ));
    }

    @Test
    void shouldRejectUpdatingToANonPositiveQuantity() {
        Cart cart = new Cart();
        cart.addItem(product(1L, "Notebook", "Portable computer", "300.00"), 1);

        assertThrows(InvalidDataException.class, () -> cart.updateQuantity(1L, 0));
        assertThrows(InvalidDataException.class, () -> cart.updateQuantity(1L, -1));
    }

    private Product product(long id, String name, String description, String price) {
        return new Product(id, name, description, new BigDecimal(price));
    }
}
