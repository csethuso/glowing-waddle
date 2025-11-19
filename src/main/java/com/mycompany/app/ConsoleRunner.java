package com.mycompany.app;

import dao.DaoFactory;
import dao.DBInit;
import dao.CustomerDAO;
import dao.AccountDAO;
import model.Customer;
import model.SavingsAccount;
import model.Account;

import java.util.List;

public class ConsoleRunner {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting console runner...");
        DBInit.init();

        CustomerDAO cdao = DaoFactory.getCustomerDAO();
        AccountDAO adao = DaoFactory.getAccountDAO();

        System.out.println("Creating customer Alice...");
        Customer alice = new Customer("Alice","1 Main St","555-0100");
        alice = cdao.create(alice);
        System.out.println("Created: " + alice);

        System.out.println("Creating savings account for Alice...");
        int generatedAccId = (int) (System.currentTimeMillis() % 1000000) + (int)(Math.random()*1000);
        Account acc = new SavingsAccount(generatedAccId, alice, 1000.0);
        try {
            adao.create(acc);
            System.out.println("Account created: " + acc);
        } catch (Exception e) {
            System.out.println("Account create/update failed (maybe already exists): " + e.getMessage());
        }

        System.out.println("All customers:");
        List<Customer> all = cdao.findAll();
        for (Customer c : all) System.out.println(" - " + c);

        System.out.println("All accounts:");
        List<Account> aList = adao.findAll();
        for (Account a : aList) System.out.println(" - " + a);

        System.out.println("Console runner finished.");
    }
}
