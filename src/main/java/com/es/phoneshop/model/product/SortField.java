package com.es.phoneshop.model.product;

public enum SortField {
    DESCRIPTION("description"),
    PRICE("price");

    private final String value;

    SortField(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static SortField fromValue(String value) {
        for (SortField field : values()) {
            if (field.getValue().equalsIgnoreCase(value)) {
                return field;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }
}
