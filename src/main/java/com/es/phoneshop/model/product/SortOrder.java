package com.es.phoneshop.model.product;

public enum SortOrder {
    ASC("asc"),
    DESC("desc");

    private final String value;

    SortOrder(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SortOrder fromValue(String value) {
        for (SortOrder order : values()) {
            if (order.getValue().equalsIgnoreCase(value)) {
                return order;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }
}
