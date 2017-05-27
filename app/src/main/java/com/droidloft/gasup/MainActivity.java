package com.droidloft.gasup;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    String version = "0.5", buildDate = "5-27-2017";
    TextView dateTextView, lastGasDateTextView, daysTextView, notSetTextView;
    Button gasUpButton;
    String lastGasDate, strDate;
    int notTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        idViews();
        getDate();
        getLastGasDate();
        loadNotTime();
        calculateDays();

        gasUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder confirmAlert = new AlertDialog.Builder(MainActivity.this);
                confirmAlert.setTitle("GASUP!");
                confirmAlert.setMessage("Are you sure?");
                confirmAlert.setCancelable(true);
                confirmAlert.setPositiveButton("GASUP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        lastGasDate = strDate;
                        lastGasDateTextView.setText("Last Gas Up: " + lastGasDate);
                        saveLastDate();
                        setAlarmManager();
                        daysTextView.setText("0");
                        Toast.makeText(MainActivity.this, "Thank you for Gassing Up!", Toast.LENGTH_SHORT).show();
                        dateTextView.setTextColor(Color.BLACK);
                        daysTextView.setTextColor(Color.BLACK);
                        updateWidget();
                        

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

    private void updateWidget() {
        Intent intent = new Intent(this, NewAppWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), NewAppWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }

    private void setAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        int notHours= notTime * 24;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, notHours);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }


    private void getLastGasDate() {
        SharedPreferences lastDatePrefs = getSharedPreferences("last_days_key", MODE_PRIVATE);
        lastGasDate = lastDatePrefs.getString("last_days_key", strDate);
        lastGasDateTextView.setText("Last Gas Up: " + lastGasDate);
    }

    private void loadNotTime(){
        SharedPreferences notSavePrefs = getSharedPreferences("not_save_key", MODE_PRIVATE);
        notTime = notSavePrefs.getInt("not_save_key", 0);
        notSetTextView.setText(notTime + " Days");
    }

    private void saveLastDate() {
        SharedPreferences lastDatePrefs = getSharedPreferences("last_days_key", MODE_PRIVATE);
        SharedPreferences.Editor lastDateEditor = lastDatePrefs.edit();
        lastDateEditor.putString("last_days_key", lastGasDate);
        lastDateEditor.apply();
    }

    private void saveNotTime() {
        SharedPreferences notSavePrefs = getSharedPreferences("not_save_key", MODE_PRIVATE);
        SharedPreferences.Editor notSaveEditor = notSavePrefs.edit();
        notSaveEditor.putInt("not_save_key", notTime);
        notSaveEditor.apply();

    }


    private void getDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        strDate = sdf.format(cal.getTime());
        dateTextView.setText("Todays Date: " + strDate);

    }

    private void idViews() {
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        lastGasDateTextView = (TextView) findViewById(R.id.lastGasTextView);
        daysTextView = (TextView) findViewById(R.id.daysTextView);
        gasUpButton = (Button) findViewById(R.id.gasUpButton);
        notSetTextView = (TextView)findViewById(R.id.notSetTextView);
    }

    private void calculateDays() {
        String lastGasDay = lastGasDate.substring(3, 5);
        String lastGasMonth = lastGasDate.substring(0, 2);
        int lastGasDayInt = Integer.parseInt(lastGasDay);
        int lastGasMonthInt = Integer.parseInt(lastGasMonth);

        Calendar theLastDay = Calendar.getInstance();
        theLastDay.set(Calendar.DAY_OF_MONTH, lastGasDayInt);
        theLastDay.set(Calendar.MONTH, lastGasMonthInt - 1);

        Calendar today = Calendar.getInstance();
        long diff = today.getTimeInMillis() - theLastDay.getTimeInMillis();
        long days = ((diff / (24 * 60 * 60 * 1000)));
        daysTextView.setText("" + days);

        if(days >= 8) {
            daysTextView.setTextColor(Color.RED);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            AlertDialog.Builder aboutAlert = new AlertDialog.Builder(this);
            aboutAlert.setTitle("GasUp v" + version);
            aboutAlert.setMessage("Build Date: " + buildDate + "\n" + "by Michael May" + "\n" + "DroidLoft");
            aboutAlert.setCancelable(true);
            aboutAlert.setIcon(R.mipmap.ic_launcher);
            aboutAlert.show();
        }

        if(item.getItemId() == R.id.set_notification) {
            showSetNotificationDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSetNotificationDialog() {
        final Dialog notDialog = new Dialog(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View notLayout = inflater.inflate(R.layout.notification_set_layout, (ViewGroup)findViewById(R.id.not_set_layout));
        notDialog.setContentView(notLayout);
        notDialog.setCancelable(true);
        notDialog.show();

        final EditText notSetEditText = (EditText)notLayout.findViewById(R.id.notSetEditText);
        notSetEditText.setText("" + notTime);
        Button notSetButton = (Button)notLayout.findViewById(R.id.setButton);

        notSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notTime = Integer.parseInt(notSetEditText.getText().toString());
                saveNotTime();
                notSetTextView.setText(notTime + " Days");
                notDialog.cancel();
                Toast.makeText(MainActivity.this, "Notification Time Changed to: " + notTime + " Days", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
