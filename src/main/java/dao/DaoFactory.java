package dao;

public class DaoFactory {
    private static final H2CustomerDAO customerDAO = new H2CustomerDAO();
    private static final H2AccountDAO accountDAO = new H2AccountDAO();

    public static H2CustomerDAO getCustomerDAO() {
        return customerDAO;
    }

    public static H2AccountDAO getAccountDAO() {
        return accountDAO;
    }
}
