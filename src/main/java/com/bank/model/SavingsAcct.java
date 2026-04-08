package com.bank.model;
public class SavingsAcct extends Account {
    private static final double min_bal = 5000;
    public SavingsAcct(String account_no, String holder, double balance) {
        super(account_no, holder, balance, "SAVINGS");
    }
    @Override
    public void withdraw(double amount) throws Exception {
        if (this.balance - amount < min_bal) {
            throw new Exception("Insufficient Funds: Minimum Balance of 5000 must be maintained");
        }
        this.balance -= amount;
    }
}