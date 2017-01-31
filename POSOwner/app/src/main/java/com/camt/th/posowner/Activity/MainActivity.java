package com.camt.th.posowner.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.camt.th.posowner.Component.Datasource;
import com.camt.th.posowner.Model.Owner;
import com.camt.th.posowner.PosApplication;
import com.camt.th.posowner.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Button login;
    private EditText username;
    private EditText password;
    private TextView status;
    private EditText serverUrl;
    private Button changeServerUrl;
    public PosApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (PosApplication) getApplicationContext();
        app.owner = new Owner();

        login = (Button) findViewById(R.id.submit);
        username = (EditText) findViewById(R.id.usernameField);
        password = (EditText) findViewById(R.id.passwordField);
        status = (TextView) findViewById(R.id.status);
        serverUrl = (EditText) findViewById(R.id.serverUrl);
        serverUrl.setText(app.library.getWebservice());
        changeServerUrl = (Button) findViewById(R.id.changeServerUrl);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.this.checkValidity()) {
                    new AuthenticateTask().execute(username.getText().toString(), password.getText().toString());
                }
            }
        });
        changeServerUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.library.setWebservice(serverUrl.getText().toString());
            }
        });
    }

    protected boolean checkValidity() {
        if (username.getText().toString().matches("")) {
            status.setText("Username is empty!");
            return false;
        }
        if (password.getText().toString().matches("")) {
            status.setText("Password is empty!");
            return false;
        }
        return true;
    }

    private class AuthenticateTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... text) {
            if (authenticate(text[0], text[1])) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Intent intent = new Intent(MainActivity.this, POSActivity.class);
                MainActivity.this.startActivity(intent);
            } else {
                MainActivity.this.status.setText("Access Denied");
            }
        }
    }

    private boolean authenticate(String username, String password) {

        String query = "authenticate/?username=" + username + "&password=" + password;
        Datasource login = new Datasource(query, "GET");
        JSONObject result = login.execute();
        if (result == null) {
            return false;
        }
        try {
            if ((boolean) result.get("status")) {
                //JSONObject raw = (JSONObject) result.get("status");
                app.owner.setUsername(username);
                app.owner.setPassword(password);
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            return false;
        }

    }
}
