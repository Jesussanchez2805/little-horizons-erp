package com.littlehorizons.erp.model;

import java.math.BigDecimal;

/**
 * Domain entity representing a fee/billing concept (Concepto de Pago).
 * 
 * @author Jesús Sánchez (Jesussanchez2805)
 */
public class PaymentConcept {

    private int id;
    private String code;
    private String name;
    private String description;
    private BigDecimal baseAmount;
    private boolean recurring;
    private boolean active;

    public PaymentConcept() {
    }

    public PaymentConcept(int id, String code, String name, String description, BigDecimal baseAmount, boolean recurring, boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.baseAmount = baseAmount;
        this.recurring = recurring;
        this.active = active;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBaseAmount() { return baseAmount; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }

    public boolean isRecurring() { return recurring; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return String.format("[%s] %s - Base: $%,.2f %s", code, name, baseAmount != null ? baseAmount.doubleValue() : 0.0, recurring ? "(Monthly)" : "");
    }
}
