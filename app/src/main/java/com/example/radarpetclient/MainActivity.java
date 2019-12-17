package com.example.radarpetclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.radarpetclient.http.RegistroHttp;
import com.example.radarpetclient.model.Registro;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RegistrosTask mTask;
    List<Registro> mRegistros;
    ImageButton imgCalendar;
    ImageButton imgGo;
    ProgressBar mProgressBar;
    boolean mRegistrosIsEmpty = true;
    Calendar calendar;
    int year, month, day;
    EditText editTextDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        imgGo = (ImageButton) findViewById(R.id.img_go);
        imgGo.setOnClickListener(this);

        imgCalendar = (ImageButton) findViewById(R.id.img_calendar);
        imgCalendar.setOnClickListener(this);

        editTextDate = (EditText) findViewById(R.id.edt_txt_date);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        if (mRegistros == null) {
            mRegistros = new ArrayList<Registro>();
        }

        if (mTask == null) {
            if (RegistroHttp.isConnected(this)) {
                startDownload();
            } else {
                Toast.makeText(this, "Disconnected...", Toast.LENGTH_SHORT).show();
            }
        } else if (mTask.getStatus() == AsyncTask.Status.RUNNING) {
            showProgress(true);
        }
    }

    private void showProgress(boolean b) {
        if (b) {
            Toast.makeText(this, "Downloading log information... Please Wait!", Toast.LENGTH_LONG).show();
        }
        mProgressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    private void startDownload() {
        if (mTask == null || mTask.getStatus() != AsyncTask.Status.RUNNING) {
            mTask = new MainActivity.RegistrosTask();
            mTask.execute();
        }
    }

    class RegistrosTask extends AsyncTask<Void, Void, List<Registro>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected List<Registro> doInBackground(Void... strings) {
            return RegistroHttp.loadRegistrosJson();
        }

        @Override
        protected void onPostExecute(List<Registro> registros) {
            super.onPostExecute(registros);
            showProgress(false);
            if (registros != null) {
                mRegistros.clear();
                mRegistros.addAll(registros);
                if (mRegistros.size() > 0) {
                    mRegistrosIsEmpty = false;
                }
            } else {
                Toast.makeText(MainActivity.this, "Failed to get records...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_calendar:
                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            editTextDate.setText(dayOfMonth + "/" + onMonth((month + 1)) + "/" + year);
                        }
                    }, year, month, day);
                    datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                    datePickerDialog.show();
                break;
            case R.id.img_go:
                if (editTextDate.getText().length() > 0) {
                    if (!mRegistrosIsEmpty) {
                        Intent intentThisToMapsActivity = new Intent(this, MapsActivity.class);
                        Bundle animation = ActivityOptionsCompat.makeCustomAnimation(
                                MainActivity.this, R.anim.slide_in_left, R.anim.slide_out_left).toBundle();
                        ActivityCompat.startActivity(MainActivity.this, intentThisToMapsActivity, animation);

                        Bundle mRegistrosData = new Bundle();
                        mRegistrosData.putParcelableArrayList("mRegistros", (ArrayList<? extends Parcelable>) mRegistros);
                        Intent intent = new Intent(this, MapsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtras(mRegistrosData);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Error! Please restart the application...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Invalid date! Please check the date...", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public String onMonth(int month){
        switch (month) {
            case 1:
                return "jan";
            case 2:
                return "fev";
            case 3:
                return "mar";
            case 4:
                return "abr";
            case 5:
                return "mai";
            case 6:
                return "jun";
            case 7:
                return "jul";
            case 8:
                return "ago";
            case 9:
                return "set";
            case 10:
                return "out";
            case 11:
                return "nov";
            default:
                return "dez";
        }
    }
}
