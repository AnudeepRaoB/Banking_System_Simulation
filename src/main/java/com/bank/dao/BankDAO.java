package com.bank.dao;

import com.bank.model.*;
import com.bank.util.DBUtil;
import java.sql.*;

public class BankDAO {
    public boolean login(String user, String pass) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user);
            pstmt.setString(2, pass);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Login Error: " + e.getMessage());
            return false;
        }
    }

    public Account getAccountByUsername(String username) {
        String sql = "SELECT a.* FROM Accounts a JOIN Users u ON a.account_no = u.account_no WHERE u.username = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String accNo = rs.getString("account_no");
                String holder = rs.getString("holder");
                double balance = rs.getDouble("balance");
                String type = rs.getString("acc_type");
                if ("SAVINGS".equals(type))
                    return new SavingsAcct(accNo, holder, balance);
                else
                    return new CurrentAcct(accNo, holder, balance);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching account: " + e.getMessage());
        }
        return null;
    }

    public boolean processTransaction(String accountNo, String type, double amount) {
        String updateSql = "UPDATE Accounts SET balance = balance + ? WHERE account_no = ?";
        String transSql = "INSERT INTO Transactions (account_no, trans_type, amount) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement uStmt = conn.prepareStatement(updateSql);
                    PreparedStatement tStmt = conn.prepareStatement(transSql)) {
                uStmt.setDouble(1, "WITHDRAWAL".equals(type) ? -amount : amount);
                uStmt.setString(2, accountNo);
                uStmt.executeUpdate();
                tStmt.setString(1, accountNo);
                tStmt.setString(2, type);
                tStmt.setDouble(3, amount);
                tStmt.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Transaction Error: " + e.getMessage());
            return false;
        }
    }

    public String performTransfer(String fromAcc, String toAcc, double amount) {
        if (fromAcc.equals(toAcc))
            return "Cannot transfer to your own account!";
        String checkSql = "SELECT holder FROM Accounts WHERE account_no = ?";
        try (Connection conn = DBUtil.getConnection()) {
            String recipientName = "";
            try (PreparedStatement cStmt = conn.prepareStatement(checkSql)) {
                cStmt.setString(1, toAcc);
                ResultSet rs = cStmt.executeQuery();
                if (rs.next()) {
                    recipientName = rs.getString("holder");
                } else {
                    return "Recipient account does not exist!";
                }
            }
            conn.setAutoCommit(false);
            String updateSql = "UPDATE Accounts SET balance = balance + ? WHERE account_no = ?";
            String transSql = "INSERT INTO Transactions (account_no, trans_type, amount, recipient_acc, recipient_name) VALUES (?, 'TRANSFER', ?, ?, ?)";
            try (PreparedStatement uStmt = conn.prepareStatement(updateSql);
                    PreparedStatement tStmt = conn.prepareStatement(transSql)) {
                uStmt.setDouble(1, -amount);
                uStmt.setString(2, fromAcc);
                if (uStmt.executeUpdate() == 0)
                    throw new SQLException("Insufficient balance or error");
                uStmt.setDouble(1, amount);
                uStmt.setString(2, toAcc);
                uStmt.executeUpdate();
                tStmt.setString(1, fromAcc);
                tStmt.setDouble(2, amount);
                tStmt.setString(3, toAcc);
                tStmt.setString(4, recipientName);
                tStmt.executeUpdate();
                tStmt.setString(1, toAcc);
                tStmt.setDouble(2, amount);
                tStmt.setString(3, fromAcc);
                tStmt.setString(4, "Transfer from " + fromAcc);
                tStmt.executeUpdate();
                conn.commit();
                return "SUCCESS";
            } catch (SQLException e) {
                conn.rollback();
                return "Database error: " + e.getMessage();
            }
        } catch (SQLException e) {
            return "Connection error: " + e.getMessage();
        }
    }

    public java.util.List<Transaction> getTransactionHistory(String accountNo) {
        java.util.List<Transaction> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE account_no = ? ORDER BY trans_time DESC";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNo);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Transaction(
                        rs.getInt("trans_id"),
                        rs.getString("account_no"),
                        rs.getString("trans_type"),
                        rs.getDouble("amount"),
                        rs.getString("recipient_acc"),
                        rs.getString("recipient_name"),
                        rs.getTimestamp("trans_time")));
            }
        } catch (SQLException e) {
            System.out.println("History Error: " + e.getMessage());
        }
        return list;
    }

    public boolean register(String user, String pass, String holder, double balance, String type) {
        String accNo = "ACC" + (int) (Math.random() * 900000 + 100000);
        String accountSql = "INSERT INTO Accounts (account_no, holder, balance, acc_type) VALUES (?, ?, ?, ?)";
        String userSql = "INSERT INTO Users (username, password, account_no) VALUES (?, ?, ?)";
        String transSql = "INSERT INTO Transactions (account_no, trans_type, amount) VALUES (?, 'DEPOSIT', ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement aStmt = conn.prepareStatement(accountSql);
                    PreparedStatement uStmt = conn.prepareStatement(userSql);
                    PreparedStatement tStmt = conn.prepareStatement(transSql)) {
                aStmt.setString(1, accNo);
                aStmt.setString(2, holder);
                aStmt.setDouble(3, balance);
                aStmt.setString(4, type);
                aStmt.executeUpdate();
                uStmt.setString(1, user);
                uStmt.setString(2, pass);
                uStmt.setString(3, accNo);
                uStmt.executeUpdate();
                tStmt.setString(1, accNo);
                tStmt.setDouble(2, balance);
                tStmt.executeUpdate();
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.out.println("Registration Error: " + e.getMessage());
            return false;
        }
    }
}