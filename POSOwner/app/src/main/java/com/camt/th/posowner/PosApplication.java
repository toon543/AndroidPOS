package com.camt.th.posowner;

import android.app.Application;

import com.camt.th.posowner.Component.Datalibrary;
import com.camt.th.posowner.Model.Employee;
import com.camt.th.posowner.Model.Finance;
import com.camt.th.posowner.Model.Owner;
import com.camt.th.posowner.Model.POS;

import org.json.JSONException;

import java.util.List;

/**
 * Created by W.J on 1/23/2016.
 */
public class PosApplication extends Application {
    public Datalibrary library = new Datalibrary();
    public Owner owner;
    public int posID;

    public List<POS> getPOS() throws JSONException {
        if (library.getPOSes() == null)
            library.getPOSList(owner);
        return library.getPOSes();
    }

    public List<Employee> getEmployee(String posName) {
        if (library.getEmployees() == null)
            library.getEmployeeList(owner, posName);
        return library.getEmployees();
    }

    public List<Finance> getFinance(String posName) {
        if (library.getFinances() == null)
            try {
                library.getFinanceList(owner, posName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return library.getFinances();
    }
}
