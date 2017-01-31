package com.camt.th.posowner.Activity.CRUD;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.camt.th.posowner.Model.POS;
import com.camt.th.posowner.PosApplication;
import com.camt.th.posowner.R;

import java.util.List;

public class UpdateActivity extends AppCompatActivity {
    private int posId;
    private String oldName;

    public PosApplication app;
    public EditText branchName;
    public EditText managerName;
    public EditText url;
    public TextView status;
    public POS pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        app = (PosApplication) getApplicationContext();

        Bundle bundle = getIntent().getExtras();
        posId = bundle.getInt("posId");

        branchName = (EditText) findViewById(R.id.branchField);
        managerName = (EditText) findViewById(R.id.managerField);
        url = (EditText) findViewById(R.id.urlField);
        status = (TextView) findViewById(R.id.status);

        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UpdateActivity.this.checkValidity()) {
                    pos = new POS();
                    pos.setName(branchName.getText().toString());
                    pos.setManager(managerName.getText().toString());
                    pos.setUrl(url.getText().toString());
                    new updateBranch().execute(pos);
                }
            }
        });
    }

    protected boolean checkValidity() {
        if (branchName.getText().toString().matches("")) {
            status.setText("Branch Name is empty");
            return false;
        }
        if (managerName.getText().toString().matches("")) {
            status.setText("Manager Name is empty");
            return false;
        }
        if (url.getText().toString().matches("")) {
            status.setText("URL is empty");
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        new fetchPOSInfo().execute(posId);
    }

    protected void setUp(POS pos) {
        oldName = pos.getName().toString();
        branchName.setText(pos.getName());
        managerName.setText(pos.getManager());
        url.setText(pos.getUrl());
    }

    protected class fetchPOSInfo extends AsyncTask<Integer, Void, POS> {

        @Override
        protected POS doInBackground(Integer... posId) {
            return UpdateActivity.this.app.library.getPOSes().get(posId[0]);
        }

        @Override
        protected void onPostExecute(POS pos) {
            if (pos != null)
                UpdateActivity.this.setUp(pos);
        }
    }

    protected class updateBranch extends AsyncTask<POS, Void, Boolean> {

        @Override
        protected Boolean doInBackground(POS... poses) {
            return UpdateActivity.this.app.library.updatePos(UpdateActivity.this.app.owner, UpdateActivity.this.oldName, poses[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UpdateActivity.this);
            if (result) {
                alertDialogBuilder.setMessage("Branch information updated");
                List<POS> poses = app.library.getPOSes();
                poses.set(UpdateActivity.this.posId, UpdateActivity.this.pos);
                app.library.setPOSes(poses);
            } else {
                alertDialogBuilder.setMessage("There is error during inserted");
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

}
