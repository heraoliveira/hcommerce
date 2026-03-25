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
        throw new IllegalStateException("Business Error: Cannot finalize an order that is already " +
                "finalized or canceled.");
    }

    public OrderStatus cancelOrder() {
        throw new IllegalStateException("Business Error: Cannot cancel an order that is already " +
                "finalized or canceled.");
    }
}