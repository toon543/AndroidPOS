package com.camt.th.posowner.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.camt.th.posowner.Model.Finance;
import com.camt.th.posowner.Model.POS;
import com.camt.th.posowner.PosApplication;
import com.camt.th.posowner.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class POSFinanceActivity extends AppCompatActivity {
    private int posId;
    private POS pos;
    private int financeCounts;
    private boolean[] isDisplay;
    public PosApplication app;
    protected TextView branchName;
    protected TextView managerName;
    protected TextView topSell;
    protected List<Finance> grapthResult;
    protected boolean isGraphReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posfinance);

        app = (PosApplication) getApplicationContext();
        pos = app.library.getPOSes().get(app.posID);

        branchName = (TextView) findViewById(R.id.branchName);
        managerName = (TextView) findViewById(R.id.managerName);
        topSell = (TextView) findViewById(R.id.topSell);

        branchName.setText(pos.getName());
        managerName.setText(pos.getManager());
        isDisplay = new boolean[]{true, true, true};
        financeCounts = 7;

        Spinner financeSpinner = (Spinner) findViewById(R.id.financeMenu);
        ArrayAdapter<CharSequence> financenAdapter = ArrayAdapter.createFromResource(this,
                R.array.finance_array, android.R.layout.simple_spinner_item);
        financenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        financeSpinner.setAdapter(financenAdapter);
        financeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    isDisplay[0] = true;
                    isDisplay[1] = false;
                    isDisplay[2] = false;
                } else if (position == 1) {
                    isDisplay[0] = false;
                    isDisplay[1] = true;
                    isDisplay[2] = false;
                } else if (position == 2) {
                    isDisplay[0] = false;
                    isDisplay[1] = false;
                    isDisplay[2] = true;
                } else if (position == 3) {
                    isDisplay[0] = true;
                    isDisplay[1] = true;
                    isDisplay[2] = true;
                }
                if (isGraphReady) {
                    generateGraph(POSFinanceActivity.this.grapthResult, POSFinanceActivity.this.isDisplay);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner periodSpinner = (Spinner) findViewById(R.id.weekMenu);
        ArrayAdapter<CharSequence> periodAdapter = ArrayAdapter.createFromResource(this,
                R.array.period_array, android.R.layout.simple_spinner_item);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSpinner.setAdapter(periodAdapter);
        periodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    financeCounts = 7;
                } else if (position == 1) {
                    financeCounts = 14;
                } else if (position == 2) {
                    financeCounts = 21;
                } else if (position == 3) {
                    financeCounts = 30;
                }
                if (isGraphReady) {
                    generateGraph(POSFinanceActivity.this.grapthResult, POSFinanceActivity.this.isDisplay);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                financeCounts = 7;
//                generateGraph(POSFinanceActivity.this.grapthResult, POSFinanceActivity.this.isDisplay);
            }
        });
    }

    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        new fetchGraph().execute();
    }

    protected void generateGraph(List<Finance> finances, boolean[] isDisplay) {
        topSell = (TextView) findViewById(R.id.topSell);
        topSell.setText(finances.get(finances.size() - 1).getTopsell());

        ArrayList<Entry> revenueComp = new ArrayList<Entry>();
        ArrayList<Entry> expenseComp = new ArrayList<Entry>();
        ArrayList<Entry> profitComp = new ArrayList<Entry>();

        LineChart mLineChart = (LineChart) findViewById(R.id.chart);

        ArrayList<String> xVals = new ArrayList<String>();
        int viewCounts = finances.size();
        if (viewCounts > this.financeCounts) {
            viewCounts = this.financeCounts;
        }
        for (int i = 0; i < viewCounts; i++) {
            Entry rev = new Entry((float) finances.get(i).getRevenue(), i); // 0 == quarter 1
            revenueComp.add(rev);

            Entry ex = new Entry((float) finances.get(i).getExpense(), i); // 0 == quarter 1
            expenseComp.add(ex);

            Entry pro = new Entry((float) finances.get(i).getProfit(), i); // 0 == quarter 1
            profitComp.add(pro);
            Date date = finances.get(i).getDate();
            xVals.add(date.toString());
        }

        LineDataSet revenue = new LineDataSet(revenueComp, "Revenue");
        revenue.setAxisDependency(YAxis.AxisDependency.LEFT);
        revenue.setDrawValues(false);

        LineDataSet expense = new LineDataSet(expenseComp, "Expense");
        expense.setColor(0xFFFF0000);
        expense.setAxisDependency(YAxis.AxisDependency.LEFT);
        expense.setDrawValues(false);

        LineDataSet profit = new LineDataSet(profitComp, "Profit");
        profit.setColor(0xFF00FF00);
        profit.setAxisDependency(YAxis.AxisDependency.LEFT);
        profit.setDrawValues(false);

        if (this.financeCounts == 7) {
            expense.setDrawValues(true);
            revenue.setDrawValues(true);
            profit.setDrawValues(true);
        }
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        if (isDisplay[0]) {
            dataSets.add(revenue);
        }
        if (isDisplay[1]) {
            dataSets.add(expense);
        }
        if (isDisplay[2]) {
            dataSets.add(profit);
        }

        LineData data = new LineData(xVals, dataSets);
        mLineChart.setData(data);
        mLineChart.invalidate(); // refresh
        this.isGraphReady = true;
    }

    protected void generateGraph(List<Finance> finances) {
        generateGraph(finances, new boolean[]{true, true, true});
    }

    protected class fetchGraph extends AsyncTask<String, Void, List<Finance>> {

        @Override
        protected List<Finance> doInBackground(String... params) {
            try {
                app.library.getFinanceList(app.owner, pos.getUrl());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<Finance> finances = app.library.getFinances();
            return finances;
        }

        @Override
        protected void onPostExecute(List<Finance> result) {
            POSFinanceActivity.this.grapthResult = result;
            POSFinanceActivity.this.generateGraph(result);
        }
    }

}
