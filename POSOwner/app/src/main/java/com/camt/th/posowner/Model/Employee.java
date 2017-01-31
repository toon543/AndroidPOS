package com.camt.th.posowner.Model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Employee {
    int id;
    String name;
    String code;
    int salary;
    List<Calendar> attends;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Calendar> getAttends() {
        return attends;
    }

    public void setAttends(List<Calendar> attends) {
        this.attends = attends;
    }
}
