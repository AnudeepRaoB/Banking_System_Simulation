package com.bank.model;
public class CurrentAcct extends Account {
    private static final double limit = 10000.0;
    public CurrentAcct(String account_no, String holder, double balance) {
        super(account_no, holder, balance, "CURRENT");
    }
    @Override
    public void withdraw(double amount) throws Exception {
        if (this.balance - amount < -limit) {
            throw new Exception("Transaction Denied: Limit Exceeded");
        }
        this.balance -= amount;
    }
}