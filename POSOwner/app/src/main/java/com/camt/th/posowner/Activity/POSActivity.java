package com.camt.th.posowner.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.camt.th.posowner.Activity.CRUD.InsertActivity;
import com.camt.th.posowner.Component.POSAdapter;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class POSActivity extends AppCompatActivity {
    public PosApplication app;
    private ListView posList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);

        app = (PosApplication) getApplicationContext();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        posList = (ListView) findViewById(R.id.posList);
        posList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                POS item = (POS) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(POSActivity.this, POSInfoActivity.class);
                intent.putExtra("posId", item.getId());
                POSActivity.this.startActivity(intent);
            }
        });

        Spinner menuSpinner = (Spinner) findViewById(R.id.menuSpinner);
        ArrayAdapter<CharSequence> menuAdapter = ArrayAdapter.createFromResource(this,
                R.array.menu_array, android.R.layout.simple_spinner_item);
        menuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuSpinner.setAdapter(menuAdapter);
        menuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 1){
                    Intent intent = new Intent(POSActivity.this, InsertActivity.class);
                    POSActivity.this.startActivity(intent);
                }else if(position == 2){
                    Intent intent = new Intent(POSActivity.this, MainActivity.class);
                    POSActivity.this.startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        super.onResume();
        new generateList().execute();
    }

    protected void updateAdapter(List<POS> poses) {
        POSAdapter adapter = new POSAdapter(this, poses);
        this.posList.setAdapter(adapter);
        //new generateGraph().execute(poses);
        generateGraphData(poses);
    }

    protected class generateList extends AsyncTask<String, Void, List<POS>> {

        @Override
        protected List<POS> doInBackground(String... params) {
            try {
                app.library.setPOSes(null);
                app.library.getPOSList(app.owner);
                return app.getPOS();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<POS> result) {
            POSActivity.this.updateAdapter(result);
        }
    }
    protected class generateGraph extends AsyncTask<List<POS>, Void, Void> {

        @Override
        protected Void doInBackground(List<POS>... params) {
            POSActivity.this.generateGraphData(params[0]);
            return null;
        }
    }

    public void generateGraphData(List<POS> POSes) {
        LineChart mLineChart = (LineChart) findViewById(R.id.chart);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < POSes.size(); i++) {
            POS pos = POSes.get(i);
            if(pos.getFinances() != null) {
                ArrayList<Entry> posComp = new ArrayList<Entry>();
                for (int j = 0; j < pos.getFinances().size(); j++) {
                    Finance finance = pos.getFinances().get(j);
                    Entry rev = new Entry((float) finance.getRevenue(), j); // 0 == quarter 1
                    posComp.add(rev);
                    if (i == 0) {
                        Date date = finance.getDate();
                        String DATE_FORMAT_NOW = "yyyy-MM-dd";
                        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
                        String stringDate = sdf.format(date);
                        try {
                            Date date2 = sdf.parse(stringDate);
                        } catch(ParseException e){
                            //Exception handling
                        } catch(Exception e){
                            //handle exception
                        }
                        xVals.add(stringDate);
                    }
                }
                Random rand = new Random();
                int r = rand.nextInt(255);
                int g = rand.nextInt(255);
                int b = rand.nextInt(255);
                int randomColor = Color.rgb(r, g, b);

                LineDataSet revenue = new LineDataSet(posComp, pos.getName());
                revenue.setColor(randomColor);
                revenue.setAxisDependency(YAxis.AxisDependency.LEFT);
                revenue.setDrawValues(false);
                dataSets.add(revenue);
            }
        }

        LineData data = new LineData(xVals, dataSets);
        mLineChart.setData(data);
        mLineChart.invalidate(); // refresh
    }
}
