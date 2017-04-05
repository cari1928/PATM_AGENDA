package com.example.radog.patm_agenda;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListEvents extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.lvEvents)
    ListView lvEvents;

    private ArrayList<itemEvent> arrayItem;
    private ListViewAdapter adapter = null;
    private CharSequence[] items;
    String selection;

    DBHelper objDBH;
    SQLiteDatabase BD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_events);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //para que se refresque la vista cuando volvemos a esta Activity
        objDBH = new DBHelper(this, "CURSO", null, 1);
        BD = objDBH.getWritableDatabase(); //open

        arrayItem = new ArrayList<>();
        cargarLista();

        registerForContextMenu(lvEvents);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opciones, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itmAddEvent:
                Intent intNewEvent = new Intent(this, NewEvent.class);
                startActivity(intNewEvent);
                break;
            case R.id.itmAbout:
                Intent intAbout = new Intent(this, About.class);
                startActivity(intAbout);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int id = v.getId();
        MenuInflater inflater = getMenuInflater();

        switch (id) {
            case R.id.lvEvents:
                inflater.inflate(R.menu.menu_events, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        itemEvent objItem = arrayItem.get(info.position);
        AlertDialog alert;

        switch (item.getItemId()) {
            case R.id.itmActUpd:
                Intent intNewEvent = new Intent(this, UpdateEvent.class);

                Bundle data = new Bundle();
                data.putString("NAME", objItem.getNameE());
                data.putString("DESC", objItem.getDescE());
                data.putString("DATE", objItem.getDateE());
                intNewEvent.putExtras(data);

                startActivity(intNewEvent);
                break;

            case R.id.itmActDel:
                builder.setMessage("Do you want to delete this event?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                long result;
                                itemEvent objItem;
                                List<String> event;
                                objDBH = new DBHelper(ListEvents.this, "CURSO", null, 1);
                                objDBH.openDB();

                                objItem = arrayItem.get(info.position);
                                event = objDBH.selectEvent(objItem.getNameE(), objItem.getDescE(), objItem.getDateE());
                                if (event != null) {
                                    //DELETE PEOPLE
                                    result = objDBH.deletePeople(Integer.parseInt(event.get(0)));
                                    if (result != -1) {
                                        //DELETE EVENT
                                        result = objDBH.deleteEvent(Integer.parseInt(event.get(0)));
                                        if (result != -1) {
                                            objDBH.closeDB();
                                            Toast.makeText(ListEvents.this, "Event successfuly deleted", Toast.LENGTH_SHORT).show();
                                            onStart();
                                        }
                                    }
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alert = builder.create();
                alert.setTitle("ALERT!!!");
                alert.show();
                break;

            case R.id.itmActCall:
                List<String> contacts = loadContactList(objItem);
                items = convertListToChar(contacts);

                builder.setTitle("Select contact").setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selection = (String) items[which]; //GET THE SELECTED ITEM
                    }
                }).setPositiveButton("Call", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] parts, parts2, parts3;
                        String number = "";

                        try {
                            parts = selection.split("/");
                            if (parts[1].contains("(")) {
                                parts2 = parts[1].split("\\(");
                                parts2 = parts2[1].split("\\)");
                                number += parts2[0];
                                parts2 = parts2[1].split("-");
                                number += parts2[0] + parts2[1];
                                parts = number.split(" ");
                                number = "";
                            } else {
                                parts = parts[1].split(" ");
                            }

                            for (int i = 0; i < parts.length; ++i) {
                                number += parts[i];
                            }

                            //CHECK NUMBER
                            //Toast.makeText(ListEvents.this, number, Toast.LENGTH_SHORT).show();
                            Intent objCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                            startActivity(objCall);
                        } catch (SecurityException e) {
                            Toast.makeText(ListEvents.this, e.toString(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(ListEvents.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alert = builder.create();
                alert.setTitle("Contacts");
                alert.show();

                break;
            case R.id.itmActSMS:
        }


        notifyAdapter(); //para que no haya problema al volver a seleccionar un item
        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, parent.getAdapter().getItem(position).toString(), Toast.LENGTH_SHORT).show();
    }

    private void cargarLista() {
        Cursor c = BD.rawQuery("SELECT * FROM event ORDER BY nameE", null);
        if (c.moveToFirst()) {
            do {
                arrayItem.add(new itemEvent(R.mipmap.ic_launcher, c.getString(1), c.getString(2), c.getString(3)));
            } while (c.moveToNext());
            adapter = new ListViewAdapter(this, arrayItem);
            lvEvents.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No existen registros", Toast.LENGTH_SHORT).show();
            //this.finish();
        }
    }

    private List<String> loadContactList(itemEvent objItem) {
        String[] parts;
        List<String> contacts, final_contacts = new ArrayList<>();
        //Load contacts list

        objDBH.openDB();

        //GET THE SELECTED EVENT
        List<String> event = objDBH.selectEvent(objItem.getNameE(), objItem.getDescE(), objItem.getDateE());
        if (event != null) {
            contacts = objDBH.selectContacts(Integer.parseInt(event.get(0)));
            if (contacts != null) {
                for (String contact : contacts) {
                    parts = contact.split("/");
                    arrayItem.add(new itemEvent(R.mipmap.ic_launcher, parts[0], parts[1], parts[2]));
                    final_contacts.add(parts[0] + " / " + parts[1]);
                }
            }
        }
        objDBH.closeDB();
        return final_contacts;
    }

    private CharSequence[] convertListToChar(List<String> contacts) {
        CharSequence[] items = new CharSequence[contacts.size()];
        for (int i = 0; i < contacts.size(); ++i) {
            items[i] = contacts.get(i);
        }

        return items;
    }

    private void notifyAdapter() {
        lvEvents.setAdapter(null);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            arrayItem = new ArrayList<>();
            BD = objDBH.getWritableDatabase(); //open
            cargarLista();
        }
    }

}
