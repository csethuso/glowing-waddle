package model;

import java.util.ArrayList;

public class Bank {
    private String bankName;
    private String branchCode;
    private ArrayList<Customer> customers;
    private ArrayList<Account> accounts;

    public Bank(String bankName, String branchCode) {
        this.bankName = bankName;
        this.branchCode = branchCode;
        this.customers = new ArrayList<>();
        this.accounts = new ArrayList<>();
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        System.out.println("Customer added: " + customer.getName());
    }

    public Customer findCustomer(int customerId) {
        for (Customer c : customers) {
            if (c.getCustomerId() == customerId)
                return c;
        }
        return null;
    }

    public void addAccount(Account account) {
        accounts.add(account);
        System.out.println("Account added for: " + account.getOwner().getName());
    }

    public Account findAccount(int accountId) {
        for (Account a : accounts) {
            if (a.getAccountId() == accountId)
                return a;
        }
        return null;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBranchCode() {
        return branchCode;
    }
}
