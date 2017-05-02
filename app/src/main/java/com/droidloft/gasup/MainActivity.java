package com.droidloft.gasup;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    String version = "0.1", buildDate = "5-2-2017";
    TextView dateTextView, lastGasDateTextView,daysTextView;
    Button gasUpButton;
    String lastGasDate, strDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        idViews();
        getDate();
        getLastGasDate();
        calculateDays();

        gasUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder confirmAlert  = new AlertDialog.Builder(MainActivity.this);
                confirmAlert.setTitle("GASUP!");
                confirmAlert.setMessage("Are you sure?");
                confirmAlert.setCancelable(true);
                confirmAlert.setPositiveButton("GASUP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        lastGasDate = strDate;
                        lastGasDateTextView.setText("Last Gas Up: " + lastGasDate);
                        saveLastDate();
                        Toast.makeText(MainActivity.this, "Thank you for Gassing Up!", Toast.LENGTH_SHORT).show();
                    }
                });

                confirmAlert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //close dialog
                    }
                });
                confirmAlert.show();


            }
        });

    }

    private void getLastGasDate() {
        SharedPreferences lastDatePrefs = getSharedPreferences("last_days_key", MODE_PRIVATE);
        lastGasDate = lastDatePrefs.getString("last_days_key", strDate);
        lastGasDateTextView.setText("Last Gas Up: " + lastGasDate);
    }

    private void saveLastDate() {
        SharedPreferences lastDatePrefs = getSharedPreferences("last_days_key", MODE_PRIVATE);
        SharedPreferences.Editor lastDateEditor = lastDatePrefs.edit();
        lastDateEditor.putString("last_days_key", lastGasDate);
        lastDateEditor.apply();
    }



    private void getDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        strDate = sdf.format(cal.getTime());
        dateTextView.setText("Todays Date: " + strDate);

    }

    private void idViews() {
        dateTextView = (TextView)findViewById(R.id.dateTextView);
        lastGasDateTextView = (TextView)findViewById(R.id.lastGasTextView);
        daysTextView = (TextView)findViewById(R.id.daysTextView);
        gasUpButton = (Button)findViewById(R.id.gasUpButton);
    }

    private void calculateDays() {
        String lastGasDay = lastGasDate.substring(3,5);
        String lastGasMonth = lastGasDate.substring(0,2);
        int lastGasDayInt = Integer.parseInt(lastGasDay);
        int lastGasMonthInt = Integer.parseInt(lastGasMonth);

        Calendar theLastDay = Calendar.getInstance();
        theLastDay.set(Calendar.DAY_OF_MONTH, lastGasDayInt);
        theLastDay.set(Calendar.MONTH, lastGasMonthInt -1);

        Calendar today = Calendar.getInstance();
        long diff = today.getTimeInMillis() - theLastDay.getTimeInMillis();
        long days = ((diff / (24 * 60 * 60 * 1000)));
        daysTextView.setText("" + days);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.about) {
            AlertDialog.Builder aboutAlert = new AlertDialog.Builder(this);
            aboutAlert.setTitle("GasUp v" + version);
            aboutAlert.setMessage("Build Date: " + buildDate + "\n" + "by Michael May" + "\n" + "DroidLoft");
            aboutAlert.setCancelable(true);
            aboutAlert.setIcon(R.mipmap.ic_launcher);
            aboutAlert.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
