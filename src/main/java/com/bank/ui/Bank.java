package com.bank.ui;

import com.bank.dao.BankDAO;
import com.bank.model.*;
import io.javalin.Javalin;

public class Bank {
    public static void main(String[] args) {
        BankDAO dao = new BankDAO();
        var app = Javalin.create(config -> {
            config.staticFiles.add("/static");
        }).start(8081);
        app.get("/", ctx -> ctx.redirect("/index.html"));
        app.post("/login", ctx -> {
            String user = ctx.formParam("username");
            String pass = ctx.formParam("password");
            if (dao.login(user, pass)) {
                ctx.sessionAttribute("currentUser", user);
                ctx.status(200).result("Login Successful");
            } else {
                ctx.status(401).result("Login Failed");
            }
        });
        app.post("/register", ctx -> {
            String user = ctx.formParam("username");
            String pass = ctx.formParam("password");
            String holder = ctx.formParam("holder");
            double balance = Double.parseDouble(ctx.formParam("balance"));
            String type = ctx.formParam("acc_type");
            if (dao.register(user, pass, holder, balance, type)) {
                ctx.status(201).result("Registration Successful");
            } else {
                ctx.status(400).result("Registration Failed: Username might be taken");
            }
        });
        app.get("/api/account", ctx -> {
            String user = ctx.sessionAttribute("currentUser");
            if (user == null) {
                ctx.status(401);
                return;
            }
            Account acc = dao.getAccountByUsername(user);
            if (acc != null)
                ctx.json(acc);
            else
                ctx.status(404);
        });
        app.post("/api/deposit", ctx -> {
            String user = ctx.sessionAttribute("currentUser");
            if (user == null) {
                ctx.status(401);
                return;
            }
            Account acc = dao.getAccountByUsername(user);
            double amount = Double.parseDouble(ctx.formParam("amount"));
            if (dao.processTransaction(acc.getAccountNo(), "DEPOSIT", amount)) {
                ctx.status(200).result("Deposit Successful");
            } else {
                ctx.status(500).result("Deposit Failed");
            }
        });
        app.post("/api/withdraw", ctx -> {
            String user = ctx.sessionAttribute("currentUser");
            if (user == null) {
                ctx.status(401);
                return;
            }
            Account acc = dao.getAccountByUsername(user);
            double amount = Double.parseDouble(ctx.formParam("amount"));
            try {
                acc.withdraw(amount);
                if (dao.processTransaction(acc.getAccountNo(), "WITHDRAWAL", amount)) {
                    ctx.status(200).result("Withdrawal Successful");
                } else {
                    ctx.status(500).result("Withdrawal Failed");
                }
            } catch (Exception e) {
                ctx.status(400).result(e.getMessage());
            }
        });
        app.post("/api/transfer", ctx -> {
            String user = ctx.sessionAttribute("currentUser");
            if (user == null) {
                ctx.status(401);
                return;
            }
            Account fromAcc = dao.getAccountByUsername(user);
            String toAccNo = ctx.formParam("toAccount");
            double amount = Double.parseDouble(ctx.formParam("amount"));
            try {
                fromAcc.withdraw(amount);
                String result = dao.performTransfer(fromAcc.getAccountNo(), toAccNo, amount);
                if ("SUCCESS".equals(result)) {
                    ctx.status(200).result("Transfer Successful");
                } else {
                    ctx.status(400).result(result);
                }
            } catch (Exception e) {
                ctx.status(400).result(e.getMessage());
            }
        });
        app.get("/api/history", ctx -> {
            String user = ctx.sessionAttribute("currentUser");
            if (user == null) {
                ctx.status(401);
                return;
            }
            Account acc = dao.getAccountByUsername(user);
            ctx.json(dao.getTransactionHistory(acc.getAccountNo()));
        });
        app.post("/logout", ctx -> {
            ctx.consumeSessionAttribute("currentUser");
            ctx.redirect("/index.html");
        });
        System.out.println("Server started at http://localhost:8081");
    }
}