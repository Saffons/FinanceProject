package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class Database {
    public static Connection connect() throws SQLException {
        String password = "OVDBNheKxTkIj75_6AsufifnNGkAyxBq";
        String username = "dljkyjsg";
        String url = "jdbc:postgresql://hattie.db.elephantsql.com:5432/dljkyjsg";
        return DriverManager.getConnection(url, username, password);
    }

    public static int addNewUser() {
        String SQL = "INSERT INTO Users(PLN_Balance, USD_BALANCE, EUR_BALANCE) "
                + "VALUES(?,?,?)";

        int id = 0;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDouble(1, 0.00);
            pstmt.setDouble(2, 0.00);
            pstmt.setDouble(3, 0.00);

            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getInt(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return id;
    }

    public static ArrayList<Integer> getUserList() {
        ResultSet resultSet = null;
        ArrayList<Integer> arr = new ArrayList<>();

        try (Connection conn = connect();
             Statement statement = conn.createStatement()) {

            // Create and execute a SELECT SQL statement.
            String selectSql = "SELECT UserID from users;";
            resultSet = statement.executeQuery(selectSql);

            // Print results from select statement
            while (resultSet.next()) {
                arr.add(resultSet.getInt(1));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return arr;
    }

    public static void transactionLog(String transactionType, String currency, String currencyTo, double value, int fromUserID, Integer toUserID) {
        String SQL = "INSERT INTO TransactionHistory(TransactionType, fromID, toID, currency, currencyTo, exchangeRate, amount, provision, transactionDate) "
                + "VALUES(?,?,?,?,?,?,?,?,?)";

        String rate = currency + currencyTo;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, transactionType);
            pstmt.setInt(2, fromUserID);
            if (toUserID != null) pstmt.setInt(3, toUserID);
            else pstmt.setNull(3, Types.INTEGER);
            pstmt.setString(4, currency);
            if (currency != null) pstmt.setString(5, currencyTo);
            else pstmt.setNull(5, Types.VARCHAR);
            if (Objects.equals(transactionType, "EXC")) pstmt.setDouble(6, Init.rates.get(rate));
            else pstmt.setNull(6, Types.NUMERIC);
            pstmt.setDouble(7, value);
            pstmt.setDouble(8, Init.commission);
            pstmt.setDate(9, new java.sql.Date(Calendar.getInstance().getTime().getTime()));

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void getHistoryDate(Date start, Date end) {
        String SQL = "SELECT * FROM TransactionHistory WHERE transactionDate BETWEEN ? AND ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDate(1, start);
            pstmt.setDate(2, end);

            printResultSet(pstmt);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void getHistoryType(String type) {
        String SQL = "SELECT * FROM TransactionHistory WHERE transactionType = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, type);

            printResultSet(pstmt);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void getHistoryCurrency(String currency) {
        String SQL = "SELECT * FROM TransactionHistory WHERE currency = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, currency);

            printResultSet(pstmt);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void getUsersBalance(int userID) {
        String SQL = "SELECT * FROM Users WHERE userID = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, userID);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("ID, PLN, USD, EUR");
            while (rs.next()) {
                System.out.println(rs.getInt(1) + "  " + rs.getDouble(2) + "  " + rs.getDouble(3) +
                        "  " + rs.getDouble(4));
            }
            System.out.println();
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void getUsersHistory(int userID) {
        String SQL = "SELECT * FROM TransactionHistory WHERE fromID = ? OR toID = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, userID);
            pstmt.setInt(2, userID);

            printResultSet(pstmt);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void printResultSet(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();

        System.out.println("TYPE, FROM_ID, TO_ID, FROM_CUR, TO_CUR, RATE, AMOUNT, COMMISSION, DATE");
        while (rs.next()) {
            System.out.println(rs.getString(2) + "   " + rs.getInt(3) + "       " + rs.getInt(4) +
                    "     " + rs.getString(5) + "         " + rs.getString(6) + "    " + rs.getDouble(7) +
                    "    " + rs.getDouble(8) + "     " + rs.getDouble(9) + "    " + rs.getDate(10));
        }
        System.out.println();
    }

    public static void getCommissionByType(String transactionType) {
        String SQL = "SELECT SUM(provision*amount) FROM TransactionHistory WHERE transactionType = ?";

        print(transactionType, SQL);
    }

    public static void getCommissionByCurrency(String currency) {
        String SQL = "SELECT SUM(provision*amount) FROM TransactionHistory WHERE currency = ?";

        print(currency, SQL);
    }

    private static void print(String currency, String SQL) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(SQL,
                     Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, currency);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getString(1) + " " + currency);
            }
            System.out.println();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
