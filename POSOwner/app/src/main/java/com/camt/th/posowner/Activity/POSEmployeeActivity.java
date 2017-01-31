package com.camt.th.posowner.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import com.camt.th.posowner.Model.Employee;
import com.camt.th.posowner.Model.POS;
import com.camt.th.posowner.PosApplication;
import com.camt.th.posowner.R;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class POSEmployeeActivity extends AppCompatActivity {
    public int empId;
    public int posId;
    public PosApplication app;
    public TextView nameField;
    public TextView salaryField;
    public TextView codeField;
    public TextView sumField;
    public TextView attendDate;
    public String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posemployee);

        //calendarView = (CalendarView) findViewById(R.id.calendarView);
        nameField = (TextView) findViewById(R.id.nameField);
        salaryField = (TextView) findViewById(R.id.salaryField);
        codeField = (TextView) findViewById(R.id.codeField);
        sumField = (TextView) findViewById(R.id.sumField);
        attendDate = (TextView) findViewById(R.id.attendDate);
        selectedDate = "";

        app = (PosApplication) getApplicationContext();
        Bundle bundle = getIntent().getExtras();
        empId = bundle.getInt("employee") - 1;
        posId = bundle.getInt("posId");

        Button attendant = (Button) findViewById(R.id.submitAttend);
        attendant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedDate.matches("")) {
                    new insertAttend().execute(empId, posId);
                } else {
                    genDialog("Please select date to attend below!");
                }
            }
        });

        new fetchEmployeeInfo().execute(empId, posId);
    }

    protected class fetchEmployeeInfo extends AsyncTask<Integer, Void, Employee> {

        @Override
        protected Employee doInBackground(Integer... params) {
            return app.library.getEmployees().get(params[0]);
        }

        @Override
        protected void onPostExecute(Employee emp) {
            POSEmployeeActivity.this.setUp(emp);
        }
    }

    protected class insertAttend extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... data) {
            POS pos = app.library.getPOSes().get(data[1]);
            Employee emp = app.library.getEmployees().get(data[0]);
            return app.library.attendEmployee(app.owner, pos, emp, POSEmployeeActivity.this.selectedDate);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(POSEmployeeActivity.this);
            if (result) {
                alertDialogBuilder.setMessage("Save today attend.");
            } else {
                alertDialogBuilder.setMessage("There is error during save");
            }

            alertDialogBuilder.setPositiveButton("close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    //Toast.makeText(InsertActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void setUp(Employee emp) {
        this.nameField.setText(emp.getName());
        this.salaryField.setText(Integer.toString(emp.getSalary()));
        this.codeField.setText(emp.getCode());
//        double percentWork = ((double)emp.getAttends().size() / 30);
        double sum = (double) emp.getSalary() * (double) emp.getAttends().size();
        this.sumField.setText(Double.toString(sum));

        initializeCalendar(emp);
    }

    public void initializeCalendar(Employee emp) {
        CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);

        for (int i = 0; i < emp.getAttends().size(); i++) {
            Calendar attend = emp.getAttends().get(i);
            ColorDrawable green = new ColorDrawable(Color.GREEN);
            caldroidFragment.setBackgroundDrawableForDate(green, attend.getTime());
        }
        CaldroidListener listener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                POSEmployeeActivity.this.selectedDate = format1.format(date);
                POSEmployeeActivity.this.attendDate.setText(POSEmployeeActivity.this.selectedDate);
            }
        };
        caldroidFragment.setCaldroidListener(listener);
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendarView, caldroidFragment);
        t.commit();
    }

    public void genDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(POSEmployeeActivity.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(InsertActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
