package com.bank.model;
public abstract class Account {
    private String account_no;
    private String holder;
    protected double balance;
    private String acc_type;
    public Account(String account_no, String holder, Double balance, String acc_type) {
        this.account_no = account_no;
        this.holder = holder;
        this.balance = balance;
        this.acc_type = acc_type;
    }
    public String getAccountNo() {
        return account_no;
    }
    public String getHolder() {
        return holder;
    }
    public Double getBalance() {
        return balance;
    }
    public String getAccType() {
        return acc_type;
    }
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }
    public abstract void withdraw(double amount) throws Exception;
}