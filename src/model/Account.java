package model;

public abstract class Account {
    protected int accountId;
    protected double balance;
    protected Customer owner;

    public Account(int accountId, Customer owner, double balance) {
        this.accountId = accountId;
        this.owner = owner;
        this.balance = balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public double getBalance() {
        return balance;
    }

    public Customer getOwner() {
        return owner;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposit successful. New balance: " + balance);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid withdrawal amount.");
            return false;
        } else if (amount > balance) {
            System.out.println("Insufficient funds.");
            return false;
        } else {
            balance -= amount;
            System.out.println("Withdrawal successful. New balance: " + balance);
            return true;
        }
    }

    public void updateBalance(double amount) {
        balance += amount;
    }

    public abstract void calculateInterest();

    @Override
    public String toString() {
        return "Account ID: " + accountId + ", Owner: " + owner.getName() + ", Balance: " + balance;
    }
}
