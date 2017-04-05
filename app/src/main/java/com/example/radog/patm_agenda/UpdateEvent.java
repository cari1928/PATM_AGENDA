package com.example.radog.patm_agenda;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateEvent extends AppCompatActivity {

    @BindView(R.id.etNameE)
    EditText etNameE;
    @BindView(R.id.etDescE)
    EditText etDescE;
    @BindView(R.id.etDateE)
    EditText etDateE;
    @BindView(R.id.etTimeE)
    EditText etTimeE;

    private ArrayList<itemEvent> arrayItem;
    private ListViewAdapter adapter = null;
    ListView lvContacts;
    private String nameE, descE, dateE, timeE;
    private int day, month, year, hour, minute, idEvent;

    private DBHelper objDBH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);
        ButterKnife.bind(this);

        //Opening and connection with SQLite
        objDBH = new DBHelper(this, "CURSO", null, 1);
        objDBH.openDB();

        lvContacts = new ListView(this);
        arrayItem = new ArrayList<>();

        //INTENT DATA
        getBundleElements();
    }

    @OnClick(R.id.btnDate)
    public void btnDate() {
        String[] parts;
        parts = dateE.split("/");
        day = Integer.parseInt(parts[0]);
        month = Integer.parseInt(parts[1]);
        year = Integer.parseInt(parts[2]);

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
        String[] parts;
        parts = timeE.split(":");
        hour = Integer.parseInt(parts[0]);
        minute = Integer.parseInt(parts[1]);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                etTimeE.setText(hourOfDay + ":" + minute);
            }
            //false es para el formato
        }, hour, minute, false);
        timePickerDialog.show();
    }

    @OnClick(R.id.btnShowC)
    public void btnShowC() {
        showDialog();
    }

    @OnClick(R.id.btnSave)
    public void btnSave() {
        long result;
        String[] editTexts = getInfoEditText();

        setIdEvent();
        if (idEvent != -1) {
            result = objDBH.updateEvent(idEvent, editTexts[0], editTexts[1], editTexts[2] + " " + editTexts[3]);
            if (result != -1) {
                Toast.makeText(this, "Successfuly Updated", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Oh oh... Something went wrong...", Toast.LENGTH_LONG).show();
            }
        }
        finish();
    }

    @OnClick(R.id.btnContacts)
    public void btnContacts() {
        Intent intContacts = new Intent(this, ListContacts.class);
        Bundle data = new Bundle();
        String[] editTexts = getInfoEditText();
        setIdEvent();

        //TODO
        //DESCOMENTAR ESTO PARA LA VERSIÓN FINAL!!!!
        //if (name.equals("") || desc.equals("") || date.equals("") || time.equals("")) {
        //  Toast.makeText(this, "Ingrese los datos solicitados", Toast.LENGTH_LONG).show();
        //} else {
        //SEND DATA
        data.putString("SOURCE", "UPDATE");
        data.putInt("ID", idEvent);
        data.putString("NAME", editTexts[0]);
        data.putString("DESC", editTexts[1]);
        data.putString("DATE", editTexts[2]);
        data.putString("TIME", editTexts[3]);

        intContacts.putExtras(data);
        startActivity(intContacts);
        finish();
        // }
    }

    @Override
    protected void onStop() {
        super.onStop();
        objDBH.closeDB();
    }

    private void getBundleElements() {
        Bundle data = getIntent().getExtras();
        nameE = data.getString("NAME");
        descE = data.getString("DESC");
        dateE = data.getString("DATE");

        initComponents();
    }

    private void initComponents() {
        List<String> contacts, event;
        String[] parts;

        etNameE.setText(nameE);
        etDescE.setText(descE);
        parts = dateE.split(" ");
        timeE = parts[1];
        etDateE.setText(parts[0]);
        etTimeE.setText(timeE);

        //Load contacts list
        event = objDBH.selectEvent(nameE, descE, dateE);
        dateE = parts[0];
        if (event != null) {
            contacts = objDBH.selectContacts(Integer.parseInt(event.get(0)));
            if (contacts != null) {
                for (String contact : contacts) {
                    parts = contact.split("/");
                    arrayItem.add(new itemEvent(R.mipmap.ic_launcher, parts[0], parts[1], parts[2]));
                }
                adapter = new ListViewAdapter(this, arrayItem);
                lvContacts.setAdapter(adapter);
            }
        }
    }

    private void showDialog() {
        //para que se pueda mostrar más de una vez
        if (lvContacts.getParent() != null) {
            ((ViewGroup) lvContacts.getParent()).removeView(lvContacts);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateEvent.this);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", null);
        builder.setView(lvContacts);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String[] getInfoEditText() {
        String[] editTexts = new String[4];
        editTexts[0] = etNameE.getText().toString();
        editTexts[1] = etDescE.getText().toString();
        editTexts[2] = etDateE.getText().toString();
        editTexts[3] = etTimeE.getText().toString();

        return editTexts;
    }

    private void setIdEvent() {
        List<String> events = objDBH.selectEvent(nameE, descE, dateE + " " + timeE);
        if (events != null) {
            idEvent = Integer.parseInt(events.get(0));
        } else {
            idEvent = -1;
        }
    }
}
