package com.camt.th.posowner.Component;

import android.annotation.TargetApi;
import android.os.Build;

import com.camt.th.posowner.Model.Employee;
import com.camt.th.posowner.Model.Finance;
import com.camt.th.posowner.Model.Owner;
import com.camt.th.posowner.Model.POS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by W.J on 1/23/2016.
 */
public class Datalibrary {
    private List<Finance> finances;
    private List<Employee> employees;
    private List<POS> POSes;
    private Datasource datasource;
    private boolean add;

    public Datalibrary(){
        this.finances = null;
        this.employees = null;
        this.POSes = null;
        this.datasource = null;
        this.add = false;
    }

    public void setWebservice(String newWebservice) {
        this.datasource.setWEBSERVICE(newWebservice);
    }

    public String getWebservice() {
        return this.datasource.getWEBSERVICE();
    }

    public List<Finance> getFinances() {
        return finances;
    }

    public void setFinances(List<Finance> finances) {
        this.finances = finances;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<POS> getPOSes() {
        return POSes;
    }

    public void setPOSes(List<POS> POSes) {
        this.POSes = POSes;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void getPOSList(Owner owner) throws JSONException {
        if (this.POSes == null)
            this.POSes = new ArrayList<POS>();
        String query = "pos-get-all/?username=" + owner.getUsername() + "&password=" + owner.getPassword();
        datasource = new Datasource(query, "GET");
        JSONObject results = datasource.execute();
        JSONArray poses = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            poses = (JSONArray) results.get("response");
        }

        if (poses != null) {
            for (int i = 0; i < poses.length(); i++) {
                JSONObject posRaw = null;
                try {
                    posRaw = (JSONObject) poses.get(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                POS pos = new POS();
                try {
                    pos.setId((Integer.parseInt(posRaw.get("id").toString())));
                    pos.setName(posRaw.get("name").toString());
                    pos.setManager(posRaw.get("manager").toString());
                    pos.setUrl(posRaw.get("url").toString());

                    getFinanceList(owner, pos.getUrl());
                    pos.setFinances(this.finances);
                    this.finances = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                POSes.add(pos);
            }
        }
    }

    public void getEmployeeList(Owner owner, String posName) {
        if (this.employees == null)
            this.employees = new ArrayList<Employee>();
        datasource = new Datasource("pos-employee/" + posName + "/?username=" + owner.getUsername() + "&password=" + owner.getPassword(), "GET");
        JSONObject results = datasource.execute();
        JSONArray employeesRaw = null;
        if (results != null) {
            try {
                employeesRaw = new JSONArray(results.get("response").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (employeesRaw != null) {
                for (int i = 0; i < employeesRaw.length(); i++) {
                    JSONObject employee = null;
                    try {
                        employee = (JSONObject) employeesRaw.get(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Employee emp = new Employee();
                    try {
                        emp.setId((Integer.parseInt(employee.get("id").toString())));
                        emp.setName(employee.get("name").toString());
                        emp.setCode(employee.get("code").toString());
                        emp.setSalary(employee.getInt("salary"));
//                        JSONArray attendants = new JSONArray(employee.get("attendants").toString());
//                        int year = 0, month = 0, day = 0;
//                        for (int j = 0; j < attendants.length(); j++) {
//                            JSONObject attend = null;
//                            try {
//                                attend = (JSONObject) attendants.get(j);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            year = Integer.parseInt(attend.get("year").toString());
//                            month = Integer.parseInt(attend.get("month").toString());
//                            day = Integer.parseInt(attend.get("day").toString());
//                            emp.getAttends().add(new Date(year,month,day));
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    employees.add(emp);
                }
            }
        }
    }

    public void getFinanceList(Owner owner, String posName) throws JSONException {
        this.finances = new ArrayList<Finance>();
        datasource = new Datasource("pos-finance/" + posName + "/?username=" + owner.getUsername() + "&password=" + owner.getPassword(), "GET");
        JSONObject results = datasource.execute();
        JSONArray financeRaw = null;
        financeRaw = new JSONArray(results.get("response").toString());
        if (financeRaw != null) {
            for (int i = 0; i < financeRaw.length(); i++) {
                JSONObject result = null;
                try {
                    result = (JSONObject) financeRaw.get(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Finance finance = new Finance();
                try {
                    finance.setId((Integer.parseInt(result.get("id").toString())));
                    finance.setTopsell(result.get("top_sell").toString());
                    finance.setRevenue((Double.parseDouble(result.get("revenue").toString())));
                    finance.setProfit((Double.parseDouble(result.get("profit").toString())));
                    finance.setExpense((Double.parseDouble(result.get("expense").toString())));
                    String rawDate = result.get("date").toString().substring(0, 10);
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    Date date = null;
                    try {
                        date = format.parse(rawDate);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    finance.setDate(date);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finances.add(finance);
            }
        }
    }

    /*POS*/
    public boolean insertPos(Owner owner, POS pos) {
        if (owner != null && pos != null) {
            datasource = new Datasource("pos-update/new/?username=" + owner.getUsername()
                    + "&password=" + owner.getPassword()
                    + "&name=" + pos.getName()
                    + "&manager=" + pos.getManager()
                    + "&url=" + pos.getUrl(), "GET");
            JSONObject results = datasource.execute();
            if (results != null) {
                try {
                    return results.getBoolean("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean updatePos(Owner owner, String oldName, POS pos) {
        if (owner != null && pos != null) {
            datasource = new Datasource("pos-update/" + oldName + "/?username=" + owner.getUsername()
                    + "&password=" + owner.getPassword()
                    + "&name=" + pos.getName()
                    + "&manager=" + pos.getManager()
                    + "&url=" + pos.getUrl(), "GET");
            JSONObject results = datasource.execute();
            if (results != null) {
                try {
                    return results.getBoolean("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /*Employeee*/
    public Employee getEmployeeInfo(Owner owner, String posName, Employee empRe) {
        if (this.employees == null)
            this.employees = new ArrayList<Employee>();
        datasource = new Datasource("pos-employee-info/" + posName + "/" + empRe.getCode() + "/?username=" + owner.getUsername() + "&password=" + owner.getPassword(), "GET");
        JSONObject results = datasource.execute();
        JSONObject employeesRaw = null;
        if (results != null) {
            try {
                employeesRaw = new JSONObject(results.get("response").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Employee emp = new Employee();
            try {
                List<Calendar> attends = new ArrayList<Calendar>();
                emp.setId((Integer.parseInt(employeesRaw.get("id").toString())));
                emp.setName(employeesRaw.get("name").toString());
                emp.setCode(employeesRaw.get("code").toString());
                emp.setSalary(employeesRaw.getInt("salary"));
                JSONArray attendants = new JSONArray(employeesRaw.get("attendants").toString());
                int year = 0, month = 0, day = 0;
                for (int j = 0; j < attendants.length(); j++) {
                    JSONObject attend = null;
                    try {
                        attend = (JSONObject) attendants.get(j);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    year = Integer.parseInt(attend.get("year").toString());
                    month = Integer.parseInt(attend.get("month").toString())-1;
                    day = Integer.parseInt(attend.get("day").toString());
                    Calendar date = Calendar.getInstance();
                    date.set(year,month,day);
                    attends.add(date);
                }
                emp.setAttends(attends);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return emp;
        }
        return null;
    }

    public boolean attendEmployee(Owner owner, POS pos, Employee emp,String date) {
        if (owner != null && pos != null && emp != null) {
            datasource = new Datasource("pos-employee-attend/" + pos.getUrl() + "/" + emp.getCode() + "/?username=" + owner.getUsername()
                    + "&password=" + owner.getPassword() + "&date=" + date);
            JSONObject results = datasource.execute();
            if (results != null) {
                try {
                    return results.getBoolean("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
