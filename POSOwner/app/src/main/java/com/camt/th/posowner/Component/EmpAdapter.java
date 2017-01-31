package com.camt.th.posowner.Component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.camt.th.posowner.Model.Employee;
import com.camt.th.posowner.Model.POS;
import com.camt.th.posowner.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by W.J on 1/23/2016.
 */
public class EmpAdapter extends BaseAdapter {
    Context mContext;
    List<Employee> employees;

    public EmpAdapter(Context context, List<Employee> pos) {
        this.mContext = context;
        this.employees = pos;
    }

    public int getCount() {
        return employees.size();
    }

    public Employee getItem(int position) {
        return employees.get(position);
    }

    public long getItemId(int position) {
        return employees.get(position).getId();
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = mInflater.inflate(R.layout.emp_list, parent, false);
        LinearLayout employeeFrame = (LinearLayout) view.findViewById(R.id.employeeFrame);
        employeeFrame.setBackgroundColor(0x73dad3);
        TextView empName = (TextView) view.findViewById(R.id.empName);
        empName.setText(employees.get(position).getName());
        if (employees.get(position).getAttends() != null) {
            if (employees.get(position).getAttends().size() > 0) {
                Calendar isToday = employees.get(position).getAttends().get(0);
                Calendar today = Calendar.getInstance();
                if (isToday.get(Calendar.DATE) == today.get(Calendar.DATE) && isToday.get(Calendar.MONTH) == today.get(Calendar.MONTH) && isToday.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
                    employeeFrame.setBackgroundColor(0xFF22FF00);
                }
            }
        }

        return view;
    }
}
