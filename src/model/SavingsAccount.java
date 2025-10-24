package model;

public class SavingsAccount extends Account {
    private static final double INTEREST_RATE = 0.05; // 5%

    public SavingsAccount(int accountId, Customer owner, double balance) {
        super(accountId, owner, balance);
    }

    @Override
    public void calculateInterest() {
        double interest = balance * INTEREST_RATE;
        balance += interest;
        System.out.println("Interest added: " + interest + ". New balance: " + balance);
    }

    @Override
    public boolean withdraw(double amount) {
        System.out.println("Withdrawals are not allowed from Savings Account.");
        return false;
    }
}
