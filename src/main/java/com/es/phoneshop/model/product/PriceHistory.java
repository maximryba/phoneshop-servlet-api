package com.es.phoneshop.model.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

public class PriceHistory implements Serializable {
    private Long id;
    private BigDecimal price;
    private LocalDate startDate;
    private Currency currency;

    public PriceHistory(Long id, BigDecimal price, LocalDate startDate, Currency currency) {
        this.id = id;
        this.price = price;
        this.startDate = startDate;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
