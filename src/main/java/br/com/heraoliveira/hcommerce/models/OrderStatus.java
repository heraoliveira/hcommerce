package br.com.heraoliveira.hcommerce.models;

public enum OrderStatus {
    CREATED {
        @Override
        public OrderStatus finalizeOrder() {
            return OrderStatus.COMPLETED;
        }

        @Override
        public OrderStatus cancelOrder() {
            return OrderStatus.CANCELED;
        }
    },
    COMPLETED,
    CANCELED;

    public OrderStatus finalizeOrder() {
        throw new IllegalStateException("Order status cannot be finalized.");
    }

    public OrderStatus cancelOrder() {
        throw new IllegalStateException("Order status cannot be cancelled.");
    }
}
