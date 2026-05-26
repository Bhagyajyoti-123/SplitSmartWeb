package com.splitsmart.model;

public class SettlementDTO {
    private String from;
    private String to;
    private double amount;

    public SettlementDTO(String from, String to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public double getAmount() { return amount; }
}
