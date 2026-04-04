package br.com.heraoliveira.hcommerce.repository;

import br.com.heraoliveira.hcommerce.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private final List<Product> products = new ArrayList<>();

    public long nextId() {
        return products.stream().mapToLong(Product::getId).max().orElse(0L) + 1;
    }

    public void save(Product product) {
        products.add(product);
    }

    public List<Product> findAll() {
        return List.copyOf(products);
    }
}