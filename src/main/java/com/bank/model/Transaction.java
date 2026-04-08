package com.bank.model;
import java.sql.Timestamp;
public class Transaction {
    private int transId;
    private String accountNo;
    private String transType;
    private double amount;
    private String recipientAcc;
    private String recipientName;
    private Timestamp transTime;
    public Transaction(int transId, String accountNo, String transType, double amount,
            String recipientAcc, String recipientName, Timestamp transTime) {
        this.transId = transId;
        this.accountNo = accountNo;
        this.transType = transType;
        this.amount = amount;
        this.recipientAcc = recipientAcc;
        this.recipientName = recipientName;
        this.transTime = transTime;
    }
    public int getTransId() {
        return transId;
    }
    public String getAccountNo() {
        return accountNo;
    }
    public String getTransType() {
        return transType;
    }
    public double getAmount() {
        return amount;
    }
    public String getRecipientAcc() {
        return recipientAcc;
    }
    public String getRecipientName() {
        return recipientName;
    }
    public Timestamp getTransTime() {
        return transTime;
    }
}