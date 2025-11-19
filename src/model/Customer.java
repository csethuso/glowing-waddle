package model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private static int NEXT_ID = 1;

    private int customerId;
    private String name;
    private String address;
    private String phone;
    private List<Account> accounts;

    public Customer(String name, String address, String phone) {
        this.customerId = NEXT_ID++;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.accounts = new ArrayList<>();
    }

    public Customer(int customerId, String name, String address, String phone) {
        this.customerId = customerId;
        if (customerId >= NEXT_ID) {
            NEXT_ID = customerId + 1;
        }
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "Customer{" + "id=" + customerId + ", name='" + name + '\'' + '}';
    }
}
