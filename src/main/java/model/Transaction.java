package model;

import java.time.LocalDateTime;

public class Transaction {

    private int id;
    private int accountId;
    private String type;          // e.g. DEPOSIT, WITHDRAW, TRANSFER_IN, TRANSFER_OUT
    private double amount;
    private String description;
    private LocalDateTime date;

    // === Constructors ===
    public Transaction() {}

    public Transaction(int accountId, String type, double amount, String description, LocalDateTime date) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public Transaction(int id, int accountId, String type, double amount, String description, LocalDateTime date) {
        this.id = id;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    // === Getters and Setters ===
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    // === Convenience Methods ===
    @Override
    public String toString() {
        return String.format("[%s] %.2f - %s (%s)", type, amount, description, date);
    }
}
