package model;

public class ChequeAccount extends Account {
    private String employerName;

    public ChequeAccount(int accountId, Customer owner, double balance, String employerName) {
        super(accountId, owner, balance);
        this.employerName = employerName;
    }

    public String getEmployerName() {
        return employerName;
    }

    @Override
    public void calculateInterest() {
        System.out.println("Cheque accounts do not earn interest.");
    }
}
