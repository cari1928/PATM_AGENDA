package com.example.radog.patm_agenda;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewEvent extends AppCompatActivity {

    @BindView(R.id.etNameE)
    EditText etNameE;
    @BindView(R.id.etDescE)
    EditText etDescE;
    @BindView(R.id.etDateE)
    EditText etDateE;
    @BindView(R.id.etTimeE)
    EditText etTimeE;

    private int day, month, year, hour, minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btnDate)
    public void btnDate() {
        final Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etDateE.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }
                , day, month, year);
        datePickerDialog.show();
    }

    @OnClick(R.id.btnTime)
    public void btnTime() {
        final Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                etTimeE.setText(hourOfDay + ":" + minute);
            }
            //false es para el formato
        }, hour, minute, false);
        timePickerDialog.show();
    }

    @OnClick(R.id.btnContacts)
    public void btnContacts() {
        Intent intContacts = new Intent(this, ListContacts.class);
        Bundle data = new Bundle();

        String name = etNameE.getText().toString();
        String desc = etDescE.getText().toString();
        String date = etDateE.getText().toString();
        String time = etTimeE.getText().toString();


        //DESCOMENTAR ESTO PARA LA VERSIÓN FINAL!!!!
        //if (name.equals("") || desc.equals("") || date.equals("") || time.equals("")) {
        //  Toast.makeText(this, "Ingrese los datos solicitados", Toast.LENGTH_LONG).show();
        //} else {
        //SEND DATA
        data.putString("SOURCE", "NEW");
        data.putString("NAME", name);
        data.putString("DESC", desc);
        data.putString("DATE", date);
        data.putString("TIME", time);

        intContacts.putExtras(data);
        startActivity(intContacts);
        finish();
        // }
    }


}
