package com.camt.th.posowner.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.camt.th.posowner.Activity.CRUD.UpdateActivity;
import com.camt.th.posowner.Component.EmpAdapter;
import com.camt.th.posowner.Model.Employee;
import com.camt.th.posowner.Model.POS;
import com.camt.th.posowner.PosApplication;
import com.camt.th.posowner.R;

import java.util.List;

public class POSInfoActivity extends AppCompatActivity {

    protected TextView branchName;
    protected TextView managerName;
    protected ListView empList;
    public String posURL;
    public POS pos;
    public int posId;
    public PosApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posinfo);

        app = (PosApplication) getApplicationContext();
        Bundle bundle = getIntent().getExtras();
        posId = bundle.getInt("posId") - 1;

        branchName = (TextView) findViewById(R.id.branchName);
        managerName = (TextView) findViewById(R.id.managerName);

        empList = (ListView) findViewById(R.id.empList);
        empList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Employee item = (Employee) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(POSInfoActivity.this, POSEmployeeActivity.class);
                intent.putExtra("employee", item.getId());
                intent.putExtra("posId", posId);
                POSInfoActivity.this.startActivity(intent);
            }
        });

        Button financeButton = (Button) findViewById(R.id.financeView);
        financeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(POSInfoActivity.this, POSFinanceActivity.class);
                POSInfoActivity.this.startActivity(intent);
            }
        });

        Button updateButton = (Button) findViewById(R.id.updatePOS);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(POSInfoActivity.this, UpdateActivity.class);
                intent.putExtra("posId", posId);
                POSInfoActivity.this.startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        new fetchPOSInfo().execute(posId);
    }

    protected void setUp(POS pos) {
        app.posID = posId;
        this.branchName.setText(pos.getName());
        this.managerName.setText(pos.getManager());
        this.posURL = pos.getUrl();
        new generateList().execute(this.posURL);
    }

    protected void updateAdapter(List<Employee> poses) {
        EmpAdapter adapter = new EmpAdapter(this, poses);
        this.empList.setAdapter(adapter);
    }

    protected class fetchPOSInfo extends AsyncTask<Integer, Void, POS> {

        @Override
        protected POS doInBackground(Integer... params) {
            return app.library.getPOSes().get(params[0]);
        }

        @Override
        protected void onPostExecute(POS pos) {
            POSInfoActivity.this.setUp(pos);
        }
    }

    protected class generateList extends AsyncTask<String, Void, List<Employee>> {

        @Override
        protected List<Employee> doInBackground(String... params) {
            List<Employee> emps = app.getEmployee(params[0]);
            for(int i = 0; i < emps.size();i++){
                Employee info = app.library.getEmployeeInfo(app.owner,params[0], emps.get(i));
                emps.set(i,info);
            }
            return emps;
        }

        @Override
        protected void onPostExecute(List<Employee> result) {
            POSInfoActivity.this.updateAdapter(result);
        }
    }
}
