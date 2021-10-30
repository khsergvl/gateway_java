package com.accenture.pay.gateway.model;

import javax.validation.constraints.DecimalMin;
import java.util.Objects;

public class Payment {

    private Integer from;
    private Integer to;
    @DecimalMin(value = "0.01", message = "The minimum transfer value is 0.01")
    private Double amount;

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return from.equals(payment.from) &&
                to.equals(payment.to) &&
                amount.equals(payment.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, amount);
    }
}
