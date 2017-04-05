package com.example.radog.patm_agenda;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListContacts extends AppCompatActivity {

    @BindView(R.id.lvContacts)
    ListView lvContacts;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> list_items = new ArrayList<>();
    private String nameE, desc, date, time, source;
    private int idEvent;

    DBHelper objDBH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);
        ButterKnife.bind(this);

        //conexión y apertura de la BD
        objDBH = new DBHelper(this, "CURSO", null, 1);
        objDBH.openDB();

        //INTENT DATA
        Bundle data = getIntent().getExtras();
        source = data.getString("SOURCE");
        nameE = data.getString("NAME");
        desc = data.getString("DESC");
        date = data.getString("DATE");
        time = data.getString("TIME");

        if (source.equals("UPDATE")) {
            idEvent = data.getInt("ID");
        }

        loadContacts();
    }

    @Override
    protected void onStop() {
        super.onStop();
        objDBH.closeDB();
    }

    public ArrayList<String> getContacts() {
        String name, id, email = null, contactNumber = null;
        ArrayList<String> alContacts = null;
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            alContacts = new ArrayList<String>();
            do {
                //Create a plain class with following variables - id, name, contactNumber, email
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);
                while (emails.moveToNext()) {
                    email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    break;
                }
                emails.close();

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        break;
                    }
                    pCur.close();
                }

                //alContacts.add(id + " " + name + " " + email + " " + contactNumber);
                alContacts.add(name + " / " + email + " / " + contactNumber);
                name = email = contactNumber = id = "Desconocido";

            } while (cursor.moveToNext());
        }

        cursor.close();
        return alContacts;
    }

    public void loadContacts() {
        list = getContacts();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lvContacts.setAdapter(adapter);

        //ALLOW MULTIPLE SELECTIONS
        lvContacts.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvContacts.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                //count += 1;
                if (!list_items.contains(list.get(position))) {
                    list_items.add(list.get(position));
                }
                mode.setTitle(list_items.size() + " actionContacts selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_contacts, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                long result;
                int idContact;
                String[] parts;
                List<String> people;

                switch (item.getItemId()) {
                    case R.id.miAddContacts:

                        //INSERT | UPDATE EVENT
                        result = actionEvents();
                        insertMessage(result);
                        if (result == -1) {
                            return false;
                        }

                        result = actionPeople();
                        if (result == -1) {
                            return false;
                        }

                        for (String msg : list_items) {
                            parts = msg.split("/");

                            //INSERT CONTACT, IF IT'S NECESSARY
                            result = actionContacts(parts[0], parts[1], parts[2]);
                            insertMessage(result);
                            if (result == -1) {
                                return false;
                            }
                            idContact = (int) result;

                            //INSERT PEOPLE
                            //THERE'S NO PROBLEM WITH UPDATE, OLD PEOPLE HAS BEEN DELETED
                            result = objDBH.insertPeople(idEvent, idContact);
                            insertMessage(result);
                            if (result == -1) {
                                return false;
                            }

                        }
                        Toast.makeText(ListContacts.this, "Information Saved", Toast.LENGTH_SHORT).show();
                        list_items = new ArrayList<String>();
                        mode.finish();
                        finish();
                        return true;
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }

    private void insertMessage(long result) {
        if (result == -1) {
            Toast.makeText(ListContacts.this, "Some error ocurred while inserting", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //checa si el contacto ya existe en la BD, de ser así, regresa el ID
    //De no ser así, se inserta
    private long actionContacts(String name, String pEmal, String pPhone) {
        long result;
        List<String> registers = objDBH.selectContact(name, pEmal, pPhone);
        String[] parts;

        if (registers == null) {
            result = objDBH.insertContact(name, pPhone, pEmal);
            //si falla, result será igual a -1
        } else {
            parts = registers.get(0).split("-");
            return Long.parseLong(parts[0]); //regresa el id del registro ya existente
        }

        return result;
    }

    private long actionEvents() {
        long result;
        if (source.equals("NEW")) {
            //INSERT EVENT
            result = objDBH.insertEvent(nameE, desc, date + " " + time);
            idEvent = (int) result;
        } else {
            //UPDATE
            //idEvent ya fue colocado previamente en onCreate
            result = objDBH.updateEvent(idEvent, nameE, desc, date + " " + time);
        }

        return result;
    }

    private long actionPeople() {
        List<String> people;
        long result;

        if (source.equals("UPDATE")) {
            //DELETE EXISTING PEOPLE
            return result = objDBH.deletePeople(idEvent); //elimina todos los contactos de ése evento
        } else {
            //CASE NEW
            return 1;
        }
    }
}
