package com.camt.th.posowner.Model;

import java.util.Date;
public class Finance {
    int id;
    String topsell;
    double revenue;
    double profit;
    double expense;
    Date date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopsell() {
        return topsell;
    }

    public void setTopsell(String top_sell) {
        this.topsell = top_sell;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public double getExpense() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
