package org.example;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Init {
    //Methods
    Init() {
        try {
            setProvision();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        try {
            setRates();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        userList = new ArrayList<>();
        userList = Database.getUserList();

        mainMenu();
    }

    public void setProvision() throws NumberFormatException {
        while (true) {
            System.out.println("Provide a commission between 0 and 1");
            Scanner sc = new Scanner(System.in);
            try {
                double value = Double.parseDouble(sc.next());
                if (value < 0 || value > 1) {
                    throw new NumberFormatException("Invalid commission set");
                }

                commission = value;
                System.out.println("Commission set to " + value * 100 + "%.");
                break;
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void setRates() throws NumberFormatException {
        String[] strings = {"PLNEUR", "EURUSD", "PLNUSD", "EURPLN", "USDEUR", "USDPLN"};
        rates = new HashMap<>();
        int i = 0;
        while (i < 3) {
            System.out.println("Provide an exchange rate for " + strings[i]);
            Scanner sc = new Scanner(System.in);
            try {
                double value = Double.parseDouble(sc.next());
                if (value < 0) {
                    throw new NumberFormatException("Invalid rates set");
                }
                rates.put(strings[i], value);
                rates.put(strings[i + 3], 1.0 / value);
                System.out.println(strings[i] + " rates set to: " + value);
                i++;
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("App initialized successfully!");
    }

    public void mainMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("1 - Log in using your id\n2 - Register a new user\n3 - Get your balances' info\n" +
                    "4 - Get commission history by type\n5 - Get commission history by currency\n6 - Get transaction history by date\n" +
                    "7 - Get transaction history by transaction type\n8 - Get transaction history by currency\n" +
                    "9 - Deposit\n10 - Withdraw\n11 - Transfer\n12 - Exchange\n0 - Exit\n");
            try {
                int val = Integer.parseInt(sc.next());
                switch (val) {
                    case 1:
                        System.out.println("Provide your account id");
                        int id = Integer.parseInt(sc.next());
                        if (userList.contains(id)) {
                            currentLogin = id;
                            System.out.println("Logged in!");
                        }
                        continue;
                    case 2:
                        int newID = Database.addNewUser();
                        System.out.println("Account created successfully, your new ID is " + newID);
                        continue;
                    case 3:
                        if (currentLogin == 0) {
                            System.out.println("Please log in first!");
                            continue;
                        }

                        Database.getUsersBalance(currentLogin);
                        Database.getUsersHistory(currentLogin);
                        continue;
                    case 4:
                        System.out.println("Provide transaction type - for deposit - DEP, withdraw - WIT " +
                                "transfer - TRA, exchange - EXC");
                        String type = sc.next();
                        if (type.matches("DEP|WIT|TRA|EXC")) {
                            Database.getCommissionByType(type);
                        }

                        continue;
                    case 5:
                        System.out.println("Provide currency - PLN, USD or EUR");
                        String cur = sc.next();
                        if (cur.matches("PLN|USD|EUR")) {
                            Database.getCommissionByCurrency(cur);
                        }

                        continue;
                    case 6:
                        System.out.println("Write a start date yyyy-mm-dd");
                        try {
                            Date start = Date.valueOf(sc.next());
                            System.out.println("Write an end date yyyy-mm-dd");
                            try {
                                Date end = Date.valueOf(sc.next());

                                Database.getHistoryDate(start, end);
                            } catch (IllegalArgumentException ex) {
                                System.out.println(ex.getMessage());
                            }
                        } catch (IllegalArgumentException ex) {
                            System.out.println(ex.getMessage());
                        }
                        continue;
                    case 7:
                        System.out.println("Provide transaction type - for deposit - DEP, withdraw - WIT " +
                                "transfer - TRA, exchange - EXC");
                        type = sc.next();

                        Database.getHistoryType(type);

                        continue;
                    case 8:
                        System.out.println("Provide currency - PLN, USD or EUR");
                        String currency = sc.next();

                        Database.getHistoryCurrency(currency);

                        continue;
                    case 9:
                        if (currentLogin == 0) {
                            System.out.println("Please log in first!");
                            continue;
                        }
                        System.out.println("Provide currency - PLN, USD or EUR");
                        String curr = sc.next();
                        System.out.println("Provide amount");
                        double amount = sc.nextDouble();

                        if (curr.matches("EUR|USD|PLN") && amount > 0) {
                            Database.transactionLog("DEP", curr, null, amount, currentLogin, null);
                            System.out.println("Action completed successfully!");
                        }

                        continue;
                    case 10:
                        if (currentLogin == 0) {
                            System.out.println("Please log in first!");
                            continue;
                        }
                        System.out.println("Provide currency - PLN, USD or EUR");
                        curr = sc.next();
                        System.out.println("Provide amount");
                        amount = sc.nextDouble();

                        if (curr.matches("EUR|USD|PLN") && amount > 0) {
                            Database.transactionLog("WIT", curr, null, amount, currentLogin, null);
                            System.out.println("Action completed successfully!");
                        }

                        continue;
                    case 11:
                        if (currentLogin == 0) {
                            System.out.println("Please log in first!");
                            continue;
                        }
                        System.out.println("Provide currency - PLN, USD or EUR");
                        curr = sc.next();
                        System.out.println("Provide amount");
                        amount = sc.nextDouble();
                        System.out.println("Provide recipient's userID");
                        int toID = sc.nextInt();

                        if (curr.matches("EUR|USD|PLN") && amount > 0 && userList.contains(toID) && toID!=currentLogin) {
                            Database.transactionLog("TRA", curr, null, amount, currentLogin, toID);
                            System.out.println("Action completed successfully!");
                        }

                        continue;
                    case 12:
                        if (currentLogin == 0) {
                            System.out.println("Please log in first!");
                            continue;
                        }
                        System.out.println("Provide currency - PLN, USD or EUR");
                        curr = sc.next();
                        System.out.println("Provide amount");
                        amount = sc.nextDouble();
                        System.out.println("Provide currency to exchange to");
                        String newCurrency = sc.next();

                        if (curr.matches("EUR|USD|PLN") && newCurrency.matches("EUR|USD|PLN") && amount > 0) {
                            Database.transactionLog("EXC", curr, newCurrency, amount, currentLogin, null);
                            System.out.println("Action completed successfully!");
                        }

                        continue;
                    case 0:
                        System.out.println("Exit!");
                        System.exit(0);

                    default:
                        System.out.println();
                }
            } catch (NumberFormatException ex) {
                System.out.println("Choose from available numbers only. Returning.");
            }
        }
    }

    //Fields
    private ArrayList<Integer> userList;
    static double commission;
    static HashMap<String, Double> rates;
    static int currentLogin = 0;

}
