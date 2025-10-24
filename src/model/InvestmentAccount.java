package model;

public class InvestmentAccount extends Account {
    private static final double INTEREST_RATE = 0.05; // 5%
    private static final double MIN_OPEN_BALANCE = 500.0;

    public InvestmentAccount(int accountId, Customer owner, double balance) {
        super(accountId, owner, balance);
        if (balance < MIN_OPEN_BALANCE) {
            System.out.println("Warning: Minimum opening balance for Investment Account is 500.");
        }
    }

    @Override
    public void calculateInterest() {
        double interest = balance * INTEREST_RATE;
        balance += interest;
        System.out.println("Interest added: " + interest + ". New balance: " + balance);
    }
}
